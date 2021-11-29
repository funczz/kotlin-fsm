package io.kotlintest.provided.com.github.funczz.kotlin.fsm

import io.kotlintest.TestCase
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.OffToggleState
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.OnToggleState
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.ToggleContext
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.ToggleEvent
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class OffToggleFsmStateTest : StringSpec() {

    private lateinit var offContext: ToggleContext

    private lateinit var onContext: ToggleContext

    override fun beforeTest(testCase: TestCase) {
        offContext = ToggleContext()
        onContext = ToggleContext(true, 2)
        super.beforeTest(testCase)
    }

    init {

        "Off --TurnOn--> On" {
            val (state, actual) = OffToggleState.fire(ToggleEvent.TurnOn, offContext)
            state::class shouldBe OnToggleState::class
            actual.also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 1
            }
            offContext.count shouldBe 1
        }

        "Off --TurnOff--> failure" {
            val actual = shouldThrow<Exception> {
                OffToggleState.fire(ToggleEvent.TurnOff, offContext)
            }
            actual.message shouldStartWith "Event not allowed: "
            offContext.count shouldBe 0
        }

        "Off --Ignore--> Off" {
            val (state, actual) = OffToggleState.fire(ToggleEvent.Ignore, offContext)
            state::class shouldBe OffToggleState::class
            actual.also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 0
            }
            offContext.count shouldBe 0
        }

        "Off --Internal--> Off" {
            val (state, actual) = OffToggleState.fire(ToggleEvent.Internal, offContext)
            state::class shouldBe OffToggleState::class
            actual.also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 1
            }
            offContext.count shouldBe 1
        }

    }
}