package com.example.marriage.network

object ApiConstants {
    // ADB reverse port forwarding — works on physical device via USB
    // No IP needed — phone's localhost:3000 maps to PC's localhost:3000
    const val BASE_URL = "http://localhost:3000/"

    // Auth
    const val REGISTER        = "api/auth/register"
    const val LOGIN           = "api/auth/login"
    const val FORGOT_PASSWORD = "api/auth/forgot-password"
    const val CHANGE_PASSWORD = "api/auth/change-password"
    const val REFRESH_TOKEN   = "api/auth/refresh-token"
    const val LOGOUT          = "api/auth/logout"

    // Registration
    const val REGISTRATION_COMPLETE = "api/registration/complete"
    const val REGISTRATION_STATUS   = "api/registration/status"

    // Profile
    const val PROFILE_ME         = "api/profile/me"
    const val PROFILE_UPDATE     = "api/profile/update/{id}"
    const val PROFILE_VISIBILITY = "api/profile/visibility"
    const val PROFILE_BY_ID      = "api/profile/{id}"

    // Matches
    const val RECOMMENDATIONS    = "api/matches/recommendations"
    const val SEND_INTEREST      = "api/matches/interests/{targetUserId}"
    const val RESPOND_INTEREST   = "api/matches/interests/{interestId}/respond"
    const val LIST_INTERESTS     = "api/matches/interests"

    // Engagement
    const val SHORTLIST_ADD      = "api/engagement/shortlist/{targetUserId}"
    const val SHORTLIST_LIST     = "api/engagement/shortlist"
    const val WHO_VIEWED_ME      = "api/engagement/who-viewed-me"
    const val TRACK_VIEW         = "api/engagement/views/{targetUserId}"

    // Chat
    const val CHAT_CREATE        = "api/chat/direct/{otherUserId}"
    const val CHAT_LIST          = "api/chat/list"
    const val CHAT_MESSAGES      = "api/chat/{chatId}/messages"
    const val CHAT_SEND          = "api/chat/{chatId}/messages"
    const val CHAT_READ          = "api/chat/{chatId}/read"
    const val FCM_TOKEN          = "api/chat/fcm-token"

    // Report
    const val REPORT_USER        = "api/users/report/{targetUserId}"
}
