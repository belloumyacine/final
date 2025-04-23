package com.example.imatah.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.model.Comment
import com.example.imatah.data.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CommentViewModel(
    private val repository: CommentRepository
) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadComments(reportId: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getComments(reportId)
                .onSuccess { _comments.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun addComment(comment: Comment) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addComment(comment)
                .onSuccess { loadComments(comment.reportId) }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateComment(comment: Comment) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateComment(comment)
                .onSuccess { loadComments(comment.reportId) }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteComment(id: UUID, reportId: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteComment(id)
                .onSuccess { loadComments(reportId) }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
} 