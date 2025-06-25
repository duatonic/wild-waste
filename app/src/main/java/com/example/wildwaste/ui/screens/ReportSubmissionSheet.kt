package com.example.wildwaste.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.wildwaste.api.TrashReportRequest
import org.osmdroid.util.GeoPoint
import java.io.ByteArrayOutputStream

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream) // Compress to reduce size
        val byteArray = byteArrayOutputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Composable
fun ReportSubmissionSheet(
    geoPoint: GeoPoint,
    userId: Int,
    onSubmit: (TrashReportRequest) -> Unit,
    isSubmitting: Boolean
) {
    var trashType by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Report New Trash", style = MaterialTheme.typography.headlineSmall)
        Text("At: ${String.format("%.5f", geoPoint.latitude)}, ${String.format("%.5f", geoPoint.longitude)}")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = trashType,
            onValueChange = { trashType = it },
            label = { Text("Type of Trash (e.g., Plastic, Cans)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity (e.g., 1 bag, ~5 kg)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Optional Notes") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )
        Spacer(Modifier.height(16.dp))

        // --- Image Picker ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Select Image")
            }
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        // --- Submit Button ---
        if (isSubmitting) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val imageBase64 = imageUri?.let { uriToBase64(context, it) }
                    val reportRequest = TrashReportRequest(
                        userId = userId,
                        latitude = geoPoint.latitude,
                        longitude = geoPoint.longitude,
                        trashType = trashType,
                        quantity = quantity,
                        imageBase64 = imageBase64,
                        notes = notes.takeIf { it.isNotBlank() }
                    )
                    onSubmit(reportRequest)
                },
                enabled = trashType.isNotBlank() && quantity.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Report")
            }
        }
    }
}