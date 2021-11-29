package com.github.funczz.kotlin.fsm

/**
 * 状態遷移の種別
 * @author funczz
 * @version 1.0.0
 */
sealed interface NextState {

    /**
     * 外部遷移
     */
    data class External(
        /** 遷移先のステート */
        val state: FsmState<*, *>
    ) : NextState

    /**
     * 内部遷移
     */
    object Internal : NextState

    /**
     * 無視
     */
    object Ignore : NextState

    /**
     * 拒否
     */
    object Deny : NextState

}