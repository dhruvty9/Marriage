package com.digitalinappraigar.app.repository

import com.digitalinappraigar.app.network.ApiService
import com.digitalinappraigar.app.network.models.InterestsListResponse
import com.digitalinappraigar.app.network.models.RecommendationsResponse
import com.digitalinappraigar.app.network.models.ShortlistResponse
import com.digitalinappraigar.app.network.models.WhoViewedMeResponse

class MatchesRepository(private val apiService: ApiService) {

    suspend fun getAllMatchesData(token: String): MatchesData {
        return try {
            val recommendations = apiService.getRecommendations(token, limit = 100, skip = 0)
            val interests = apiService.getInterests(token)
            val shortlist = apiService.getShortlist(token)
            val whoViewedMe = apiService.getWhoViewedMe(token)

            MatchesData(
                recommendations = if (recommendations.isSuccessful) recommendations.body() else null,
                interests = if (interests.isSuccessful) interests.body() else null,
                shortlist = if (shortlist.isSuccessful) shortlist.body() else null,
                whoViewedMe = if (whoViewedMe.isSuccessful) whoViewedMe.body() else null
            )
        } catch (e: Exception) {
            MatchesData(null, null, null, null)
        }
    }

    suspend fun getRecommendations(token: String, limit: Int = 10, skip: Int = 0): RecommendationsResponse? {
        return try {
            val response = apiService.getRecommendations(token, limit, skip)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getInterests(token: String): InterestsListResponse? {
        return try {
            val response = apiService.getInterests(token)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getShortlist(token: String): ShortlistResponse? {
        return try {
            val response = apiService.getShortlist(token)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWhoViewedMe(token: String): WhoViewedMeResponse? {
        return try {
            val response = apiService.getWhoViewedMe(token)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}

data class MatchesData(
    val recommendations: RecommendationsResponse?,
    val interests: InterestsListResponse?,
    val shortlist: ShortlistResponse?,
    val whoViewedMe: WhoViewedMeResponse?
)
