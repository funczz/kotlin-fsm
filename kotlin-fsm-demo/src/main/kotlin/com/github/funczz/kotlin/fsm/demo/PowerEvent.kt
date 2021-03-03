package com.github.funczz.kotlin.fsm.demo

/**
 * ステートマシンのイベント
 * @author funczz
 */
sealed class PowerEvent {

    /**
     * OFF
     */
    object OFF : PowerEvent()

    /**
     * ON
     */
    object ON : PowerEvent()

}