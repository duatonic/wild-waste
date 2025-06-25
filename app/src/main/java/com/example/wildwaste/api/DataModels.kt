package com.example.wildwaste.api

import com.google.gson.annotations.SerializedName

// --- User Models ---
data class UserRequest(val username: String, val password: String)

data class LoginResponse(
    val status: String,
    val message: String,
    @SerializedName("user_id") val userId: Int?,
    val username: String?
)

data class GenericResponse(val status: String, val message: String)

// --- Report Models ---
data class TrashReportRequest(
    @SerializedName("user_id") val userId: Int,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("trash_type") val trashType: String,
    val quantity: String,
    @SerializedName("image_base64") val imageBase64: String?,
    val notes: String?
)

data class TrashReport(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("trash_type") val trashType: String,
    val quantity: String,
    @SerializedName("image_base64") val imageBase64: String?,
    val notes: String?,
    @SerializedName("reported_at") val reportedAt: String,
    val username: String? // Included from the GET /reports endpoint
)

data class AllReportsResponse(
    val status: String,
    val data: List<TrashReport>?
)