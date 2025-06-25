package com.example.wildwaste.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wildwaste.R
import com.example.wildwaste.viewmodels.ThemeViewModel

// --- User Data Class ---
data class User(
    val id: Int,
    val name: String,
    val memberSince: String,
    val avatarResId: Int
)

// --- Updated Data Fetcher ---
fun getUpdatedUser(userId: Int, username: String): User {
    return User(
        id = userId,
        name = username,
        memberSince = "June 2025",
        avatarResId = R.drawable.ic_avatar_placeholder
    )
}

@Composable
fun AccountScreen(
    userId: Int,
    username: String,
    themeViewModel: ThemeViewModel,
    onLogoutClicked: () -> Unit
) {
    val user = remember(userId, username) { getUpdatedUser(userId, username) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val isDarkMode by themeViewModel.isDarkMode

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirmLogout = {
                showLogoutDialog = false
                onLogoutClicked()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(user = user)
            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(title = "Account")
            AccountOptionItem(
                icon = Icons.Default.Edit,
                title = "Edit Profile",
                onClick = { /* TODO: Navigate to Edit Profile screen */ }
            )
            AccountOptionItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Logout",
                isLogout = true,
                onClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(title = "Appearance")

            SettingSwitchItem(
                // ERROR 2 FIX: Use Icons.Filled for DarkMode
                icon = Icons.Filled.DarkMode,
                title = "Dark Mode",
                isChecked = isDarkMode,
                onCheckedChange = { themeViewModel.toggleTheme() }
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Logout") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(
                onClick = onConfirmLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = user.avatarResId),
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Member since ${user.memberSince}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AccountOptionItem(
    icon: ImageVector,
    title: String,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
    Divider(modifier = Modifier.padding(start = 56.dp))
}

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
    Divider(modifier = Modifier.padding(start = 56.dp))
}


@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    // ERROR 1 FIX: Use viewModel() to get a ViewModel instance for the preview
    val themeViewModel: ThemeViewModel = viewModel()
    AccountScreen(
        userId = 123,
        username = "Rizky Pratama",
        themeViewModel = themeViewModel,
        onLogoutClicked = {}
    )
}
