package com.example.wildwaste.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildwaste.api.ApiService
import com.example.wildwaste.api.GenericResponse
import com.example.wildwaste.api.RetrofitInstance
import com.example.wildwaste.api.TrashReport
import com.example.wildwaste.api.TrashReportRequest
import com.google.gson.Gson
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
                    var errorMessage = ""
                    if (!response.isSuccessful) {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                // Use the existing GenericResponse model to parse the error
                                val errorResponse = Gson().fromJson(errorBody, GenericResponse::class.java)
                                errorMessage = errorResponse.message
                            } catch (e: Exception) {
                                // Parsing failed, use the default message
                                errorMessage = "Failed to fetch reports"
                            }
                        }
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
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