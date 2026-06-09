package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.network.models.MatchProfile
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import android.graphics.Color
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tvUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.statusBarColor = Color.parseColor("#8B0000")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        sessionManager = SessionManager(this)
        tvUserName = findViewById(R.id.tvUserName)

        // Initial set from session
        tvUserName.text = "${sessionManager.getUserName() ?: "User"}!"

        setupFilterChips()
        refreshProfileAndData()
        setupBottomNav()
        setupDrawer()
    }

    private fun refreshProfileAndData() {
        lifecycleScope.launch {
            try {
                // 1. Fetch Latest Profile (ensures name/details are correct)
                val profileRes = RetrofitClient.apiService.getMyProfile(sessionManager.getBearerToken())
                if (profileRes.isSuccessful) {
                    profileRes.body()?.data?.let { user ->
                        sessionManager.saveUser(user)
                        tvUserName.text = "${user.fullName}!"
                    }
                }

                // 2. Load Matches
                loadRecommendations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadRecommendations() {
        val cardIds = listOf(R.id.cardRec1, R.id.cardRec2, R.id.cardRec3, R.id.cardNear1, R.id.cardNear2)
        cardIds.forEach { id -> findViewById<CardView>(id).visibility = View.GONE }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRecommendations(
                    token = sessionManager.getBearerToken(),
                    limit = 6,
                    skip  = 0
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val profiles = response.body()?.data?.recommendations ?: emptyList()
                    if (profiles.isNotEmpty()) {
                        bindRecommendedCards(profiles.take(3))
                        bindNearbyCards(profiles.drop(3).take(2))
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "API Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindRecommendedCards(profiles: List<MatchProfile>) {
        val cardIds = listOf(R.id.cardRec1, R.id.cardRec2, R.id.cardRec3)
        val nameIds = listOf(R.id.tvRecName1, R.id.tvRecName2, R.id.tvRecName3)
        val profIds = listOf(R.id.tvRecProf1, R.id.tvRecProf2, R.id.tvRecProf3)
        
        profiles.forEachIndexed { i, profile ->
            if (i < cardIds.size) {
                val card = findViewById<CardView>(cardIds[i])
                card.visibility = View.VISIBLE
                findViewById<TextView>(nameIds[i])?.text = profile.fullName ?: "Unknown"
                findViewById<TextView>(profIds[i])?.text = profile.professionalDetails?.occupation ?: ""
                card.setOnClickListener { openMatchDetail(profile) }
            }
        }
    }

    private fun bindNearbyCards(profiles: List<MatchProfile>) {
        val cardIds = listOf(R.id.cardNear1, R.id.cardNear2)
        val nameIds = listOf(R.id.tvNearName1, R.id.tvNearName2)
        val profIds = listOf(R.id.tvNearProf1, R.id.tvNearProf2)
        
        profiles.forEachIndexed { i, profile ->
            if (i < cardIds.size) {
                val card = findViewById<CardView>(cardIds[i])
                card.visibility = View.VISIBLE
                findViewById<TextView>(nameIds[i])?.text = profile.fullName ?: "Unknown"
                findViewById<TextView>(profIds[i])?.text = profile.professionalDetails?.occupation ?: ""
                card.setOnClickListener { openMatchDetail(profile) }
            }
        }
    }

    private fun openMatchDetail(profile: MatchProfile) {
        val intent = Intent(this, MatchDetailActivity::class.java).apply {
            putExtra("userId",     profile.id)
            putExtra("name",       profile.fullName ?: "")
            putExtra("profession", profile.professionalDetails?.occupation ?: "")
            putExtra("city",       profile.locationDetails?.currentCity ?: "")
            putExtra("photoUrl",   profile.mediaUpload?.profilePhoto?.url ?: "")
        }
        startActivity(intent)
    }

    private fun setupDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener { drawerLayout.openDrawer(GravityCompat.END) }
        findViewById<TextView>(R.id.drawerMatches).setOnClickListener { startActivity(Intent(this, MatchesActivity::class.java)) }
        findViewById<TextView>(R.id.drawerChat).setOnClickListener { startActivity(Intent(this, ChatListActivity::class.java)) }
        findViewById<TextView>(R.id.drawerProfile).setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        findViewById<TextView>(R.id.drawerLogout).setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
            finish()
        }
    }

    private fun setupFilterChips() {
        findViewById<TextView>(R.id.tvMatchPref).setOnClickListener { startActivity(Intent(this, MatchesActivity::class.java)) }
        findViewById<TextView>(R.id.tvSeeAllRecommended).setOnClickListener { startActivity(Intent(this, MatchesActivity::class.java)) }
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navMatches).setOnClickListener { startActivity(Intent(this, MatchesActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener { startActivity(Intent(this, ChatListActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }
}
