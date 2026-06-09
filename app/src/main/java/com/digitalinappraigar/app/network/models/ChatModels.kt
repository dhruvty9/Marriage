package com.digitalinappraigar.app.network.models

import com.google.gson.annotations.SerializedName

data class ChatListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("chats")   val chats: List<ChatItem>?,
    @SerializedName("total")   val total: Int?
)

data class ChatItem(
    @SerializedName("_id")           val id: String,
    @SerializedName("otherUser")     val otherUser: MatchProfile?,
    @SerializedName("lastMessage")   val lastMessage: String?,
    @SerializedName("lastMessageAt") val lastMessageAt: String?,
    @SerializedName("unreadCount")   val unreadCount: Int?
)

data class ChatCreateResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("chat")    val chat: ChatItem?
)

data class MessagesResponse(
    @SerializedName("success")  val success: Boolean,
    @SerializedName("messages") val messages: List<MessageItem>?,
    @SerializedName("total")    val total: Int?
)

data class MessageItem(
    @SerializedName("_id")       val id: String,
    @SerializedName("senderId")  val senderId: String?,
    @SerializedName("text")      val text: String?,
    @SerializedName("timestamp") val timestamp: String?
)

data class SendMessageRequest(
    @SerializedName("text")      val text: String,
    @SerializedName("timestamp") val timestamp: String
)

data class SendMessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: MessageItem?
)

data class FcmTokenRequest(
    @SerializedName("fcmToken") val fcmToken: String
)
