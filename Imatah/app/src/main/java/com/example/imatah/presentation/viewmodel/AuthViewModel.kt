package com.example.imatah.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(repository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signUp(email, password)
                .onSuccess { _isLoggedIn.value = true }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signIn(email, password)
                .onSuccess { _isLoggedIn.value = true }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signOut()
                .onSuccess { _isLoggedIn.value = false }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.resetPassword(email)
                .onSuccess { _error.value = "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني" }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
} 