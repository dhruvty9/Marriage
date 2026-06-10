package com.example.marriage.network.models

import com.google.gson.annotations.SerializedName

// ── Request Models ────────────────────────────────────────────

data class RegisterRequest(
    @SerializedName("profile")      val profile: String,
    @SerializedName("fullName")     val fullName: String,
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("password")     val password: String
)

data class LoginRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("password")     val password: String
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class ForgotPasswordRequest(
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("newPassword")  val newPassword: String
)

data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

// ── Response Models ───────────────────────────────────────────

data class AuthResponse(
    @SerializedName("success")      val success: Boolean,
    @SerializedName("message")      val message: String?,
    @SerializedName("accessToken")  val accessToken: String?,
    @SerializedName("refreshToken") val refreshToken: String?,
    @SerializedName("user")         val user: UserData?,
    @SerializedName("data")         val data: UserData?
)

data class UserData(
    @SerializedName("_id")          val id: String,
    @SerializedName("fullName")     val fullName: String,
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("profile")      val profile: String?,
    @SerializedName("profilePhoto") val profilePhoto: ProfilePhoto?,
    @SerializedName("religionDetails") val religionDetails: ReligionDetails?,
    @SerializedName("personalDetails") val personalDetails: PersonalDetailsData?,
    @SerializedName("basicInformation") val basicInformation: BasicInformation?,
    @SerializedName("locationDetails")  val locationDetails: LocationDetails?,
    @SerializedName("professionalDetails") val professionalDetails: ProfessionalDetails?,
    @SerializedName("aboutSection")     val aboutSection: AboutSection?,
    @SerializedName("profileStatus")    val profileStatus: String?,
    @SerializedName("profileCompletedSteps") val profileCompletedSteps: Int?
)

data class PersonalDetailsData(
    @SerializedName("maritalStatus") val maritalStatus: String?,
    @SerializedName("noOfChildren") val noOfChildren: Int?,
    @SerializedName("childrenLivingWithYou") val childrenLivingWithYou: Boolean?,
    @SerializedName("height") val height: String?,
    @SerializedName("familyStatus") val familyStatus: String?,
    @SerializedName("familyType") val familyType: String?
)

data class ReligionDetails(
    @SerializedName("caste")    val caste: String?,
    @SerializedName("subCaste") val subCaste: String?,
    @SerializedName("dosham")   val dosham: String?
)

data class ProfilePhoto(
    @SerializedName("url")      val url: String?,
    @SerializedName("publicId") val publicId: String?
)

data class BasicInformation(
    @SerializedName("gender")        val gender: String?,
    @SerializedName("heightCm")      val heightCm: Int?,
    @SerializedName("maritalStatus") val maritalStatus: String?,
    @SerializedName("motherTongue")  val motherTongue: String?
)

data class LocationDetails(
    @SerializedName("state")       val state: String?,
    @SerializedName("currentCity") val currentCity: String?,
    @SerializedName("hometown")    val hometown: String?
)

data class ProfessionalDetails(
    @SerializedName("education")    val education: String?,
    @SerializedName("occupation")   val occupation: String?,
    @SerializedName("annualIncome") val annualIncome: Long?,
    @SerializedName("jobType")      val jobType: String?
)

data class ApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
