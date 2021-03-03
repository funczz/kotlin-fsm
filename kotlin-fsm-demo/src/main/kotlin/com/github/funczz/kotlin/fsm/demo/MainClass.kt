package com.github.funczz.kotlin.fsm.demo

import com.github.funczz.kotlin.fsm.simple.Fsm
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * kotlin-fsm デモアプリ
 * @author funczz
 */
class MainClass {

    private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    fun invoke() {
        val fsm = Fsm(
                initialState = PowerState.StoppedPowerState,
                context = PowerContext()
        )

        var event: PowerEvent = PowerEvent.OFF
        logger.info("[{}] fire -> {}", fsm.state()::class.java.simpleName, event::class.java.simpleName)
        fsm.fire(event)

        event = PowerEvent.ON
        logger.info("[{}] fire -> {}", fsm.state()::class.java.simpleName, event::class.java.simpleName)
        fsm.fire(event)

        logger.info("[{}] fire -> {}", fsm.state()::class.java.simpleName, event::class.java.simpleName)
        fsm.fire(event)

        event = PowerEvent.OFF
        logger.info("[{}] fire -> {}", fsm.state()::class.java.simpleName, event::class.java.simpleName)
        fsm.fire(event)
    }

    companion object {

        /**
         * main メソッド
         */
        @JvmStatic
        fun main(args: Array<String>) {
            MainClass().invoke()
        }

    }

}