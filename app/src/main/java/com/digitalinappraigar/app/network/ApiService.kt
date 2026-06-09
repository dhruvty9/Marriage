package com.digitalinappraigar.app.network

import com.digitalinappraigar.app.network.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── AUTH ──────────────────────────────────────────────────
    @POST(ApiConstants.REGISTER)
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST(ApiConstants.LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST(ApiConstants.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    // ── REGISTRATION FLOW (backendraigar) ─────────────────────
    @POST(ApiConstants.BASIC_DETAILS)
    suspend fun saveBasicDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @POST(ApiConstants.RELIGION_DETAILS)
    suspend fun saveReligionDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @POST(ApiConstants.PERSONAL_DETAILS)
    suspend fun savePersonalDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @POST(ApiConstants.PROFESSIONAL_DETAILS)
    suspend fun saveProfessionalDetails(
        @Header("Authorization") token: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    // ── PROFILE / ABOUT YOURSELF ──────────────────────────────
    @GET(ApiConstants.PROFILE_ME)
    suspend fun getMyProfile(
        @Header("Authorization") token: String
    ): Response<AuthResponse>

    @GET(ApiConstants.PROFILE_BY_ID)
    suspend fun getProfileById(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Response<AuthResponse>

    @PUT(ApiConstants.PROFILE_UPDATE)
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse>

    @Multipart
    @PUT(ApiConstants.ABOUT_YOURSELF)
    suspend fun saveAboutYourself(
        @Header("Authorization") token: String,
        @Part("bio") bio: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<ApiResponse>

    // ── MATCHES ───────────────────────────────────────────────
    @GET(ApiConstants.RECOMMENDATIONS)
    suspend fun getRecommendations(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 10,
        @Query("skip")  skip: Int  = 0
    ): Response<RecommendationsResponse>

    @POST(ApiConstants.SEND_INTEREST)
    suspend fun sendInterest(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): Response<InterestResponse>

    @GET(ApiConstants.LIST_INTERESTS)
    suspend fun getInterests(
        @Header("Authorization") token: String,
        @Query("type") type: String = "received"
    ): Response<InterestsListResponse>

    // ── ENGAGEMENT ────────────────────────────────────────────
    @GET(ApiConstants.SHORTLIST_LIST)
    suspend fun getShortlist(
        @Header("Authorization") token: String
    ): Response<ShortlistResponse>

    @GET(ApiConstants.WHO_VIEWED_ME)
    suspend fun getWhoViewedMe(
        @Header("Authorization") token: String
    ): Response<WhoViewedMeResponse>

    @POST(ApiConstants.TRACK_VIEW)
    suspend fun trackProfileView(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): Response<ApiResponse>

    // ── CHAT ──────────────────────────────────────────────────
    @POST(ApiConstants.CHAT_CREATE)
    suspend fun createOrGetChat(
        @Header("Authorization") token: String,
        @Path("otherUserId") otherUserId: String
    ): Response<ChatCreateResponse>

    @GET(ApiConstants.CHAT_LIST)
    suspend fun getChatList(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
        @Query("skip")  skip: Int  = 0
    ): Response<ChatListResponse>

    @GET(ApiConstants.CHAT_MESSAGES)
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Query("limit") limit: Int = 50,
        @Query("skip")  skip: Int  = 0
    ): Response<MessagesResponse>

    @POST(ApiConstants.CHAT_SEND)
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>

    @PATCH(ApiConstants.CHAT_READ)
    suspend fun markChatRead(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): Response<ApiResponse>

    // ── CONFIG ────────────────────────────────────────────────
    @GET("api/config/options")
    suspend fun getConfigOptions(): Response<ConfigOptionsResponse>
}
