package com.example.marriage

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
class ProfessionalDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_professional_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            // FIX: This opens the NEXT page
            val intent = Intent(this, AboutYourselfActivity::class.java)
            startActivity(intent)
        }


        setupSpinner(
            R.id.spinnerHighestEducation,
            arrayOf(
                "Select Education",
                "B.Tech",
                "B.Sc",
                "B.Com",
                "B.A",
                "M.Tech",
                "MBA",
                "PhD",
                "Other"
            )
        )
        setupSpinner(
            R.id.spinnerEmployedIn,
            arrayOf(
                "Select Sector",
                "Private Sector",
                "Government/Public Sector",
                "Business/Self Employed",
                "Not Working"
            )
        )
        setupSpinner(
            R.id.spinnerOccupation,
            arrayOf(
                "Select Occupation",
                "Software Engineer",
                "Doctor",
                "Teacher",
                "Business",
                "Other"
            )
        )
        setupSpinner(
            R.id.spinnerAnnualIncome,
            arrayOf(
                "Select Income",
                "Less than 2 Lakh",
                "2-5 Lakh",
                "5-10 Lakh",
                "More than 10 Lakh"
            )
        )
        setupSpinner(
            R.id.spinnerWorkLocation,
            arrayOf("Select Work Location", "Delhi", "Mumbai", "Bangalore", "Hyderabad","Noida", "Other")
        )
        setupSpinner(
            R.id.spinnerState,
            arrayOf("Select State", "Maharashtra", "Delhi", "Karnataka", "Telangana", "Uttar Pradesh ","Other")
        )
    }

    private fun setupSpinner(spinnerId: Int, items: Array<String>) {
        val spinner = findViewById<Spinner>(spinnerId)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

}