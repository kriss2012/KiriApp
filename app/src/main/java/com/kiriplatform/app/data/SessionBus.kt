package com.kiriplatform.app.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionBus {
    private val _events = MutableSharedFlow<SessionEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: SessionEvent) {
        _events.emit(event)
    }
}

sealed class SessionEvent {
    object Logout : SessionEvent()
    object NotificationReceived : SessionEvent()
}
