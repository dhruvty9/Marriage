package com.example.marriage.network

import com.example.marriage.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── AUTH ──────────────────────────────────────────────────

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse>

    @POST("api/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse>

    @POST("api/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Body request: RefreshTokenRequest): Response<ApiResponse>

    // ── REGISTRATION ──────────────────────────────────────────

    @POST("api/registration/complete")
    suspend fun completeRegistration(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @PUT("api/basic-details/update")
    suspend fun updateBasicDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @PUT("api/religion-details/update")
    suspend fun updateReligionDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @PUT("api/personal-details/update")
    suspend fun updatePersonalDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @GET("api/registration/status")
    suspend fun getRegistrationStatus(
        @Header("Authorization") token: String
    ): Response<AuthResponse> // Fixed to AuthResponse

    // ── PROFILE ───────────────────────────────────────────────

    @GET("api/profile/me")
    suspend fun getMyProfile(
        @Header("Authorization") token: String
    ): Response<AuthResponse>

    @GET("api/profile/{id}")
    suspend fun getProfileById(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Response<AuthResponse>

    @PATCH("api/profile/visibility")
    suspend fun updateVisibility(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    // ── MATCHES ───────────────────────────────────────────────

    @GET("api/matches/recommendations")
    suspend fun getRecommendations(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10,
        @Query("skip")  skip: Int  = 0
    ): Response<RecommendationsResponse>

    @POST("api/matches/interests/{targetUserId}")
    suspend fun sendInterest(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): Response<InterestResponse>

    @PATCH("api/matches/interests/{interestId}/respond")
    suspend fun respondToInterest(
        @Header("Authorization") token: String,
        @Path("interestId") interestId: String,
        @Body request: InterestRespondRequest
    ): Response<ApiResponse>

    @GET("api/matches/interests")
    suspend fun getInterests(
        @Header("Authorization") token: String
    ): Response<InterestsListResponse>

    // ── ENGAGEMENT ────────────────────────────────────────────

    @POST("api/engagement/shortlist/{targetUserId}")
    suspend fun addToShortlist(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): Response<ApiResponse>

    @DELETE("api/engagement/shortlist/{targetUserId}")
    suspend fun removeFromShortlist(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): Response<ApiResponse>

    @GET("api/engagement/shortlist")
    suspend fun getShortlist(
        @Header("Authorization") token: String
    ): Response<ShortlistResponse>

    @GET("api/engagement/who-viewed-me")
    suspend fun getWhoViewedMe(
        @Header("Authorization") token: String
    ): Response<WhoViewedMeResponse>

    @POST("api/engagement/views/{targetUserId}")
    suspend fun trackProfileView(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): Response<ApiResponse>

    // ── CHAT ──────────────────────────────────────────────────

    @POST("api/chat/direct/{otherUserId}")
    suspend fun createOrGetChat(
        @Header("Authorization") token: String,
        @Path("otherUserId") otherUserId: String
    ): Response<ChatCreateResponse>

    @GET("api/chat/list")
    suspend fun getChatList(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
        @Query("skip")  skip: Int  = 0
    ): Response<ChatListResponse>

    @GET("api/chat/{chatId}/messages")
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Query("limit") limit: Int = 50,
        @Query("skip")  skip: Int  = 0
    ): Response<MessagesResponse>

    @POST("api/chat/{chatId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>

    @PATCH("api/chat/{chatId}/read")
    suspend fun markChatRead(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): Response<ApiResponse>

    @POST("api/chat/fcm-token")
    suspend fun registerFcmToken(
        @Header("Authorization") token: String,
        @Body request: FcmTokenRequest
    ): Response<ApiResponse>

    // ── REPORT ────────────────────────────────────────────────

    @POST("api/users/report/{targetUserId}")
    suspend fun reportUser(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>
}
