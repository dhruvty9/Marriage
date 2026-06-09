package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.digitalinappraigarvivah.app.R

class Onboarding1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_page_1)

        val tvSkip = findViewById<TextView>(R.id.tvSkip)
        val ivNext = findViewById<ImageView>(R.id.ivNext)

        tvSkip.setOnClickListener {
            navigateToLogin()
        }

        ivNext.setOnClickListener {
            navigateToOnboarding2()
        }
    }

    private fun navigateToOnboarding2() {
        val intent = Intent(this, Onboarding2Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val sharedPreferences = getSharedPreferences("OnboardingPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("hasSeenOnboarding", true).apply()
        
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
