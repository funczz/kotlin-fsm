package com.github.funczz.kotlin.fsm.reentrant

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class FsmStateTest : StringSpec() {

    /**
     * FSM Context
     */
    class FsmContext

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
        init {
            permit(InternalEvent::class.java)
            permit(eventType = NextEvent::class.java, NextState)
            ignore(eventType = NoopEvent::class.java)
        }
    }

    /**
     * FSM State: NextState
     */
    object NextState : FsmState<FsmContext>() {
        init {
            permit(eventType = NextEvent::class.java, NextState)
            ignore(eventType = NoopEvent::class.java)
        }
    }

    init {

        "FirstState::canFire" {
            FirstState.apply {
                canFire(eventType = InternalEvent::class.java) shouldBe true
                canFire(eventType = NextEvent::class.java) shouldBe true
                canFire(eventType = NoopEvent::class.java) shouldBe true
                canFire(eventType = NotRegisteredEvent::class.java) shouldBe false
            }
        }

        "FirstState::isIgnoreFire" {
            FirstState.apply {
                isIgnoreFire(eventType = NextEvent::class.java) shouldBe false
                isIgnoreFire(eventType = NoopEvent::class.java) shouldBe true
                isIgnoreFire(eventType = NotRegisteredEvent::class.java) shouldBe false
            }
        }

        "FirstState::fire" {
            FirstState.apply {
                fire(event = InternalEvent()) shouldBe null
                fire(event = NextEvent()) shouldBe NextState
                fire(event = NoopEvent()) shouldBe null
                fire(event = NotRegisteredEvent()) shouldBe null
            }
        }

        "NextState::canFire" {
            NextState.apply {
                canFire(eventType = NextEvent::class.java) shouldBe true
                canFire(eventType = NoopEvent::class.java) shouldBe true
                canFire(eventType = NotRegisteredEvent::class.java) shouldBe false
            }
        }

        "NextState::isIgnoreFire" {
            NextState.apply {
                isIgnoreFire(eventType = NextEvent::class.java) shouldBe false
                isIgnoreFire(eventType = NoopEvent::class.java) shouldBe true
                isIgnoreFire(eventType = NotRegisteredEvent::class.java) shouldBe false
            }
        }

        "NextState::fire" {
            NextState.apply {
                fire(event = NextEvent()) shouldBe NextState
                fire(event = NoopEvent()) shouldBe null
                fire(event = NotRegisteredEvent()) shouldBe null
            }
        }

    }

}