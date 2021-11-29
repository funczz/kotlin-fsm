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

class OnToggleFsmStateTest : StringSpec() {

    private lateinit var offContext: ToggleContext

    private lateinit var onContext: ToggleContext

    override fun beforeTest(testCase: TestCase) {
        offContext = ToggleContext()
        onContext = ToggleContext(true, 2)
        super.beforeTest(testCase)
    }

    init {
        "On --TurnOff--> Off" {
            val (state, actual) = OnToggleState.fire(ToggleEvent.TurnOff, onContext)
            state::class shouldBe OffToggleState::class
            actual.also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 1
            }
            onContext.count shouldBe 1
        }

        "On --TurnOn--> failure" {
            val actual = shouldThrow<Exception> {
                OnToggleState.fire(ToggleEvent.TurnOn, onContext)
            }
            actual.message shouldStartWith "Event not allowed: "
            onContext.count shouldBe 2
        }

        "On --Ignore--> On" {
            val (state, actual) = OnToggleState.fire(ToggleEvent.Ignore, onContext)
            state::class shouldBe OnToggleState::class
            actual.also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 2
            }
            onContext.count shouldBe 2
        }

        "On --Internal--> On" {
            val (state, actual) = OnToggleState.fire(ToggleEvent.Internal, onContext)
            state::class shouldBe OnToggleState::class
            actual.also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 3
            }
            onContext.count shouldBe 3
        }

    }

}