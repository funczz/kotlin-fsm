package io.kotlintest.provided.com.github.funczz.kotlin.fsm.toggle

sealed class ToggleEvent {

    object TurnOn : ToggleEvent()

    object TurnOff : ToggleEvent()

    object Ignore : ToggleEvent()

    object Internal : ToggleEvent()

}