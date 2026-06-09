package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
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

        val spMarital = findViewById<Spinner>(R.id.spinnerMaritalStatus)
        val spChildren = findViewById<Spinner>(R.id.spinnerNumChildren)
        val spFamStatus = findViewById<Spinner>(R.id.spinnerFamilyStatus)
        val spFamType = findViewById<Spinner>(R.id.spinnerFamilyType)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val radioYes = findViewById<RadioButton>(R.id.radioYes)
        val radioNo = findViewById<RadioButton>(R.id.radioNo)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getBearerToken()

        // 1. Setup Spinners
        val maritalItems = arrayOf("Select Status", "Never Married", "Divorced", "Widowed", "Awaiting Divorce")
        val childItems = arrayOf("Select Number", "0", "1", "2", "3", "4+")
        val familyStatusItems = arrayOf("Select Status", "Middle Class", "Upper Middle Class", "Rich", "Affluent")
        val familyTypeItems = arrayOf("Select Type", "Joint Family", "Nuclear Family", "Other")

        setupSpinner(spMarital, maritalItems)
        setupSpinner(spChildren, childItems)
        setupSpinner(spFamStatus, familyStatusItems)
        setupSpinner(spFamType, familyTypeItems)

        // 2. Fetch data
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRegistrationStatus(token)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data as? Map<String, Any>
                    data?.let {
                        etHeight.setText(it["height"]?.toString() ?: "")
                        
                        setSpinnerSelection(spMarital, it["maritalStatus"]?.toString())
                        setSpinnerSelection(spChildren, it["noOfChildren"]?.toString())
                        setSpinnerSelection(spFamStatus, it["familyStatus"]?.toString())
                        setSpinnerSelection(spFamType, it["familyType"]?.toString())

                        if (it["childrenLivingWithYou"] == "Yes") radioYes.isChecked = true
                        else if (it["childrenLivingWithYou"] == "No") radioNo.isChecked = true
                    }
                }
            } catch (e: Exception) { }
        }

        btnContinue.setOnClickListener {
            val marital = spMarital.selectedItem.toString()
            val children = spChildren.selectedItem.toString()
            val famStatus = spFamStatus.selectedItem.toString()
            val famType = spFamType.selectedItem.toString()
            val height = etHeight.text.toString().trim()
            val childrenLiving = if (radioYes.isChecked) "Yes" else "No"

            if (marital.startsWith("Select")) { Toast.makeText(this, "Select marital status", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (height.isEmpty()) { Toast.makeText(this, "Enter height", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            val data = mapOf(
                "maritalStatus" to marital,
                "noOfChildren" to children,
                "childrenLivingWithYou" to childrenLiving,
                "height" to height,
                "familyStatus" to famStatus,
                "familyType" to famType
            )

            lifecycleScope.launch {
                try {
                    btnContinue.isEnabled = false
                    // FIXED: Using saveRegistrationStep to avoid 404
                    val response = RetrofitClient.apiService.saveRegistrationStep(token, data)
                    if (response.isSuccessful) {
                        startActivity(Intent(this@PersonalDetailsActivity, ProfessionalDetailsActivity::class.java))
                    } else {
                        Toast.makeText(this@PersonalDetailsActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PersonalDetailsActivity, "Connection error", Toast.LENGTH_SHORT).show()
                } finally {
                    btnContinue.isEnabled = true
                }
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        if (value == null) return
        val adapter = spinner.adapter as ArrayAdapter<String>
        val pos = adapter.getPosition(value)
        if (pos >= 0) spinner.setSelection(pos)
    }
}
