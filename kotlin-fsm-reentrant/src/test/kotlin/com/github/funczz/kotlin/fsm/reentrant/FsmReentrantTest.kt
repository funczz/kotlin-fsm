package com.github.funczz.kotlin.fsm.reentrant

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class FsmReentrantTest : StringSpec() {

    /**
     * FSM Context
     */
    data class FsmContext(var value: MutableList<String> = mutableListOf())

    /**
     * FSM Event
     */
    interface FsmEvent
    class InternalEvent : FsmEvent
    class NextEvent : FsmEvent
    class NoopEvent : FsmEvent
    class NotRegisteredEvent : FsmEvent

    /**
     * FSM State: FirstState
     */
    object FirstState : FsmState<FsmContext>() {
        private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

        init {
            onEntry { event, context ->
                logger.debug("start entry action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
                context.value.add("ENTRY: ${this::class.java.simpleName}")

            }
            onDo { event, context ->
                logger.debug("start do action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
                context.value.add("DO: ${this::class.java.simpleName}")
            }
            onExit { event, context ->
                logger.debug("start exit action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
                context.value.add("EXIT: ${this::class.java.simpleName}")
            }
            permit(eventType = InternalEvent::class.java)
            permit(eventType = NextEvent::class.java, NextState)
        }
    }

    /**
     * FSM State: NextState
     */
    object NextState : FsmState<FsmContext>() {
        private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

        init {
            onEntry { event, context ->
                logger.debug("start entry action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
                context.value.add("ENTRY: ${this::class.java.simpleName}")

            }
            onDo { event, context ->
                logger.debug("start do action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
                context.value.add("DO: ${this::class.java.simpleName}")
            }
            onExit { event, context ->
                logger.debug("start exit action. event=${event.javaClass.simpleName}, state=${this.javaClass.simpleName}")
                context.value.add("EXIT: ${this::class.java.simpleName}")
            }
            permit(eventType = NextEvent::class.java)
            ignore(eventType = NoopEvent::class.java)
        }
    }

    /**
     * ExecutorService に新規のスレッドを投入する拡張メソッド
     * @param delay 現在から遅延実行までの時間(ミリ秒)
     * @param function スレッドで実行するファンクション
     */
    private fun ExecutorService.run(delay: Long = 1L, function: () -> Unit) {
        Thread.sleep(delay)
        this.execute {
            thread {
                function()
            }
        }
    }

    init {

        "排他制御" {
            val fsm = Fsm(initialState = FirstState, context = FsmContext())
            val executor = Executors.newCachedThreadPool()
            fsm.context().value.size shouldBe 0
            executor.run {
                fsm.state() shouldBe FirstState
            }
            executor.run {
                fsm.fire(event = InternalEvent())
            }
            executor.run {
                fsm.state() shouldBe FirstState
            }
            executor.run {
                fsm.fire(event = NextEvent())
            }
            executor.run {
                fsm.state() shouldBe NextState
            }
            executor.run {
                fsm.fire(event = NextEvent())
            }
            executor.run {
                fsm.state() shouldBe NextState
            }
            executor.run {
                fsm.fire(event = NoopEvent())
            }
            executor.run {
                fsm.state() shouldBe NextState
            }
            executor.awaitTermination(100, TimeUnit.MILLISECONDS)

            fsm.context().value.apply {
                size shouldBe 5
                get(0) shouldBe "DO: FirstState"
                get(1) shouldBe "EXIT: FirstState"
                get(2) shouldBe "ENTRY: NextState"
                get(3) shouldBe "DO: NextState"
                get(4) shouldBe "DO: NextState"
            }

        }

        "transaction" {
            val fsm = Fsm(initialState = FirstState, context = FsmContext())
            val executor = Executors.newCachedThreadPool()
            fsm.context().value.size shouldBe 0
            executor.run {
                fsm.state() shouldBe FirstState
            }
            executor.run {
                fsm.transaction {
                    it.fire(event = InternalEvent())
                    it.fire(event = NextEvent())
                    it.fire(event = NextEvent())
                    it.fire(event = NoopEvent())
                }
            }
            executor.run {
                fsm.state() shouldBe NextState
            }
            executor.awaitTermination(100, TimeUnit.MILLISECONDS)

            fsm.context().value.apply {
                size shouldBe 5
                get(0) shouldBe "DO: FirstState"
                get(1) shouldBe "EXIT: FirstState"
                get(2) shouldBe "ENTRY: NextState"
                get(3) shouldBe "DO: NextState"
                get(4) shouldBe "DO: NextState"
            }

        }

    }

}