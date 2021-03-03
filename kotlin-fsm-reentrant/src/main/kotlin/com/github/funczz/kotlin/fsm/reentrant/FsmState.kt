package com.github.funczz.kotlin.fsm.reentrant

import com.github.funczz.kotlin.fsm.IFsmState
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ステートマシンの状態 IFsmState の実装
 * @author funczz
 */
open class FsmState<T : Any> : IFsmState<T> {

    /**
     * アクション種別
     */
    private enum class Action { ENTRY, DO, EXIT }

    /**
     * 遷移情報
     * @param state 遷移先の状態
     * @param internal 内部遷移は <code>true</code> 、外部遷移は <code>false</code>
     */
    private data class Transition<T : Any>(val state: IFsmState<T>, val internal: Boolean)

    /**
     * 発火するイベントと遷移情報を登録する
     * key: イベントの Class,
     * value: 遷移情報
     */
    private val permitEvents: ConcurrentHashMap<Class<*>, Transition<T>> = ConcurrentHashMap()

    /**
     * 無視するイベントを登録する
     * element: イベントの Class
     */
    private val ignoreEvents: CopyOnWriteArraySet<Class<*>> = CopyOnWriteArraySet()

    /**
     * 遷移時に処理するアクションを登録する
     * key: アクション種別,
     * value: イベント、コンテキストを引数とするファンクション
     */
    private val actions: ConcurrentHashMap<Action, ((event: Any, context: T) -> Unit)?> = ConcurrentHashMap()

    override fun onEntry(function: (event: Any, context: T) -> Unit) {
        actions[Action.ENTRY] = function
    }

    override fun onDo(function: (event: Any, context: T) -> Unit) {
        actions[Action.DO] = function
    }

    override fun onExit(function: (event: Any, context: T) -> Unit) {
        actions[Action.EXIT] = function
    }

    override fun onEntry(event: Any, context: T) {
        actions[Action.ENTRY]?.let { it(event, context) }
    }

    override fun onDo(event: Any, context: T) {
        actions[Action.DO]?.let { it(event, context) }
    }

    override fun onExit(event: Any, context: T) {
        actions[Action.EXIT]?.let { it(event, context) }
    }

    override fun permit(eventType: Class<*>) {
        permitEvents[eventType] = Transition(state = this, internal = true)
    }

    override fun permit(eventType: Class<*>, state: IFsmState<T>) {
        permitEvents[eventType] = Transition(state = state, internal = false)
    }

    override fun ignore(eventType: Class<*>) {
        ignoreEvents.add(eventType)
    }

    override fun canFire(eventType: Class<*>): Boolean {
        return permitEvents.containsKey(eventType) || ignoreEvents.contains(eventType)
    }

    override fun isIgnoreFire(eventType: Class<*>): Boolean {
        return ignoreEvents.contains(eventType)
    }

    override fun fire(event: Any): IFsmState<T>? {
        return permitEvents[event::class.java]?.let {
            when (it.internal) {
                true -> null
                else -> it.state
            }
        }
    }

}