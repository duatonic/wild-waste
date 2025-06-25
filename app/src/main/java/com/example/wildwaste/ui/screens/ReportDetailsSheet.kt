package com.example.wildwaste.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wildwaste.api.TrashReport
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportDetailsSheet(
    report: TrashReport,
    onDelete: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Attempt to decode the Base64 string into a Bitmap
    val decodedImage = remember(report.imageBase64) {
        try {
            val imageBytes = Base64.decode(report.imageBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            null // Return null if decoding fails
        }
    }

    // Function to format the date string
    val formattedDate = remember(report.reportedAt) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            parser.parse(report.reportedAt)?.let { formatter.format(it) } ?: report.reportedAt
        } catch (e: Exception) {
            report.reportedAt // Fallback to original string
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Trash Report Details",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display the image if available
        if (decodedImage != null) {
            Image(
                bitmap = decodedImage.asImageBitmap(),
                contentDescription = "Image of ${report.trashType}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
        else {
            Text(
                text = "Image not available",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Report details
        DetailRow(label = "Type", value = report.trashType)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        DetailRow(label = "Quantity", value = report.quantity)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        DetailRow(label = "Reported by", value = report.username ?: "Unknown")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        DetailRow(label = "Date", value = formattedDate)

        // Display notes if they exist
        if (!report.notes.isNullOrBlank()) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = report.notes, style = MaterialTheme.typography.bodyLarge)
        }

        onDelete?.let { deleteAction ->
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Report")
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // Add space at the bottom
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Report?") },
            text = { Text("Are you sure you want to permanently delete this report? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete?.invoke()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// A helper composable for displaying a label and a value
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}