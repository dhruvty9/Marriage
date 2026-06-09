package com.digitalinappraigar.app.network.models

import com.google.gson.annotations.SerializedName

// ── Shared Sub-Models ────────────────────────────────────────

data class ProfilePhoto(
    @SerializedName("url") val url: String?
)

data class MediaUpload(
    @SerializedName("profilePhoto") val profilePhoto: ProfilePhoto?
)

data class BasicInformation(
    @SerializedName("gender") val gender: String?,
    @SerializedName("heightCm") val heightCm: String?,
    @SerializedName("maritalStatus") val maritalStatus: String?,
    @SerializedName("motherTongue") val motherTongue: String?
)

data class LocationDetails(
    @SerializedName("state") val state: String?,
    @SerializedName("currentCity") val currentCity: String?,
    @SerializedName("hometown") val hometown: String?
)

data class ProfessionalDetails(
    @SerializedName("education") val education: String?,
    @SerializedName("occupation") val occupation: String?,
    @SerializedName("annualIncome") val annualIncome: String?,
    @SerializedName("jobType") val jobType: String?
)

data class AboutSection(
    @SerializedName("bio") val bio: String?
)

data class AstroDetails(
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("manglikStatus") val manglikStatus: String?
)

// ── Match / Recommendation Models ────────────────────────────

data class RecommendationsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: RecommendationsData?
)

data class RecommendationsData(
    @SerializedName("recommendations") val recommendations: List<MatchProfile>?,
    @SerializedName("total")           val total: Int?
)

data class MatchProfile(
    @SerializedName("_id")               val id: String,
    @SerializedName("fullName")          val fullName: String?,
    @SerializedName("mediaUpload")       val mediaUpload: MediaUpload?,
    @SerializedName("basicInformation")  val basicInformation: BasicInformation?,
    @SerializedName("locationDetails")   val locationDetails: LocationDetails?,
    @SerializedName("professionalDetails") val professionalDetails: ProfessionalDetails?,
    @SerializedName("aboutSection")      val aboutSection: AboutSection?,
    @SerializedName("astroDetails")      val astroDetails: AstroDetails?
)

data class InterestResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data")    val data: InterestResponseData? = null
)

data class InterestResponseData(
    @SerializedName("interestId")   val interestId: String?,
    @SerializedName("status")       val status: String?,
    @SerializedName("targetUserId") val targetUserId: String?
)

data class InterestsListResponse(
    @SerializedName("success")   val success: Boolean,
    @SerializedName("interests") val interests: List<InterestItem>?,
    @SerializedName("total")     val total: Int?
)

data class InterestItem(
    @SerializedName("_id")          val id: String,
    @SerializedName("status")       val status: String?,
    @SerializedName("createdAt")    val createdAt: String?,
    @SerializedName("senderUser")   val senderUser: MatchProfile?,
    @SerializedName("receiverUser") val receiverUser: MatchProfile?
)

data class ShortlistResponse(
    @SerializedName("success")   val success: Boolean,
    @SerializedName("shortlist") val shortlist: List<MatchProfile>?,
    @SerializedName("total")     val total: Int?
)

data class WhoViewedMeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("viewers") val viewers: List<MatchProfile>?,
    @SerializedName("total")   val total: Int?
)

data class PassProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
