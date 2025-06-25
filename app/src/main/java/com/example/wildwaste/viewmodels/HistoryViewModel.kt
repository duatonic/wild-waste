package com.example.wildwaste.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildwaste.api.ApiService
import com.example.wildwaste.api.GenericResponse
import com.example.wildwaste.api.RetrofitInstance
import com.example.wildwaste.api.TrashReport
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val reports: List<TrashReport> = emptyList(),
    val error: String? = null,
    val deletionSuccess: Boolean = false
)

class HistoryViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitInstance.api
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    fun fetchUserReports(userId: Int) {
        viewModelScope.launch {
            _uiState.value = HistoryUiState(isLoading = true)
            try {
                val response = apiService.getUserReports(userId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = HistoryUiState(reports = response.body()?.data ?: emptyList())
                    Log.d("HistoryViewModel", "Reports fetched successfully")
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
                                errorMessage = "Failed to fetch history"
                            }
                        }
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState(error = "Connection error: ${e.message}")
            }
        }
    }

    fun deleteReport(reportId: Int, userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, deletionSuccess = false)
            try {
                val response = apiService.deleteReport(reportId)
                if(response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = _uiState.value.copy(isLoading = false, deletionSuccess = true)
                    // Refresh the list after successful deletion
                    fetchUserReports(userId)
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to delete report"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Connection error: ${e.message}")
            }
        }
    }

    fun consumedDeletionEvent() {
        _uiState.value = _uiState.value.copy(deletionSuccess = false, error = null)
    }
}