package com.example.imatah.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.model.Category
import com.example.imatah.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCategories()
                .onSuccess { _categories.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun loadCategory(id: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCategory(id)
                .onSuccess { _selectedCategory.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addCategory(category)
                .onSuccess { loadCategories() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateCategory(category)
                .onSuccess { loadCategories() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteCategory(id: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteCategory(id)
                .onSuccess { loadCategories() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
