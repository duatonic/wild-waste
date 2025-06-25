package com.example.wildwaste.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wildwaste.viewmodels.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import java.io.ByteArrayOutputStream
import com.example.wildwaste.api.TrashReport

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    userId: Int
) {
    val context = LocalContext.current
    val uiState by mapViewModel.uiState.collectAsState()
    var selectedGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // State for the new report submission flow
    var newReportGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var showSubmissionSheet by remember { mutableStateOf(false) }

    var selectedReport by remember { mutableStateOf<TrashReport?>(null) }

    // --- Permission Handling ---
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    LaunchedEffect(key1 = true) {
        locationPermissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            Toast.makeText(context, "Report Submitted Successfully!", Toast.LENGTH_SHORT).show()
            showBottomSheet = false
            mapViewModel.consumedSubmissionEvent()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            mapViewModel.consumedSubmissionEvent()
        }
    }

    if (locationPermissionsState.allPermissionsGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            // --- Map View ---
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    // This is the block where the MapView is created.
                    Configuration.getInstance()
                        .load(context, context.getSharedPreferences("osmdroid", 0))
                    MapView(it).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(-7.2575, 112.7521)) // Default to Surabaya

                        // Receiver for long-press to add a new report
                        val eventsReceiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?) = false
                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    newReportGeoPoint = it
                                    showSubmissionSheet = true // Trigger the submission sheet
                                }
                                return true
                            }
                        }
                        overlays.add(MapEventsOverlay(eventsReceiver))
                    }
                },
                update = { mapView ->
                    mapView.overlays.removeAll { it is Marker } // Clear only markers
                    // Add a temporary marker for the new report location
                    if (showSubmissionSheet) {
                        newReportGeoPoint?.let { point ->
                            val tempMarker = Marker(mapView)
                            tempMarker.position = point
                            tempMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            tempMarker.title = "New Report Location"
                            mapView.overlays.add(tempMarker)
                        }
                    }
                    // Add markers for existing reports with a click listener
                    uiState.reports.forEach { report ->
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(report.latitude, report.longitude)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = report.trashType
                        marker.snippet = "Click for details"

                        // KEY CHANGE: Set the click listener to show the details sheet
                        marker.setOnMarkerClickListener { _, _ ->
                            selectedReport = report // Update state to show the details sheet
                            true // Event handled
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )
        }

        // --- Bottom Sheet for NEW report submission ---
        if (showSubmissionSheet) {
            ModalBottomSheet(onDismissRequest = { showSubmissionSheet = false }) {
                ReportSubmissionSheet(
                    geoPoint = newReportGeoPoint!!,
                    userId = userId,
                    onSubmit = { request -> mapViewModel.submitReport(request) },
                    isSubmitting = uiState.isLoading
                )
            }
        }

        // --- Bottom Sheet for showing report DETAILS ---
        selectedReport?.let { report ->
            ModalBottomSheet(onDismissRequest = { selectedReport = null }) {
                ReportDetailsSheet(report = report)
            }
        }

    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Location permission is required to use the map feature. Please grant the permission in settings.")
        }
    }
}