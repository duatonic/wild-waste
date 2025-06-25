package com.example.wildwaste.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildwaste.api.ApiService
import com.example.wildwaste.api.RetrofitInstance
import com.example.wildwaste.api.UserRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Represents the state of the authentication UI
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val loginSuccess: Boolean = false,
    // THE FIX IS APPLIED HERE:
    val loggedInUserId: Int? = null // Changed 'nil' to 'null'
)

class AuthViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitInstance.api

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val response = apiService.loginUser(UserRequest(username, password))
                if (response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = AuthUiState(
                        loginSuccess = true,
                        loggedInUserId = response.body()?.userId
                    )
                } else {
                    val errorMessage = response.body()?.message ?: "An unknown error occurred"
                    _uiState.value = AuthUiState(error = errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Could not connect to server: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val response = apiService.registerUser(UserRequest(username, password))
                if (response.isSuccessful && response.body()?.status == "success") {
                    _uiState.value = AuthUiState(registrationSuccess = true)
                } else {
                    val errorMessage = response.body()?.message ?: "An unknown error occurred"
                    _uiState.value = AuthUiState(error = errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Could not connect to server: ${e.message}")
            }
        }
    }

    // Used to reset the state after a navigation event
    fun consumedEvents() {
        _uiState.value = AuthUiState()
    }

    // This function resets the state to log the user out
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
