package com.digitalinappraigar.app.repository

import com.digitalinappraigar.app.network.ApiService
import com.digitalinappraigar.app.network.models.ChatListResponse
import com.digitalinappraigar.app.network.models.MessagesResponse

class ChatRepository(private val apiService: ApiService) {

    suspend fun getAllChatData(token: String): ChatData {
        return try {
            val chatList = apiService.getChatList(token, limit = 100, skip = 0)
            
            val messagesMap = mutableMapOf<String, MessagesResponse?>()
            if (chatList.isSuccessful && chatList.body()?.success == true) {
                chatList.body()?.chats?.forEach { chat ->
                    val messages = apiService.getChatMessages(token, chat.id, limit = 100, skip = 0)
                    messagesMap[chat.id] = if (messages.isSuccessful) messages.body() else null
                }
            }

            ChatData(
                chatList = if (chatList.isSuccessful) chatList.body() else null,
                messages = messagesMap
            )
        } catch (e: Exception) {
            ChatData(null, emptyMap())
        }
    }

    suspend fun getChatList(token: String, limit: Int = 20, skip: Int = 0): ChatListResponse? {
        return try {
            val response = apiService.getChatList(token, limit, skip)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getChatMessages(token: String, chatId: String, limit: Int = 50, skip: Int = 0): MessagesResponse? {
        return try {
            val response = apiService.getChatMessages(token, chatId, limit, skip)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}

data class ChatData(
    val chatList: ChatListResponse?,
    val messages: Map<String, MessagesResponse?>
)
