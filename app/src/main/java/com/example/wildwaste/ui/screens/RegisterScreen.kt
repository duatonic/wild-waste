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
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- PERUBAHAN DI SINI ---
    // Menerapkan gradien tiga warna yang konsisten dengan tema aplikasi
    val gradientColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2D6A44), // Hijau paling gelap
            Color(0xFF4B8E5A), // Hijau pertengahan
            Color(0xFF5CA46C)  // Hijau paling terang
        )
    )

    LaunchedEffect(key1 = uiState) {
        if (uiState.registrationSuccess) {
            Toast.makeText(context, "Registration Successful! Please log in.", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
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
            text = "Create an Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = password != confirmPassword,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                cursorColor = Color.White,
                errorContainerColor = Color.White.copy(alpha = 0.3f)
            )
        )
        if (password != confirmPassword) {
            Text(
                "Passwords do not match",
                color = Color(0xFFD32F2F), // Merah yang lebih kontras
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = { authViewModel.register(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = username.isNotBlank() && password.isNotBlank() && password == confirmPassword,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF004D40) // Hijau sangat tua sebagai aksen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Register", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log In", color = Color.White)
        }
    }
}
