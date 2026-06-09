package com.digitalinappraigar.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import kotlinx.coroutines.launch
import java.util.Calendar

class BasicActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basic)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val editAge = findViewById<EditText>(R.id.editAge)
        val editDob = findViewById<EditText>(R.id.editDob)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val radioMale = findViewById<RadioButton>(R.id.radioMale)
        val radioFemale = findViewById<RadioButton>(R.id.radioFemale)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnBack.setOnClickListener { finish() }

        // Date Picker for DOB
        editDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selectedDate = "$day/${month + 1}/$year"
                    editDob.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getBearerToken()

        // 1. Fetch existing data from Backend
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRegistrationStatus(token)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data as? Map<String, Any>
                    data?.let {
                        val ageVal = it["age"]?.toString()?.replace(".0", "") ?: ""
                        editAge.setText(ageVal)
                        editDob.setText(it["dateOfBirth"]?.toString() ?: "")
                        editEmail.setText(it["email"]?.toString() ?: "")
                        
                        val gender = it["gender"]?.toString() ?: ""
                        if (gender.equals("Male", true)) radioMale.isChecked = true
                        else if (gender.equals("Female", true)) radioFemale.isChecked = true
                    }
                }
            } catch (e: Exception) {
                Log.e("BasicActivity", "Initial fetch failed: ${e.message}")
            }
        }

        // 2. Continue with Validation and Save
        btnContinue.setOnClickListener {
            val age = editAge.text.toString().trim()
            val dob = editDob.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val gender = if (radioMale.isChecked) "Male" else if (radioFemale.isChecked) "Female" else ""

            if (age.isEmpty()) { Toast.makeText(this, "Please enter age", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (dob.isEmpty()) { Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (gender.isEmpty()) { Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            val dataMap = mapOf(
                "age" to age,
                "dateOfBirth" to dob,
                "email" to email,
                "gender" to gender
            )

            lifecycleScope.launch {
                try {
                    btnContinue.isEnabled = false
                    // FIXED: Using saveRegistrationStep (POST /api/registration/complete) to avoid 404
                    val response = RetrofitClient.apiService.saveRegistrationStep(token, dataMap)
                    if (response.isSuccessful) {
                        startActivity(Intent(this@BasicActivity, ReligionDetailsActivity::class.java))
                    } else {
                        Toast.makeText(this@BasicActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("BasicActivity", "Update error", e)
                    Toast.makeText(this@BasicActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnContinue.isEnabled = true
                }
            }
        }
    }
}
