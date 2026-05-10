package com.example.marriage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.marriage.network.RetrofitClient
import com.example.marriage.network.models.MatchProfile
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        // Set user name in header
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        tvUserName.text = "${sessionManager.getUserName() ?: "User"}!"

        setupFilterChips()
        loadRecommendations()
        setupBottomNav()
    }

    private fun setupFilterChips() {
        findViewById<TextView>(R.id.chipOnline).setOnClickListener {
            Toast.makeText(this, "Who Online", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.chipNew).setOnClickListener {
            Toast.makeText(this, "New matches", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.chipEducation).setOnClickListener {
            Toast.makeText(this, "High Education", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.tvMatchPref).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }
        findViewById<TextView>(R.id.tvSeeAllRecommended).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }
        findViewById<TextView>(R.id.tvSeeAllNearby).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }
    }

    private fun loadRecommendations() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRecommendations(
                    token = sessionManager.getBearerToken(),
                    limit = 6,
                    skip  = 0
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val profiles = response.body()?.recommendations ?: emptyList()
                    bindRecommendedCards(profiles.take(3))
                    bindNearbyCards(profiles.drop(3).take(2))
                } else {
                    // Show static cards if API fails
                    setupStaticCards()
                }
            } catch (e: Exception) {
                setupStaticCards()
            }
        }
    }

    private fun bindRecommendedCards(profiles: List<MatchProfile>) {
        val cardIds = listOf(R.id.cardRec1, R.id.cardRec2, R.id.cardRec3)
        profiles.forEachIndexed { i, profile ->
            if (i < cardIds.size) {
                val card = findViewById<CardView>(cardIds[i])
                card.setOnClickListener { openMatchDetail(profile) }
            }
        }
        // If fewer than 3 profiles, still set up static fallback for remaining
        if (profiles.size < 3) setupStaticCards()
    }

    private fun bindNearbyCards(profiles: List<MatchProfile>) {
        val cardIds = listOf(R.id.cardNear1, R.id.cardNear2)
        profiles.forEachIndexed { i, profile ->
            if (i < cardIds.size) {
                val card = findViewById<CardView>(cardIds[i])
                card.setOnClickListener { openMatchDetail(profile) }
            }
        }
    }

    private fun setupStaticCards() {
        // Fallback static navigation when no API data
        val staticData = listOf(
            Triple(R.id.cardRec1, "Srivalli", "Software Engg."),
            Triple(R.id.cardRec2, "Ramya Varma", "Doctor"),
            Triple(R.id.cardRec3, "Bhavna KM", "CA"),
            Triple(R.id.cardNear1, "Srivalli D", "Software Engg."),
            Triple(R.id.cardNear2, "Ramya Varma", "Government Job")
        )
        staticData.forEach { (id, name, prof) ->
            findViewById<CardView>(id).setOnClickListener {
                val intent = Intent(this, MatchDetailActivity::class.java).apply {
                    putExtra("name", name)
                    putExtra("profession", prof)
                }
                startActivity(intent)
            }
        }
    }

    private fun openMatchDetail(profile: MatchProfile) {
        val intent = Intent(this, MatchDetailActivity::class.java).apply {
            putExtra("userId",     profile.id)
            putExtra("name",       profile.fullName ?: "")
            putExtra("profession", profile.professionalDetails?.occupation ?: "")
            putExtra("city",       profile.locationDetails?.currentCity ?: "")
            putExtra("photoUrl",   profile.profilePhoto?.url ?: "")
        }
        startActivity(intent)
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navMatches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener {
            Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
