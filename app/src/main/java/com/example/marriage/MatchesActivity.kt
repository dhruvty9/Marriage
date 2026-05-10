package com.example.marriage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.network.models.MatchProfile
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch

class MatchesActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var currentProfiles: List<MatchProfile> = emptyList()
    private var currentIndex = 0

    private lateinit var tabFilters   : TextView
    private lateinit var tabMatchPref : TextView
    private lateinit var tabBasicMe   : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        sessionManager = SessionManager(this)

        tabFilters   = findViewById(R.id.tabFilters)
        tabMatchPref = findViewById(R.id.tabMatchPref)
        tabBasicMe   = findViewById(R.id.tabBasicMe)

        tabFilters.setOnClickListener   { selectTab(0) }
        tabMatchPref.setOnClickListener { selectTab(1) }
        tabBasicMe.setOnClickListener   { selectTab(2) }

        findViewById<ImageButton>(R.id.btnFilter).setOnClickListener {
            Toast.makeText(this, "Filters", Toast.LENGTH_SHORT).show()
        }

        loadRecommendations()
        setupBottomNav()
    }

    private fun loadRecommendations() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRecommendations(
                    token = sessionManager.getBearerToken(),
                    limit = 20,
                    skip  = 0
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    currentProfiles = response.body()?.recommendations ?: emptyList()
                    if (currentProfiles.isNotEmpty()) {
                        showProfile(currentProfiles[0])
                    }
                } else {
                    setupStaticCard()
                }
            } catch (e: Exception) {
                setupStaticCard()
            }
        }
    }

    private fun showProfile(profile: MatchProfile) {
        val tvName = findViewById<TextView>(R.id.tvMatchName)
        tvName.text = profile.fullName ?: "Unknown"

        val card = findViewById<CardView>(R.id.cardMatchProfile)
        card.setOnClickListener {
            val intent = Intent(this, MatchDetailActivity::class.java).apply {
                putExtra("userId",     profile.id)
                putExtra("name",       profile.fullName ?: "")
                putExtra("profession", profile.professionalDetails?.occupation ?: "")
                putExtra("city",       profile.locationDetails?.currentCity ?: "")
                putExtra("photoUrl",   profile.profilePhoto?.url ?: "")
            }
            startActivity(intent)
        }

        val btnYes = findViewById<Button>(R.id.btnYesInterested)
        val btnNo  = findViewById<Button>(R.id.btnNo)

        btnYes.isEnabled = true
        btnYes.text = "Yes, Interested"

        btnYes.setOnClickListener {
            sendInterest(profile.id, btnYes)
        }
        btnNo.setOnClickListener {
            Toast.makeText(this, "Skipped", Toast.LENGTH_SHORT).show()
            // Show next profile
            currentIndex++
            if (currentIndex < currentProfiles.size) {
                showProfile(currentProfiles[currentIndex])
            } else {
                Toast.makeText(this, "No more profiles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendInterest(targetUserId: String, btnYes: Button) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.sendInterest(
                    token        = sessionManager.getBearerToken(),
                    targetUserId = targetUserId
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@MatchesActivity, "Interest sent!", Toast.LENGTH_SHORT).show()
                    btnYes.text      = "Interest Sent"
                    btnYes.isEnabled = false
                } else {
                    val msg = response.body()?.message ?: "Failed to send interest"
                    Toast.makeText(this@MatchesActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MatchesActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupStaticCard() {
        val btnYes = findViewById<Button>(R.id.btnYesInterested)
        val btnNo  = findViewById<Button>(R.id.btnNo)
        val card   = findViewById<CardView>(R.id.cardMatchProfile)

        card.setOnClickListener {
            startActivity(Intent(this, MatchDetailActivity::class.java).apply {
                putExtra("name", "G. Srivalli")
                putExtra("profession", "Software Engg.")
            })
        }
        btnYes.setOnClickListener {
            Toast.makeText(this, "Interest sent to G. Srivalli!", Toast.LENGTH_SHORT).show()
            btnYes.text = "Interest Sent"
            btnYes.isEnabled = false
        }
        btnNo.setOnClickListener {
            Toast.makeText(this, "Skipped", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectTab(index: Int) {
        val tabs = listOf(tabFilters, tabMatchPref, tabBasicMe)
        tabs.forEachIndexed { i, tab ->
            if (i == index) {
                tab.setTextColor(android.graphics.Color.parseColor("#8B0000"))
                tab.setTypeface(null, android.graphics.Typeface.BOLD)
                tab.setBackgroundColor(android.graphics.Color.parseColor("#FFF0F0"))
            } else {
                tab.setTextColor(android.graphics.Color.parseColor("#757575"))
                tab.setTypeface(null, android.graphics.Typeface.NORMAL)
                tab.setBackgroundColor(android.graphics.Color.WHITE)
            }
        }
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
