package com.digitalinappraigar.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.network.models.MatchProfile
import com.digitalinappraigar.app.repository.MatchesRepository
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import kotlinx.coroutines.launch

class MatchesActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var matchesRepository: MatchesRepository
    
    private var currentProfiles: List<MatchProfile> = emptyList()
    private var currentIndex = 0

    private lateinit var tabFilters   : TextView
    private lateinit var tabMatchPref : TextView
    private lateinit var tabBasicMe   : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        window.statusBarColor = Color.parseColor("#8B0000")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        sessionManager = SessionManager(this)
        matchesRepository = MatchesRepository(RetrofitClient.apiService)

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
        
        // Load other data to verify references
        loadAdditionalData()
    }

    private fun loadRecommendations() {
        val card = findViewById<CardView>(R.id.cardMatchProfile)
        card.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val response = matchesRepository.getRecommendations(token)
                
                if (response?.success == true) {
                    currentProfiles = response.data?.recommendations ?: emptyList()
                    if (currentProfiles.isNotEmpty()) {
                        showProfile(currentProfiles[0])
                    } else {
                        Toast.makeText(this@MatchesActivity, "No matches found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MatchesActivity, "Failed to load matches", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MatchesActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadAdditionalData() {
        lifecycleScope.launch {
            val token = sessionManager.getBearerToken()
            
            // These calls use the repository which now has the correct ApiService methods
            val interests = matchesRepository.getInterests(token)
            val shortlist = matchesRepository.getShortlist(token)
            val whoViewedMe = matchesRepository.getWhoViewedMe(token)
            
            // Update UI accordingly if you have sections for these
        }
    }

    private fun showProfile(profile: MatchProfile) {
        val tvName = findViewById<TextView>(R.id.tvMatchName)
        tvName.text = profile.fullName ?: "Unknown"

        val card = findViewById<CardView>(R.id.cardMatchProfile)
        card.visibility = View.VISIBLE
        card.setOnClickListener {
            val intent = Intent(this, MatchDetailActivity::class.java).apply {
                putExtra("userId",     profile.id)
                putExtra("name",       profile.fullName ?: "")
                putExtra("profession", profile.professionalDetails?.occupation ?: "")
                putExtra("city",       profile.locationDetails?.currentCity ?: "")
                putExtra("photoUrl",   profile.mediaUpload?.profilePhoto?.url ?: "")
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

    private fun selectTab(index: Int) {
        val tabs = listOf(tabFilters, tabMatchPref, tabBasicMe)
        tabs.forEachIndexed { i, tab ->
            if (i == index) {
                tab.setTextColor(Color.parseColor("#8B0000"))
                tab.setTypeface(null, android.graphics.Typeface.BOLD)
                tab.setBackgroundColor(Color.parseColor("#FFF0F0"))
            } else {
                tab.setTextColor(Color.parseColor("#757575"))
                tab.setTypeface(null, android.graphics.Typeface.NORMAL)
                tab.setBackgroundColor(Color.WHITE)
            }
        }
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
