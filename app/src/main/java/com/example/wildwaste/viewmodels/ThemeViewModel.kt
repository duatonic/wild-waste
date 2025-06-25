package com.example.wildwaste.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    // State to hold whether dark mode is enabled or not.
    // mutableStateOf is used so that Compose can react to its changes.
    var isDarkMode = mutableStateOf(false)
        private set // The setter is private to ensure state is only changed via the toggle function.

    fun toggleTheme() {
        isDarkMode.value = !isDarkMode.value
    }
}
