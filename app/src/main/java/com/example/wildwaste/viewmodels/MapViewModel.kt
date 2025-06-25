package com.example.wildwaste.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildwaste.api.ApiService
import com.example.wildwaste.api.RetrofitInstance
import com.example.wildwaste.api.TrashReport
import com.example.wildwaste.api.TrashReportRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = false,
    val reports: List<TrashReport> = emptyList(),
    val error: String? = null,
    val submissionSuccess: Boolean = false
)

class MapViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitInstance.api
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        fetchAllReports()
    }

    fun fetchAllReports() {
        viewModelScope.launch {
            _uiState.value = MapUiState(isLoading = true)
            try {
                val response = apiService.getAllReports()
                if (response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reports = response.body()?.data ?: emptyList()
                    )
                } else {
                    // IMPORTANT THIS WAS SUPPOSED TO BE response.body()?.message but message is not in getAllReports Data Model (AllReportsResponse)
                    val errorMessage = response.body()?.data ?: "Failed to fetch reports"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to fetch reports")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Connection error: ${e.message}")
            }
        }
    }

    fun submitReport(reportRequest: TrashReportRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, submissionSuccess = false)
            try {
                val response = apiService.submitReport(reportRequest)
                if (response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = _uiState.value.copy(isLoading = false, submissionSuccess = true)
                    // Refresh the reports on the map
                    fetchAllReports()
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to submit report"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Connection error: ${e.message}")
            }
        }
    }

    fun consumedSubmissionEvent() {
        _uiState.value = _uiState.value.copy(submissionSuccess = false, error = null)
    }
}