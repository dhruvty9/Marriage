package com.example.marriage.network.models

import com.google.gson.annotations.SerializedName

// ── Match / Recommendation Models ────────────────────────────

data class RecommendationsResponse(
    @SerializedName("success")         val success: Boolean,
    @SerializedName("recommendations") val recommendations: List<MatchProfile>?,
    @SerializedName("total")           val total: Int?
)

data class MatchProfile(
    @SerializedName("_id")               val id: String,
    @SerializedName("fullName")          val fullName: String?,
    @SerializedName("profilePhoto")      val profilePhoto: ProfilePhoto?,
    @SerializedName("basicInformation")  val basicInformation: BasicInformation?,
    @SerializedName("locationDetails")   val locationDetails: LocationDetails?,
    @SerializedName("professionalDetails") val professionalDetails: ProfessionalDetails?,
    @SerializedName("aboutSection")      val aboutSection: AboutSection?,
    @SerializedName("astroDetails")      val astroDetails: AstroDetails?
)

data class AboutSection(
    @SerializedName("aboutMe") val aboutMe: String? = null,
    @SerializedName("bio") val bio: String? = null
)

data class AstroDetails(
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("manglikStatus") val manglikStatus: String?
)

// ── Interest Models ───────────────────────────────────────────

data class InterestResponse(
    @SerializedName("success")  val success: Boolean,
    @SerializedName("message")  val message: String?,
    @SerializedName("interest") val interest: InterestData?
)

data class InterestData(
    @SerializedName("_id")            val id: String,
    @SerializedName("senderUserId")   val senderUserId: String?,
    @SerializedName("receiverUserId") val receiverUserId: String?,
    @SerializedName("status")         val status: String?
)

data class InterestRespondRequest(
    @SerializedName("status")          val status: String,   // accepted / rejected
    @SerializedName("responseRemark")  val responseRemark: String? = null
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

// ── Shortlist Models ──────────────────────────────────────────

data class ShortlistResponse(
    @SerializedName("success")   val success: Boolean,
    @SerializedName("shortlist") val shortlist: List<MatchProfile>?,
    @SerializedName("total")     val total: Int?
)

// ── Who Viewed Me ─────────────────────────────────────────────

data class WhoViewedMeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("viewers") val viewers: List<MatchProfile>?,
    @SerializedName("total")   val total: Int?
)
