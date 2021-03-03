package com.github.funczz.kotlin.fsm.simple

import com.github.funczz.kotlin.fsm.IFsm
import com.github.funczz.kotlin.fsm.IFsmState
import com.github.funczz.kotlin.fsm.NotRegisteredEventFsmException

/**
 * ステートマシン <code>IFsm<T></code> の実装
 * @author funczz
 */
open class Fsm<T : Any>(
        /**
         * ステートマシンの初期状態
         */
        initialState: IFsmState<T>,

        /**
         * 遷移により処理されるオブジェクト
         */
        private val context: T,
) : IFsm<T> {

    /**
     * ステートマシンの状態
     */
    private var state: IFsmState<T> = initialState

    override fun canFire(eventType: Class<*>): Boolean {
        return state.canFire(eventType = eventType)
    }

    override fun fire(event: Any) {
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

    override fun state(): IFsmState<T> {
        return state
    }

    override fun context(): T {
        return context
    }

}
