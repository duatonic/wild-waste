package com.example.wildwaste.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    suspend fun registerUser(@Body userRequest: UserRequest): Response<GenericResponse>

    @POST("login")
    suspend fun loginUser(@Body userRequest: UserRequest): Response<LoginResponse>

    @POST("reports")
    suspend fun submitReport(@Body reportRequest: TrashReportRequest): Response<GenericResponse>

    @GET("reports")
    suspend fun getAllReports(): Response<AllReportsResponse>

    @GET("reports/user/{user_id}")
    suspend fun getUserReports(@Path("user_id") userId: Int): Response<AllReportsResponse>
}