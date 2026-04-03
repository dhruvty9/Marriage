package com.example.marriage

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonalDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            val intent = Intent(this, ProfessionalDetailsActivity::class.java)
            startActivity(intent)
        }

        setupSpinner(R.id.spinnerMaritalStatus, arrayOf("Select Status", "No", "Yes", "Divorced", "Widowed"))
        setupSpinner(R.id.spinnerNumChildren, arrayOf("Select Number", "0", "1", "2", "3", "4+"))
        setupSpinner(R.id.spinnerFamilyStatus, arrayOf("Select Status", "Middle Class", "Upper Middle Class", "Rich", "Affluent"))
        setupSpinner(R.id.spinnerFamilyType, arrayOf("Select Type", "Joint Family", "Nuclear Family", "Other"))

        // Add background color logic for "Is Children living with you?"
        val radioGroup = findViewById<android.widget.RadioGroup>(R.id.radioGroupChildrenLiving)
        val radioYes = findViewById<android.widget.RadioButton>(R.id.radioYes)
        val radioNo = findViewById<android.widget.RadioButton>(R.id.radioNo)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioYes -> {
                    // Set Yes button to Green, No button to Transparent
                    radioYes.setBackgroundColor(android.graphics.Color.parseColor("#C8E6C9"))
                    radioNo.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
                R.id.radioNo -> {
                    // Set No button to Red, Yes button to Transparent
                    radioNo.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))
                    radioYes.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            }
        }
    }

    private fun setupSpinner(spinnerId: Int, items: Array<String>) {
        val spinner = findViewById<Spinner>(spinnerId)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}