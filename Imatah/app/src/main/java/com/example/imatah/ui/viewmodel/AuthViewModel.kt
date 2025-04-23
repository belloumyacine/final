package com.example.imatah.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.model.User
import com.example.imatah.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { result ->
                result.onSuccess { user ->
                    _currentUser.value = user
                }.onFailure { e ->
                    _error.value = e.message
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signUp(email, password)
                .onSuccess {
                    loadCurrentUser()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signIn(email, password)
                .onSuccess {
                    loadCurrentUser()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signOut()
                .onSuccess {
                    _currentUser.value = null
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.resetPassword(email)
                .onSuccess {
                    _error.value = "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني"
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun updateProfile(name: String?, avatarUrl: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateProfile(name, avatarUrl)
                .onSuccess { user ->
                    _currentUser.value = user
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
} 