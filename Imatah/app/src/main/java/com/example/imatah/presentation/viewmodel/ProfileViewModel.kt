package com.example.imatah.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.model.Profile
import com.example.imatah.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProfile(userId: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProfile(userId)
                .onSuccess { _profile.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCurrentUserProfile()
                .onSuccess { _profile.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateProfile(profile)
                .onSuccess { _profile.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
} 