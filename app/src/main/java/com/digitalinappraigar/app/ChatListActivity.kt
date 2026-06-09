package com.digitalinappraigar.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.network.models.ChatItem
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import kotlinx.coroutines.launch
import androidx.core.view.WindowInsetsControllerCompat

class ChatListActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tabAll    : TextView
    private lateinit var tabUnread : TextView
    private lateinit var tabCalls  : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        window.statusBarColor = Color.parseColor("#8B0000")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        sessionManager = SessionManager(this)

        tabAll    = findViewById(R.id.tabAllMessages)
        tabUnread = findViewById(R.id.tabUnread)
        tabCalls  = findViewById(R.id.tabCalls)

        tabAll.setOnClickListener    { selectTab(0) }
        tabUnread.setOnClickListener { selectTab(1) }
        tabCalls.setOnClickListener  { selectTab(2) }

        // Static chat items from XML — wire them up
        setupStaticChatItems()

        // Load real chats from API
        loadChatList()

        setupBottomNav()
    }

    private fun loadChatList() {
        // Hide all static frontend chat items initially
        val itemIds = listOf(R.id.chatItem1, R.id.chatItem2, R.id.chatItem3, R.id.chatItem4, R.id.chatItem5, R.id.chatItem6)
        itemIds.forEach { id -> findViewById<LinearLayout>(id).visibility = View.GONE }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getChatList(
                    token = sessionManager.getBearerToken(),
                    limit = 20
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val chats = response.body()?.chats ?: emptyList()
                    if (chats.isNotEmpty()) {
                        updateChatItems(chats)
                    } else {
                        Toast.makeText(this@ChatListActivity, "No chats found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ChatListActivity, "Failed to load backend chats", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatListActivity, "API Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateChatItems(chats: List<ChatItem>) {
        // Update the static XML items with real data
        val itemIds = listOf(
            R.id.chatItem1, R.id.chatItem2, R.id.chatItem3,
            R.id.chatItem4, R.id.chatItem5, R.id.chatItem6
        )
        chats.forEachIndexed { i, chat ->
            if (i < itemIds.size) {
                val item = findViewById<LinearLayout>(itemIds[i])
                item.visibility = View.VISIBLE // Make visible since it has real data
                
                // Update name TextView (first child after ImageView)
                val nameView = item.getChildAt(1) as? LinearLayout
                val tvName = nameView?.getChildAt(0) as? TextView
                val tvMsg  = nameView?.getChildAt(1) as? TextView

                tvName?.text = chat.otherUser?.fullName ?: "Unknown"
                tvMsg?.text  = chat.lastMessage ?: "No messages yet"

                val chatId   = chat.id
                val otherUser = chat.otherUser
                item.setOnClickListener {
                    val intent = Intent(this, ChatActivity::class.java).apply {
                        putExtra("chatId",   chatId)
                        putExtra("name",     otherUser?.fullName ?: "Unknown")
                        putExtra("userId",   otherUser?.id ?: "")
                        // Corrected: profilePhoto is nested inside mediaUpload to match the data model
                        putExtra("photoUrl", otherUser?.mediaUpload?.profilePhoto?.url ?: "")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupStaticChatItems() {
        val names = listOf("Srivalli", "Lavanya", "Harika", "Kavya", "Divya", "Deepika")
        val ids   = listOf(
            R.id.chatItem1, R.id.chatItem2, R.id.chatItem3,
            R.id.chatItem4, R.id.chatItem5, R.id.chatItem6
        )
        ids.forEachIndexed { i, id ->
            val item = findViewById<LinearLayout>(id)
            item.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java).apply {
                    putExtra("name", names[i])
                }
                startActivity(intent)
            }
        }
    }

    private fun selectTab(index: Int) {
        val tabs = listOf(tabAll, tabUnread, tabCalls)
        tabs.forEachIndexed { i, tab ->
            if (i == index) {
                tab.setTextColor(Color.WHITE)
                tab.setTypeface(null, Typeface.BOLD)
                tab.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#8B0000")
                )
            } else {
                tab.setTextColor(Color.parseColor("#555555"))
                tab.setTypeface(null, Typeface.NORMAL)
                tab.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#F0F0F0")
                )
            }
        }
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navMatches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
