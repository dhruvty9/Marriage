package com.example.marriage

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.marriage.network.RetrofitClient
import com.example.marriage.network.models.ChatItem
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch

class ChatListActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tabAll    : TextView
    private lateinit var tabUnread : TextView
    private lateinit var tabCalls  : TextView
    private lateinit var chatListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

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
                    }
                }
            } catch (e: Exception) {
                // Static items already shown
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
                        putExtra("photoUrl", otherUser?.profilePhoto?.url ?: "")
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
        }
        findViewById<LinearLayout>(R.id.navMatches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
