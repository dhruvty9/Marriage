package com.digitalinappraigar.app

import android.os.Bundle
import android.util.Log
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.network.models.*
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import com.google.gson.Gson
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

        findViewById<TextView>(R.id.tvDetailName).text = name

        if (photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .centerCrop()
                .into(findViewById<ImageView>(R.id.ivDetailPhoto))
        }

        if (userId.isNotEmpty()) {
            loadFullProfile(userId)
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
                if (!isOnline()) {
                    Toast.makeText(this@MatchDetailActivity, "No network connection", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val response = RetrofitClient.apiService.getProfileById(
                    token  = sessionManager.getBearerToken(),
                    userId = userId
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data ?: return@launch
                    Log.d("MatchDetailActivity", "Profile loaded successfully for user: ${user.fullName}")
                } else {
                    val code = response.code()
                    val msg = if (response.isSuccessful) {
                        response.body()?.message
                    } else {
                        parseErrorMessage(response.errorBody()?.string())
                    } ?: "Server error ($code)"
                    
                    Log.e("MatchDetailActivity", "getProfileById failed: code=$code, msg=$msg")
                    Toast.makeText(this@MatchDetailActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Unknown network error"
                Log.e("MatchDetailActivity", "loadFullProfile failed: $msg", e)
                Toast.makeText(this@MatchDetailActivity, "Profile load failed: $msg", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendInterest(targetUserId: String, name: String, btnYes: Button) {
        lifecycleScope.launch {
            try {
                if (!isOnline()) {
                    Toast.makeText(this@MatchDetailActivity, "No network connection", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val response = RetrofitClient.apiService.sendInterest(
                    token        = sessionManager.getBearerToken(),
                    targetUserId = targetUserId
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@MatchDetailActivity, response.body()?.message ?: "Interest sent to $name!", Toast.LENGTH_SHORT).show()
                    btnYes.text      = "Interest Sent"
                    btnYes.isEnabled = false
                } else {
                    val code = response.code()
                    val msg = if (response.isSuccessful) {
                        response.body()?.message
                    } else {
                        parseErrorMessage(response.errorBody()?.string())
                    } ?: "Failed (code $code)"

                    Log.e("MatchDetailActivity", "sendInterest failed: code=$code, msg=$msg")
                    Toast.makeText(this@MatchDetailActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Network error"
                Log.e("MatchDetailActivity", "sendInterest failed: $msg", e)
                Toast.makeText(this@MatchDetailActivity, "Network error: $msg", Toast.LENGTH_LONG).show()
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
            } catch (e: Exception) {
                Log.e("MatchDetailActivity", "trackView failed: ${e.message}", e)
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody == null) return null
        return try {
            val apiResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
            apiResponse.message
        } catch (e: Exception) {
            null
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val nw = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nw) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
