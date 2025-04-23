package com.example.imatah.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imatah.data.model.Report
import com.example.imatah.data.model.User
import com.example.imatah.data.repository.AuthRepository
import com.example.imatah.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentSearchQuery = ""
    private var currentFilterStatus = "الكل"
    private var currentUser: User? = null

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                result.onSuccess { user ->
                    currentUser = user
                    loadReports()
                }.onFailure { e ->
                    _error.value = e.message
                }
            }
        }
    }

    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            reportRepository.getReports()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { result ->
                    result.onSuccess { reports ->
                        _reports.value = reports
                    }.onFailure { e ->
                        _error.value = e.message
                    }
                    _isLoading.value = false
                }
        }
    }

    fun addReport(report: Report) {
        if (currentUser == null) {
            _error.value = "يجب تسجيل الدخول أولاً"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val newReport = report.copy(userId = currentUser!!.id)
            reportRepository.addReport(newReport)
                .onSuccess {
                    loadReports()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun updateReport(report: Report) {
        if (currentUser == null) {
            _error.value = "يجب تسجيل الدخول أولاً"
            return
        }

        if (report.userId != currentUser!!.id) {
            _error.value = "لا يمكنك تعديل تقارير الآخرين"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            reportRepository.updateReport(report)
                .onSuccess {
                    loadReports()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun deleteReport(id: UUID) {
        if (currentUser == null) {
            _error.value = "يجب تسجيل الدخول أولاً"
            return
        }

        val report = _reports.value.find { it.id == id }
        if (report == null) {
            _error.value = "التقرير غير موجود"
            return
        }

        if (report.userId != currentUser!!.id) {
            _error.value = "لا يمكنك حذف تقارير الآخرين"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            reportRepository.deleteReport(id)
                .onSuccess {
                    loadReports()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun searchReports(query: String) {
        currentSearchQuery = query
        viewModelScope.launch {
            _isLoading.value = true
            reportRepository.searchAndFilterReports(currentSearchQuery, currentFilterStatus)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { result ->
                    result.onSuccess { reports ->
                        _reports.value = reports
                    }.onFailure { e ->
                        _error.value = e.message
                    }
                    _isLoading.value = false
                }
        }
    }

    fun filterReportsByStatus(status: String) {
        currentFilterStatus = status
        viewModelScope.launch {
            _isLoading.value = true
            reportRepository.searchAndFilterReports(currentSearchQuery, currentFilterStatus)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { result ->
                    result.onSuccess { reports ->
                        _reports.value = reports
                    }.onFailure { e ->
                        _error.value = e.message
                    }
                    _isLoading.value = false
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSearch() {
        currentSearchQuery = ""
        currentFilterStatus = "الكل"
        loadReports()
    }

    fun canEditReport(report: Report): Boolean {
        return currentUser != null && report.userId == currentUser!!.id
    }
} 