package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.marriage.utils.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 2 second wait then navigate
        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(this)
            
            val intent = if (sessionManager.isLoggedIn()) {
                val user = sessionManager.getUser()
                
                if (user == null) {
                    // Agar login hai par user profile info nahi hai, toh wapas Login par bhejein
                    Intent(this, LoginActivity::class.java)
                } else if (user.profileStatus == "PENDING" || user.profileStatus == "INCOMPLETE") {
                    // Agar profile incomplete hai toh BasicActivity par bhejein
                    Intent(this, BasicActivity::class.java)
                } else {
                    // Agar sab sahi hai toh HomeActivity par
                    Intent(this, HomeActivity::class.java)
                }
            } else {
                // Not logged in
                Intent(this, LoginActivity::class.java)
            }
            
            startActivity(intent)
            finish()
        }, 2000)
    }
}
