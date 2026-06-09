package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import kotlinx.coroutines.launch

class ReligionDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_religion_details)

        val spinnerCaste = findViewById<Spinner>(R.id.spinnerCaste)
        val editSubCaste = findViewById<EditText>(R.id.editSubCaste)
        val radioYes = findViewById<RadioButton>(R.id.radioYes)
        val radioNo = findViewById<RadioButton>(R.id.radioNo)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getBearerToken()

        // 1. Fetch Config and existing data
        lifecycleScope.launch {
            try {
                // Populate castes
                val castes = mutableListOf("Select Caste", "Brahmin", "Kshatriya", "Vaishya", "Shudra", "Other")
                val adapter = ArrayAdapter(this@ReligionDetailsActivity, R.layout.spinner_item, castes)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCaste.adapter = adapter

                // Load saved data
                val statusResp = RetrofitClient.apiService.getRegistrationStatus(token)
                if (statusResp.isSuccessful) {
                    val data = statusResp.body()?.data as? Map<String, Any>
                    data?.let {
                        editSubCaste.setText(it["subCaste"]?.toString() ?: "")
                        val caste = it["caste"]?.toString() ?: ""
                        val pos = adapter.getPosition(caste)
                        if (pos >= 0) spinnerCaste.setSelection(pos)
                        
                        if (it["dosham"] == "Yes") radioYes.isChecked = true
                        else radioNo.isChecked = true
                    }
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }

        btnContinue.setOnClickListener {
            val caste = spinnerCaste.selectedItem.toString()
            val subCaste = editSubCaste.text.toString().trim()
            val dosham = if (radioYes.isChecked) "Yes" else "No"

            if (caste == "Select Caste") {
                Toast.makeText(this, "Please select caste", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = mapOf(
                "caste" to caste,
                "subCaste" to subCaste,
                "dosham" to dosham
            )

            lifecycleScope.launch {
                try {
                    btnContinue.isEnabled = false
                    // FIXED: Using saveRegistrationStep to avoid 404 (doesn't require userId in URL)
                    val response = RetrofitClient.apiService.saveRegistrationStep(token, data)
                    if (response.isSuccessful) {
                        startActivity(Intent(this@ReligionDetailsActivity, PersonalDetailsActivity::class.java))
                    } else {
                        Toast.makeText(this@ReligionDetailsActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ReligionDetailsActivity, "Connection error", Toast.LENGTH_SHORT).show()
                } finally {
                    btnContinue.isEnabled = true
                }
            }
        }
    }
}
