package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.digitalinappraigarvivah.app.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("OnboardingPrefs", MODE_PRIVATE)
        val hasSeenOnboarding = sharedPreferences.getBoolean("hasSeenOnboarding", false)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!hasSeenOnboarding) {
                startActivity(Intent(this, Onboarding1Activity::class.java))
            } else {
                // ALWAYS go to Login Page first as requested
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, SPLASH_DELAY)
    }
}
