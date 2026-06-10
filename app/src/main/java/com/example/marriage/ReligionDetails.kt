package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class ReligionDetailsActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    private lateinit var spinnerCaste: Spinner
    private lateinit var editSubCaste: EditText
    private lateinit var radioGroupDosham: RadioGroup
    
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_religion_details)
        
        sessionManager = SessionManager(this)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        spinnerCaste = findViewById(R.id.spinnerCaste)
        editSubCaste = findViewById(R.id.editSubCaste)
        radioGroupDosham = findViewById(R.id.radioGroupDosham)
        
        val castes = arrayOf("Select Caste", "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Other")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, castes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCaste.adapter = adapter

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            saveReligionData()
        }
        
        fetchExistingData()
    }
    
    private fun fetchExistingData() {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val response = RetrofitClient.apiService.getMyProfile(token)
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user ?: response.body()?.data
                    user?.religionDetails?.let { religion ->
                        setSpinnerValue(spinnerCaste, religion.caste)
                        editSubCaste.setText(religion.subCaste.orEmpty())
                        when (religion.dosham) {
                            "Yes" -> radioGroupDosham.check(R.id.radioDoshamYes)
                            "No" -> radioGroupDosham.check(R.id.radioDoshamNo)
                            "Don't Know" -> radioGroupDosham.check(R.id.radioDoshamUnknown)
                        }
                    }
                }
            } catch (e: Exception) {}
        }
    }

    private fun saveReligionData() {
        val caste = spinnerCaste.selectedItem.toString()
        val subCaste = editSubCaste.text.toString().trim()
        val selectedDoshamId = radioGroupDosham.checkedRadioButtonId
        val dosham = if (selectedDoshamId != -1) {
            findViewById<RadioButton>(selectedDoshamId).text.toString()
        } else {
            ""
        }
        
        if (caste == "Select Caste" || dosham.isEmpty()) {
            Toast.makeText(this, "Please select Caste and Dosham", Toast.LENGTH_SHORT).show()
            return
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.isEnabled = false
        btnContinue.text = "Saving..."

        lifecycleScope.launch {
            try {
                val token = sessionManager.getBearerToken()
                val data = mapOf(
                    "caste" to caste,
                    "subCaste" to subCaste,
                    "dosham" to dosham
                )
                
                val response = RetrofitClient.apiService.updateReligionDetails(token, data)
                if (response.isSuccessful && response.body()?.success == true) {
                    startActivity(Intent(this@ReligionDetailsActivity, PersonalDetailsActivity::class.java))
                } else {
                    val errorMessage = response.body()?.message ?: parseErrorMessage(response.errorBody()?.string())
                    Toast.makeText(this@ReligionDetailsActivity, errorMessage ?: "Failed to save", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReligionDetailsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            } finally {
                btnContinue.isEnabled = true
                btnContinue.text = "Continue"
            }
        }
    }

    private fun setSpinnerValue(spinner: Spinner, value: String?) {
        if (value.isNullOrBlank()) return
        val adapter = spinner.adapter ?: return
        for (position in 0 until adapter.count) {
            if (adapter.getItem(position)?.toString() == value) {
                spinner.setSelection(position)
                return
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
