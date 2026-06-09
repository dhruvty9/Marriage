package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.digitalinappraigarvivah.app.R

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val ivBack      = findViewById<ImageView>(R.id.ivBack)
        val btnGoHome   = findViewById<Button>(R.id.btnGoToHome)
        val btnComplete = findViewById<Button>(R.id.btnComplete)

        ivBack.setOnClickListener { finish() }

        btnGoHome.setOnClickListener { goToHome() }
        btnComplete.setOnClickListener { goToHome() }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
