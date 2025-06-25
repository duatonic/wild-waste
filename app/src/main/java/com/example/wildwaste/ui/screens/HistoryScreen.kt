package com.example.wildwaste.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wildwaste.api.TrashReport
import com.example.wildwaste.viewmodels.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    userId: Int,
    historyViewModel: HistoryViewModel = viewModel()
) {
    var selectedReport by remember { mutableStateOf<TrashReport?>(null) }
    val uiState by historyViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Fetch reports when the screen is first composed
    LaunchedEffect(key1 = userId) {
        historyViewModel.fetchUserReports(userId)
        Log.d("HistoryScreen", "Reports fetched for user ID: $userId")
    }

    LaunchedEffect(uiState.deletionSuccess, uiState.error) {
        if(uiState.deletionSuccess) {
            Toast.makeText(context, "Report deleted successfully!", Toast.LENGTH_SHORT).show()
            selectedReport = null // Close the bottom sheet
            historyViewModel.consumedDeletionEvent()
        }
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            historyViewModel.consumedDeletionEvent()
        }
    }

    Scaffold(
        topBar = {
            // PERUBAHAN UTAMA DI SINI
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Report History",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2D6A44), // Hijau paling gelap
                            Color(0xFF4B8E5A), // Hijau pertengahan
                            Color(0xFF5CA46C)  // Hijau paling terang
                        )
                    )
                ),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                uiState.reports.isEmpty() -> {
                    Text(
                        text = "You haven't submitted any reports yet.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.reports, key = { it.id }) { report ->
                            ReportHistoryItem(
                                report = report,
                                onClick = { selectedReport = report }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Refreshing reports...", Toast.LENGTH_SHORT).show()
                    historyViewModel.fetchUserReports(userId)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(32.dp)
                    .align(Alignment.BottomEnd),
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh Reports")
            }
        }
    }

    selectedReport?.let { report ->
        ModalBottomSheet(onDismissRequest = { selectedReport = null }) {
            ReportDetailsSheet(
                report = report,
                onDelete = {
                    historyViewModel.deleteReport(report.id, userId)
                }
            )
        }
    }
}
