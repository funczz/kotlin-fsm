package com.github.funczz.kotlin.fsm.simple

import com.github.funczz.kotlin.fsm.NotRegisteredEventFsmException
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FsmTest : StringSpec() {

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

    init {

        "canFire" {
            val fsm = Fsm(initialState = FirstState, context = FsmContext())
            fsm.canFire(InternalEvent::class.java) shouldBe true
            fsm.canFire(NextEvent::class.java) shouldBe true
            fsm.canFire(NoopEvent::class.java) shouldBe false
        }

        "fire" {
            val fsm = Fsm(initialState = FirstState, context = FsmContext())
            fsm.context().value.size shouldBe 0
            fsm.state() shouldBe FirstState
            fsm.fire(event = InternalEvent())
            fsm.state() shouldBe FirstState
            fsm.fire(event = NextEvent())
            fsm.state() shouldBe NextState
            fsm.fire(event = NextEvent())
            fsm.state() shouldBe NextState
            fsm.fire(event = NoopEvent())
            fsm.state() shouldBe NextState
            fsm.context().value.apply {
                size shouldBe 5
                get(0) shouldBe "DO: FirstState"
                get(1) shouldBe "EXIT: FirstState"
                get(2) shouldBe "ENTRY: NextState"
                get(3) shouldBe "DO: NextState"
                get(4) shouldBe "DO: NextState"
            }
        }

        "未登録イベントを発火する" {
            val fsm = Fsm(initialState = FirstState, context = FsmContext())

            shouldThrow<NotRegisteredEventFsmException> {
                fsm.fire(event = NotRegisteredEvent())
            }.message shouldStartWith "not registered event. event="
            fsm.state() shouldBe FirstState

            shouldThrow<NotRegisteredEventFsmException> {
                fsm.fire(event = NoopEvent())
            }.message shouldStartWith "not registered event. event="
            fsm.state() shouldBe FirstState
        }

    }

}