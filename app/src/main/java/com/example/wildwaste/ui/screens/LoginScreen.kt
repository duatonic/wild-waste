package com.example.wildwaste.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.wildwaste.R
import com.example.wildwaste.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (userId: Int, username: String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- PERUBAHAN DI SINI ---
    // Menggunakan tiga warna gradien baru untuk kedalaman visual yang lebih baik
    val gradientColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2D6A44), // Hijau paling gelap
            Color(0xFF4B8E5A), // Hijau pertengahan
            Color(0xFF5CA46C)  // Hijau paling terang
        )
    )

    LaunchedEffect(key1 = uiState) {
        if (uiState.loginSuccess) {
            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            val userId = uiState.loggedInUserId
            if (userId != null) {
                onLoginSuccess(userId, username)
            } else {
                Toast.makeText(context, "Could not retrieve user ID.", Toast.LENGTH_LONG).show()
            }
            authViewModel.consumedEvents()
        }
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.consumedEvents()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientColors) // Menerapkan gradien baru
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_wildwaste_logo),
            contentDescription = "WildWaste Logo",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "WildWaste",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Welcome Back",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                cursorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                cursorColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = { authViewModel.login(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = username.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    // Warna tombol sedikit disesuaikan agar cocok dengan gradien baru
                    containerColor = Color(0xFF004D40)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Login", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Sign Up", color = Color.White)
        }
    }
}
