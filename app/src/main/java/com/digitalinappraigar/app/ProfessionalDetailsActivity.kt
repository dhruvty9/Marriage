package com.digitalinappraigar.app

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        val spEdu = findViewById<Spinner>(R.id.spinnerHighestEducation)
        val spEmp = findViewById<Spinner>(R.id.spinnerEmployedIn)
        val spOcc = findViewById<Spinner>(R.id.spinnerOccupation)
        val spInc = findViewById<Spinner>(R.id.spinnerAnnualIncome)
        val spState = findViewById<Spinner>(R.id.spinnerState)
        val spCity = findViewById<Spinner>(R.id.spinnerWorkLocation)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getBearerToken()

        // 1. Load data and setup dynamic State/City
        lifecycleScope.launch {
            try {
                val configResp = RetrofitClient.apiService.getConfigOptions()
                if (configResp.isSuccessful) {
                    val configData = configResp.body()?.data
                    val statesMap = configData?.states ?: emptyMap()

                    // Populate States
                    val statesList = mutableListOf("Select State")
                    statesList.addAll(statesMap.keys)
                    val stateAdapter = ArrayAdapter(this@ProfessionalDetailsActivity, R.layout.spinner_item, statesList)
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spState.adapter = stateAdapter

                    spState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedState = statesList[position]
                            val cities = mutableListOf("Select City")
                            cities.addAll(statesMap[selectedState] ?: emptyList())
                            val cityAdapter = ArrayAdapter(this@ProfessionalDetailsActivity, R.layout.spinner_item, cities)
                            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spCity.adapter = cityAdapter
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

                    // Populate other spinners
                    setupConfigSpinner(spEdu, configData?.educations, "Select Education")
                    setupConfigSpinner(spEmp, configData?.jobTypes, "Select Sector")
                    setupConfigSpinner(spOcc, configData?.occupations, "Select Occupation")
                    
                    val incomeLabels = configData?.annualIncomes?.map { it.label } ?: emptyList()
                    setupConfigSpinner(spInc, incomeLabels, "Select Income")

                    // 2. Fetch existing saved data
                    val statusResp = RetrofitClient.apiService.getRegistrationStatus(token)
                    if (statusResp.isSuccessful) {
                        val data = statusResp.body()?.data as? Map<String, Any>
                        data?.let {
                            setSpinnerSelection(spEdu, it["education"]?.toString())
                            setSpinnerSelection(spEmp, it["employedIn"]?.toString())
                            setSpinnerSelection(spOcc, it["occupation"]?.toString())
                            setSpinnerSelection(spInc, it["annualIncome"]?.toString())
                            
                            val savedState = it["state"]?.toString()
                            val statePos = stateAdapter.getPosition(savedState)
                            if (statePos >= 0) {
                                spState.setSelection(statePos)
                            }
                        }
                    }
                }
            } catch (e: Exception) { }
        }

        btnContinue.setOnClickListener {
            val edu = spEdu.selectedItem.toString()
            val emp = spEmp.selectedItem.toString()
            val occ = spOcc.selectedItem.toString()
            val inc = spInc.selectedItem.toString()
            val state = spState.selectedItem.toString()
            val city = spCity.selectedItem.toString()

            if (edu.startsWith("Select")) { Toast.makeText(this, "Select education", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (state.startsWith("Select")) { Toast.makeText(this, "Select state", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            val data = mapOf(
                "highestEducation" to edu,
                "employedIn" to emp,
                "occupation" to occ,
                "annualIncome" to inc,
                "state" to state,
                "city" to city
            )

            lifecycleScope.launch {
                try {
                    btnContinue.isEnabled = false
                    // FIXED: Using saveRegistrationStep to avoid 404
                    val response = RetrofitClient.apiService.saveRegistrationStep(token, data)
                    if (response.isSuccessful) {
                        startActivity(Intent(this@ProfessionalDetailsActivity, AboutYourselfActivity::class.java))
                    } else {
                        Toast.makeText(this@ProfessionalDetailsActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ProfessionalDetailsActivity, "Connection error", Toast.LENGTH_SHORT).show()
                } finally {
                    btnContinue.isEnabled = true
                }
            }
        }
    }

    private fun setupConfigSpinner(spinner: Spinner, items: List<String>?, default: String) {
        val list = mutableListOf(default)
        list.addAll(items ?: emptyList())
        val adapter = ArrayAdapter(this, R.layout.spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        if (value == null || spinner.adapter == null) return
        val adapter = spinner.adapter as ArrayAdapter<String>
        val pos = adapter.getPosition(value)
        if (pos >= 0) spinner.setSelection(pos)
    }
}
