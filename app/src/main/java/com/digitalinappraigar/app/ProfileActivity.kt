package com.digitalinappraigar.app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import android.graphics.Color
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        window.statusBarColor = Color.parseColor("#8B0000")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        sessionManager = SessionManager(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        
        // Initial set from session
        updateUI()

        btnBack.setOnClickListener { finish() }

        // Fetch fresh data from backend
        fetchProfileData()
    }

    private fun fetchProfileData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile(sessionManager.getBearerToken())
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { user ->
                        sessionManager.saveUser(user)
                        updateUI()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to refresh profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        val user = sessionManager.getUser()
        findViewById<TextView>(R.id.tvProfileName).text = user?.fullName ?: "Unknown"
        findViewById<TextView>(R.id.tvProfilePhone).text = user?.mobileNumber ?: ""
    }
}
