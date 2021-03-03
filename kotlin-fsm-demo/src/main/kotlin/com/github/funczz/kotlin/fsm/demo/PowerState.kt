package com.github.funczz.kotlin.fsm.demo

import com.github.funczz.kotlin.fsm.simple.FsmState
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * ステートマシンの状態
 * @author funczz
 */
sealed class PowerState : FsmState<PowerContext>() {

    val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    /**
     * Stopped
     */
    object StoppedPowerState : PowerState() {

        init {
            onEntry { event, _ ->
                logger.info("start entry action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")

            }
            onDo { event, _ ->
                logger.info("start do action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
            }
            onExit { event, _ ->
                logger.info("start exit action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
            }
            permit(eventType = PowerEvent.OFF::class.java) //内部遷移
            permit(eventType = PowerEvent.ON::class.java, RunningPowerState)
        }

    }

    /**
     * Running
     */
    object RunningPowerState : PowerState() {

        init {
            onEntry { event, _ ->
                logger.info("start entry action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")

            }
            onDo { event, _ ->
                logger.info("start do action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
            }
            onExit { event, _ ->
                logger.info("start exit action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
            }
            permit(eventType = PowerEvent.OFF::class.java, StoppedPowerState)
            permit(eventType = PowerEvent.ON::class.java, this) //外部遷移
        }

    }

}