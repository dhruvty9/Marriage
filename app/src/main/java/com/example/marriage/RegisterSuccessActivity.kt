package com.example.marriage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_success)

        val ivBack      = findViewById<ImageView>(R.id.ivBack)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val btnComplete = findViewById<Button>(R.id.btnComplete)

        ivBack.setOnClickListener { finish() }

        // Continue → go to Success screen
        btnContinue.setOnClickListener {
            startActivity(Intent(this, SuccessActivity::class.java))
        }

        // Complete → also go to Success
        btnComplete.setOnClickListener {
            startActivity(Intent(this, SuccessActivity::class.java))
        }
    }
}
