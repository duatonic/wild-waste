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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.sp
import com.example.wildwaste.R

// --- User Data Class ---
// The User data class now holds all relevant, non-hardcoded info.
data class User(
    val id: Int,
    val name: String,
    val memberSince: String,
    val avatarResId: Int
)

// --- Updated Data Fetcher ---
// This function now takes the username as a parameter.
fun getUpdatedUser(userId: Int, username: String): User {
    return User(
        id = userId,
        name = username, // The name is now dynamic
        memberSince = "June 2025", // This remains hardcoded for now as requested
        avatarResId = R.drawable.ic_avatar_placeholder
    )
}


@Composable
fun AccountScreen(userId: Int, username: String) { // The username is now passed in
    // The user object is created with the dynamic username
    val user = remember(userId, username) { getUpdatedUser(userId, username) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(user = user) // The header will now display the dynamic name
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
                onClick = { /* TODO: Implement logout logic and navigate to login */ }
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(title = "Settings")
            SettingSwitchItem(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                isChecked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }
    }
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
            text = user.name, // Displays the dynamic name
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

// The other composables (SectionTitle, AccountOptionItem, etc.) remain unchanged.

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
    // Preview now includes a sample username
    AccountScreen(userId = 123, username = "Rizky Pratama")
}
