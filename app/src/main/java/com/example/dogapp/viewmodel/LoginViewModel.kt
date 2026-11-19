package com.example.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface LoginUiEvent {
    data object RequestBiometricPrompt : LoginUiEvent
    data object NavigateToHome : LoginUiEvent
}

/** Mantiene el estado y los eventos del flujo de acceso fuera del Fragment. */
class LoginViewModel : ViewModel() {
    private val eventChannel = Channel<LoginUiEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val _statusMessage = MutableLiveData<String?>(null)
    val statusMessage: LiveData<String?> = _statusMessage

    fun onFingerprintTapped() {
        _statusMessage.value = null
        emit(LoginUiEvent.RequestBiometricPrompt)
    }

    fun onBiometricAuthenticated() {
        _statusMessage.value = null
        emit(LoginUiEvent.NavigateToHome)
    }

    fun showStatusMessage(message: String) {
        _statusMessage.value = message
    }

    private fun emit(event: LoginUiEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}
