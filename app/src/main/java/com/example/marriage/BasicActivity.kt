package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class BasicActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    private lateinit var editAge: EditText
    private lateinit var editDob: EditText
    private lateinit var editEmail: EditText
    private lateinit var radioGroupGender: RadioGroup

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basic)
        
        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Date Picker for DOB
        editAge = findViewById(R.id.editAge)
        editDob = findViewById(R.id.editDob)
        editEmail = findViewById(R.id.editEmail)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        editDob.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            android.app.DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                editDob.setText(selectedDate)
            }, year, month, day).show()
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            saveBasicData()
        }
        
        // Fetch existing data
        fetchExistingData()
    }

    private fun fetchExistingData() {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val response = RetrofitClient.apiService.getMyProfile(token)
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    // Assuming DOB might be stored somewhere, though not explicitly in UserData model shown.
                    // If it's in user?.basicInformation, we would set it here.
                }
            } catch (e: Exception) {
                // Silently handle
            }
        }
    }

    private fun saveBasicData() {
        val age = editAge.text.toString().trim().toIntOrNull()
        val dob = editDob.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val selectedGenderId = radioGroupGender.checkedRadioButtonId
        val gender = if (selectedGenderId != -1) {
            findViewById<RadioButton>(selectedGenderId).text.toString()
        } else {
            ""
        }

        if (age == null || age < 18 || dob.isEmpty() || email.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all basic details correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.isEnabled = false
        btnContinue.text = "Saving..."

        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val data = mapOf(
                    "age" to age,
                    "dateOfBirth" to dob,
                    "email" to email,
                    "gender" to gender
                )
                
                val response = RetrofitClient.apiService.updateBasicDetails(token, data)
                if (response.isSuccessful && response.body()?.success == true) {
                    startActivity(Intent(this@BasicActivity, ReligionDetailsActivity::class.java))
                } else {
                    val errorMessage = response.body()?.message ?: parseErrorMessage(response.errorBody()?.string())
                    Toast.makeText(this@BasicActivity, errorMessage ?: "Failed to save data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BasicActivity, "Network Error", Toast.LENGTH_SHORT).show()
            } finally {
                btnContinue.isEnabled = true
                btnContinue.text = "Continue"
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            JSONObject(errorBody).optString("message").takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
