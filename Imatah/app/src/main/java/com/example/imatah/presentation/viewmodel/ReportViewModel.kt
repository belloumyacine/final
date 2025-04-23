package com.example.imatah.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.model.Report
import com.example.imatah.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ReportViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getReports()
                .onSuccess { _reports.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun addReport(report: Report) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addReport(report)
                .onSuccess { loadReports() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateReport(report: Report) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateReport(report)
                .onSuccess { loadReports() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteReport(id: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteReport(id)
                .onSuccess { loadReports() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun searchReports(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchReports(query)
                .onSuccess { _reports.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun filterReportsByStatus(status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.filterReportsByStatus(status)
                .onSuccess { _reports.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
