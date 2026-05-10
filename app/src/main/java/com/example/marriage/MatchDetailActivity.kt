package com.example.marriage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.marriage.network.RetrofitClient
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch

class MatchDetailActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        sessionManager = SessionManager(this)

        val userId     = intent.getStringExtra("userId") ?: ""
        val name       = intent.getStringExtra("name")       ?: "G. Srivalli"
        val profession = intent.getStringExtra("profession") ?: "Software Engg."
        val city       = intent.getStringExtra("city")       ?: "Hyderabad"
        val photoUrl   = intent.getStringExtra("photoUrl")   ?: ""

        // Set name
        findViewById<TextView>(R.id.tvDetailName).text = name

        // Load photo with Glide
        if (photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .centerCrop()
                .into(findViewById<ImageView>(R.id.ivDetailPhoto))
        }

        // If userId available, load full profile from API
        if (userId.isNotEmpty()) {
            loadFullProfile(userId)
            // Track this profile view
            trackView(userId)
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val btnYes = findViewById<Button>(R.id.btnYesInterested)
        val btnNo  = findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            if (userId.isNotEmpty()) {
                sendInterest(userId, name, btnYes)
            } else {
                Toast.makeText(this, "Interest sent to $name!", Toast.LENGTH_SHORT).show()
                btnYes.text = "Interest Sent"
                btnYes.isEnabled = false
            }
        }
        btnNo.setOnClickListener {
            Toast.makeText(this, "Skipped", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadFullProfile(userId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfileById(
                    token  = sessionManager.getBearerToken(),
                    userId = userId
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user ?: return@launch

                    // Update UI with real data
                    user.basicInformation?.let { basic ->
                        val heightText = basic.heightCm?.let { "${it / 30} ft ${it % 30} in" } ?: ""
                        // Update detail rows if needed
                    }
                    user.aboutSection?.let { about ->
                        // about.bio available
                    }
                }
            } catch (e: Exception) {
                // Silently fail — intent data already shown
            }
        }
    }

    private fun sendInterest(targetUserId: String, name: String, btnYes: Button) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.sendInterest(
                    token        = sessionManager.getBearerToken(),
                    targetUserId = targetUserId
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@MatchDetailActivity, "Interest sent to $name!", Toast.LENGTH_SHORT).show()
                    btnYes.text      = "Interest Sent"
                    btnYes.isEnabled = false
                } else {
                    val msg = response.body()?.message ?: "Failed"
                    Toast.makeText(this@MatchDetailActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MatchDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun trackView(targetUserId: String) {
        lifecycleScope.launch {
            try {
                RetrofitClient.apiService.trackProfileView(
                    token        = sessionManager.getBearerToken(),
                    targetUserId = targetUserId
                )
            } catch (e: Exception) { /* silent */ }
        }
    }
}
