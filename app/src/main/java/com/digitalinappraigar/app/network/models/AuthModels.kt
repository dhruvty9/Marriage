package com.digitalinappraigar.app.network.models

import com.google.gson.annotations.SerializedName

// Request models
data class RegisterRequest(
    @SerializedName("profile") val profile: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("password") val password: String
)

data class VerifyOtpRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("otp") val otp: String
)

// Response models
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String?,
    @SerializedName("refreshToken") val refreshToken: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserData?
)

data class UserData(
    @SerializedName("_id") val id: String, // Map MongoDB _id to Kotlin id
    @SerializedName("fullName") val fullName: String,
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("profile") val profile: String,
    @SerializedName("profileStatus") val profileStatus: String?,
    @SerializedName("age") val age: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("gender") val gender: String?
)

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: Any? = null
)
