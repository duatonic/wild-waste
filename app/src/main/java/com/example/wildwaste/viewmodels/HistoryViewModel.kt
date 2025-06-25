package com.example.wildwaste.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildwaste.api.ApiService
import com.example.wildwaste.api.RetrofitInstance
import com.example.wildwaste.api.TrashReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val reports: List<TrashReport> = emptyList(),
    val error: String? = null
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
                } else {
                    // IMPORTANT THIS WAS SUPPOSED TO BE response.body()?.message but message is not in getUserReports Data Model (AllReportsResponse)
                    val errorMessage = response.body()?.data ?: "Failed to fetch history"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to fetch history")
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState(error = "Connection error: ${e.message}")
            }
        }
    }
}