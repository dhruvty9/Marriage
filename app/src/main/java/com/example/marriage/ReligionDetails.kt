package com.example.marriage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReligionDetailsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_religion_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.widget.Button>(R.id.btnContinue).setOnClickListener {
            val intent = android.content.Intent(this, PersonalDetailsActivity::class.java)
            startActivity(intent)
        }

        val spinnerCaste = findViewById<android.widget.Spinner>(R.id.spinnerCaste)
        val castes = arrayOf("Select Caste", "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Other")
        val adapter = android.widget.ArrayAdapter(this, R.layout.spinner_item, castes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCaste.adapter = adapter
    }
}