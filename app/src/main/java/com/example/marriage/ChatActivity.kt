package com.example.marriage

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.marriage.network.RetrofitClient
import com.example.marriage.network.models.SendMessageRequest
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var sessionManager : SessionManager
    private lateinit var scrollMessages : ScrollView
    private lateinit var etMessage      : EditText
    private var chatId   = ""
    private var otherUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sessionManager = SessionManager(this)

        val name     = intent.getStringExtra("name")     ?: "Lavanya"
        chatId       = intent.getStringExtra("chatId")   ?: ""
        otherUserId  = intent.getStringExtra("userId")   ?: ""
        val photoUrl = intent.getStringExtra("photoUrl") ?: ""

        // Set contact name
        val tvChatName = findViewById<TextView>(R.id.tvChatName)
        tvChatName.text = name

        // Load contact photo
        if (photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .circleCrop()
                .into(findViewById<ImageView>(R.id.ivChatPhoto))
        }

        scrollMessages = findViewById(R.id.scrollMessages)
        etMessage      = findViewById(R.id.etMessage)

        // Back
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Call buttons
        findViewById<ImageButton>(R.id.btnVideoCall).setOnClickListener {
            Toast.makeText(this, "Video call with $name", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.btnVoiceCall).setOnClickListener {
            Toast.makeText(this, "Voice call with $name", Toast.LENGTH_SHORT).show()
        }

        // Attach
        findViewById<ImageButton>(R.id.btnAttach).setOnClickListener {
            Toast.makeText(this, "Attach file", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.btnEmoji).setOnClickListener {
            Toast.makeText(this, "Emoji", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.btnCamera).setOnClickListener {
            Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show()
        }

        // Send / Mic button
        val btnMic = findViewById<ImageButton>(R.id.btnMic)
        btnMic.setOnClickListener {
            val msg = etMessage.text.toString().trim()
            if (msg.isNotEmpty()) {
                sendMessage(msg, name)
            } else {
                Toast.makeText(this, "Voice message", Toast.LENGTH_SHORT).show()
            }
        }

        // If we have a chatId, load messages; else create chat first
        if (chatId.isNotEmpty()) {
            loadMessages()
            markAsRead()
        } else if (otherUserId.isNotEmpty()) {
            createChat(otherUserId)
        }

        // Scroll to bottom
        scrollMessages.post { scrollMessages.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    private fun createChat(otherUserId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createOrGetChat(
                    token       = sessionManager.getBearerToken(),
                    otherUserId = otherUserId
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    chatId = response.body()?.chat?.id ?: ""
                    if (chatId.isNotEmpty()) loadMessages()
                }
            } catch (e: Exception) { /* silent */ }
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getChatMessages(
                    token  = sessionManager.getBearerToken(),
                    chatId = chatId,
                    limit  = 50
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    // Messages loaded — in a real app, update RecyclerView
                    // For now, static messages in XML are shown
                }
            } catch (e: Exception) { /* silent */ }
        }
    }

    private fun sendMessage(text: String, contactName: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())

        if (chatId.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.sendMessage(
                        token   = sessionManager.getBearerToken(),
                        chatId  = chatId,
                        request = SendMessageRequest(text = text, timestamp = timestamp)
                    )
                    if (response.isSuccessful && response.body()?.success == true) {
                        etMessage.setText("")
                        Toast.makeText(this@ChatActivity, "Sent", Toast.LENGTH_SHORT).show()
                        scrollMessages.post { scrollMessages.fullScroll(ScrollView.FOCUS_DOWN) }
                    } else {
                        Toast.makeText(this@ChatActivity, "Failed to send", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ChatActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // No chatId yet — show locally
            Toast.makeText(this, "Message: $text", Toast.LENGTH_SHORT).show()
            etMessage.setText("")
        }
    }

    private fun markAsRead() {
        if (chatId.isEmpty()) return
        lifecycleScope.launch {
            try {
                RetrofitClient.apiService.markChatRead(
                    token  = sessionManager.getBearerToken(),
                    chatId = chatId
                )
            } catch (e: Exception) { /* silent */ }
        }
    }
}
