package com.digitalinappraigar.app.network

import com.digitalinappraigarvivah.app.BuildConfig

object ApiConstants {
    const val BASE_URL: String = BuildConfig.SERVER_URL

    // Auth
    const val REGISTER        = "api/auth/register"
    const val LOGIN           = "api/auth/login"
    const val SEND_OTP        = "api/auth/send-otp"
    const val VERIFY_OTP      = "api/auth/verify-otp"
    const val REFRESH_TOKEN   = "api/auth/refresh-token"
    const val LOGOUT          = "api/auth/logout"

    // Registration (backendraigar)
    const val BASIC_DETAILS      = "api/basic-details"
    const val RELIGION_DETAILS   = "api/religion-details"
    const val PERSONAL_DETAILS   = "api/personal-details"
    const val PROFESSIONAL_DETAILS = "api/professional-details"
    const val ABOUT_YOURSELF     = "api/about-yourself/update"

    // Profile
    const val PROFILE_UPDATE     = "api/profile/update/{id}"
    const val PROFILE_ME         = "api/profile/me"
    const val PROFILE_BY_ID      = "api/profile/{id}"
    const val PROFILE_VISIBILITY = "api/profile/visibility"

    // Matches
    const val RECOMMENDATIONS    = "api/matches/recommendations"
    const val PASS_PROFILE       = "api/matches/pass/{targetUserId}"
    const val SEND_INTEREST      = "api/matches/interests/{targetUserId}"
    const val LIST_INTERESTS     = "api/matches/interests"

    // Engagement
    const val SHORTLIST_ADD      = "api/user-engagement/shortlist/{targetUserId}"
    const val SHORTLIST_LIST     = "api/user-engagement/shortlist"
    const val WHO_VIEWED_ME      = "api/user-engagement/who-viewed-me"
    const val TRACK_VIEW         = "api/user-engagement/views/{targetUserId}"

    // Chat
    const val CHAT_CREATE        = "api/chat/direct/{otherUserId}"
    const val CHAT_LIST          = "api/chat/list"
    const val CHAT_MESSAGES      = "api/chat/{chatId}/messages"
    const val CHAT_SEND          = "api/chat/{chatId}/messages"
    const val CHAT_READ          = "api/chat/{chatId}/read"
}
