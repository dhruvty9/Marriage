package com.example.marriage

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

class PersonalDetailsActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    private lateinit var spinnerMaritalStatus: Spinner
    private lateinit var spinnerNumChildren: Spinner
    private lateinit var spinnerFamilyStatus: Spinner
    private lateinit var spinnerFamilyType: Spinner
    private lateinit var radioGroupChildrenLiving: RadioGroup
    private lateinit var etHeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_details)
        
        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        spinnerMaritalStatus = findViewById(R.id.spinnerMaritalStatus)
        spinnerNumChildren = findViewById(R.id.spinnerNumChildren)
        spinnerFamilyStatus = findViewById(R.id.spinnerFamilyStatus)
        spinnerFamilyType = findViewById(R.id.spinnerFamilyType)
        radioGroupChildrenLiving = findViewById(R.id.radioGroupChildrenLiving)
        etHeight = findViewById(R.id.etHeight)

        setupSpinner(spinnerMaritalStatus, arrayOf("Select Status", "Never Married", "Divorced", "Widowed", "Separated"))
        setupSpinner(spinnerNumChildren, arrayOf("Select Number", "None", "1", "2", "3", "4+"))
        setupSpinner(spinnerFamilyStatus, arrayOf("Select Status", "Middle Class", "Upper Middle Class", "Rich", "Affluent"))
        setupSpinner(spinnerFamilyType, arrayOf("Select Type", "Joint Family", "Nuclear Family", "Other"))

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            savePersonalData()
        }

        // Handle radio button background changes
        val radioYes = findViewById<RadioButton>(R.id.radioYes)
        val radioNo = findViewById<RadioButton>(R.id.radioNo)
        radioGroupChildrenLiving.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioYes -> {
                    radioYes.setBackgroundColor(android.graphics.Color.parseColor("#C8E6C9"))
                    radioNo.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
                R.id.radioNo -> {
                    radioNo.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))
                    radioYes.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            }
        }
        
        fetchExistingData()
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun fetchExistingData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile(sessionManager.getBearerToken())
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user ?: response.body()?.data
                    user?.personalDetails?.let { personal ->
                        setSpinnerValue(spinnerMaritalStatus, personal.maritalStatus)
                        setSpinnerValue(spinnerNumChildren, personal.noOfChildren?.let { if (it == 0) "None" else if (it >= 4) "4+" else it.toString() })
                        setSpinnerValue(spinnerFamilyStatus, personal.familyStatus)
                        setSpinnerValue(spinnerFamilyType, personal.familyType)
                        etHeight.setText(personal.height.orEmpty())
                        when (personal.childrenLivingWithYou) {
                            true -> radioGroupChildrenLiving.check(R.id.radioYes)
                            false -> radioGroupChildrenLiving.check(R.id.radioNo)
                            null -> Unit
                        }
                    } ?: run {
                        user?.basicInformation?.maritalStatus?.let { setSpinnerValue(spinnerMaritalStatus, it) }
                    }
                }
            } catch (e: Exception) {}
        }
    }

    private fun savePersonalData() {
        val maritalStatus = spinnerMaritalStatus.selectedItem.toString()
        val noOfChildren = parseChildrenCount(spinnerNumChildren.selectedItem.toString())
        val selectedChildrenLivingId = radioGroupChildrenLiving.checkedRadioButtonId
        val childrenLivingWithYou = when (selectedChildrenLivingId) {
            R.id.radioYes -> true
            R.id.radioNo -> false
            else -> null
        }
        val height = etHeight.text.toString().trim()
        val familyStatus = spinnerFamilyStatus.selectedItem.toString()
        val familyType = spinnerFamilyType.selectedItem.toString()
        
        if (
            maritalStatus == "Select Status" ||
            noOfChildren == null ||
            childrenLivingWithYou == null ||
            height.isEmpty() ||
            familyStatus == "Select Status" ||
            familyType == "Select Type"
        ) {
            Toast.makeText(this, "Please select all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.isEnabled = false
        btnContinue.text = "Saving..."

        lifecycleScope.launch {
            try {
                val data = mapOf(
                    "maritalStatus" to maritalStatus,
                    "noOfChildren" to noOfChildren,
                    "childrenLivingWithYou" to childrenLivingWithYou,
                    "height" to height,
                    "familyStatus" to familyStatus,
                    "familyType" to familyType
                )
                
                val response = RetrofitClient.apiService.updatePersonalDetails(sessionManager.getBearerToken(), data)
                if (response.isSuccessful && response.body()?.success == true) {
                    startActivity(Intent(this@PersonalDetailsActivity, ProfessionalDetailsActivity::class.java))
                } else {
                    val errorMessage = response.body()?.message ?: parseErrorMessage(response.errorBody()?.string())
                    Toast.makeText(this@PersonalDetailsActivity, errorMessage ?: "Failed to save", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PersonalDetailsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            } finally {
                btnContinue.isEnabled = true
                btnContinue.text = "Continue"
            }
        }
    }

    private fun parseChildrenCount(value: String): Int? {
        return when (value) {
            "None" -> 0
            "4+" -> 4
            "Select Number" -> null
            else -> value.toIntOrNull()
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
