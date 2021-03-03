package com.github.funczz.kotlin.fsm.reentrant

import com.github.funczz.kotlin.fsm.IFsm
import com.github.funczz.kotlin.fsm.IFsmState
import com.github.funczz.kotlin.fsm.NotRegisteredEventFsmException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * ステートマシン <code>IFsm<T></code> のスレッドセーフ版実装
 *
 * @author funczz
 */
open class Fsm<T : Any>(
        /**
         * ステートマシンの初期状態
         */
        initialState: IFsmState<T>,

        /**
         * 遷移毎に処理されるオブジェクト
         */
        private val context: T,
) : IFsm<T> {

    /**
     * ステートマシンの状態
     */
    private var state: IFsmState<T> = initialState

    /**
     * 排他 Lock
     */
    private val lock = ReentrantLock(true)

    /**
     * ファンクションを排他 Lock <code>lock</code> による排他制御下で実行する
     * 非公開メソッド
     * @param function 実行するファンクション
     * @return ファンクションの実行結果を返す
     */
    private inline fun <R> withLock(function: () -> R): R {
        return lock.withLock(function)
    }

    /**
     * ファンクションを排他 Lock <code>lock</code> による排他制御下で実行する
     * 公開メソッド
     * @param function 実行するファンクション
     */
    fun transaction(function: (Fsm<T>) -> Unit) {
        withLock {
            function(this)
        }
    }

    override fun canFire(eventType: Class<*>): Boolean {
        return withLock {
            state.canFire(eventType = eventType)
        }
    }

    override fun fire(event: Any) {
        withLock {
            //イベントが未登録の場合は例外エラー
            if (!state.canFire(event::class.java))
                throw NotRegisteredEventFsmException("not registered event. event=$event, state=$state")

            //無視するイベントに登録されている場合は何もせずに処理を戻す
            if (state.isIgnoreFire(event::class.java)) return

            when (val nextState = state.fire(event)) {
                //遷移先の状態がない場合は内部遷移
                null -> {
                }
                //それ以外は外部遷移
                else -> {
                    state.onExit(event, context)
                    state = nextState
                    state.onEntry(event, context)
                }
            }
            state.onDo(event, context)
        }
    }

    override fun state(): IFsmState<T> {
        return withLock {
            state
        }
    }

    override fun context(): T {
        return withLock {
            context
        }
    }

}
