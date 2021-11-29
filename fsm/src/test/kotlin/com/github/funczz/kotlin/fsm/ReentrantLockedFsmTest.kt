package io.kotlintest.provided.com.github.funczz.kotlin.fsm

import com.github.funczz.kotlin.fsm.ReentrantLockedFsm
import io.kotlintest.TestCase
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.OffToggleState
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.OnToggleState
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.ToggleContext
import io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle.ToggleEvent
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class ReentrantLockedFsmTest : StringSpec() {

    private lateinit var fsm: ReentrantLockedFsm<ToggleEvent, ToggleContext>


    override fun beforeTest(testCase: TestCase) {
        fsm = ReentrantLockedFsm(state = OffToggleState, context = ToggleContext())
        super.beforeTest(testCase)
    }

    init {
        "Off --TurnOn--> On" {
            fsm.fire(ToggleEvent.TurnOn)
            fsm.state::class shouldBe OnToggleState::class
            fsm.context.also { ctx ->
                ctx.isOn shouldBe true
                ctx.count shouldBe 1
            }
        }

        "Off --TurnOff--> failure" {
            val actual = shouldThrow<Exception> {
                fsm.fire(ToggleEvent.TurnOff)
            }
            actual.message shouldStartWith "Event not allowed: "
            fsm.context.count shouldBe 0
        }

        "Off --Ignore--> Off" {
            fsm.fire(ToggleEvent.Ignore)
            fsm.state::class shouldBe OffToggleState::class
            fsm.context.also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 0
            }
        }

        "Off --Internal--> Off" {
            fsm.fire(ToggleEvent.Internal)
            fsm.state::class shouldBe OffToggleState::class
            fsm.context.also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 1
            }
        }

        "Off --TurnOn--> On --TurnOff-> Off" {
            fsm.fire(ToggleEvent.TurnOn)
            fsm.fire(ToggleEvent.TurnOff)
            fsm.state::class shouldBe OffToggleState::class
            fsm.context.also { ctx ->
                ctx.isOn shouldBe false
                ctx.count shouldBe 1
            }
        }

    }

}