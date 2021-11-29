package io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle

import com.github.funczz.kotlin.fsm.FsmState
import com.github.funczz.kotlin.fsm.NextState

object OnToggleState : FsmState<ToggleEvent, ToggleContext> {
    override fun getNextState(event: ToggleEvent, context: ToggleContext): NextState {
        return when (event) {
            is ToggleEvent.TurnOn -> NextState.Deny
            is ToggleEvent.TurnOff -> NextState.External(OffToggleState)
            is ToggleEvent.Ignore -> NextState.Ignore
            is ToggleEvent.Internal -> NextState.Internal
        }
    }

    override fun onEntry(event: ToggleEvent, context: ToggleContext): ToggleContext {
        context.isOn = true
        context.count = 0
        return context
    }

    override fun onDo(event: ToggleEvent, context: ToggleContext): ToggleContext {
        context.count += 1
        return context
    }

    override fun onExit(event: ToggleEvent, context: ToggleContext): ToggleContext {
        return context
    }

}