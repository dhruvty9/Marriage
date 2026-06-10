package com.example.marriage

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProfessionalDetailsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var spinnerHighestEducation: Spinner
    private lateinit var spinnerEmployedIn: Spinner
    private lateinit var spinnerOccupation: Spinner
    private lateinit var spinnerAnnualIncome: Spinner
    private lateinit var spinnerState: Spinner
    private lateinit var spinnerCity: Spinner
    
    private var pendingCity: String? = null
    private var isFirstLoad = true

    // 1. Data Mapping: All Indian States & Their Main Cities
    private val stateCityMap = mapOf(
        "Select State" to listOf("Select City"),
        "Andhra Pradesh" to listOf("Visakhapatnam", "Vijayawada", "Guntur", "Nellore", "Tirupati"),
        "Arunachal Pradesh" to listOf("Itanagar", "Naharlagun", "Pasighat", "Roing"),
        "Assam" to listOf("Guwahati", "Dibrugarh", "Silchar", "Jorhat", "Nagaon"),
        "Bihar" to listOf("Patna", "Gaya", "Bhagalpur", "Muzaffarpur", "Purnia"),
        "Chhattisgarh" to listOf("Raipur", "Bhilai", "Bilaspur", "Korba", "Durg"),
        "Goa" to listOf("Panaji", "Margao", "Vasco da Gama", "Mapusa"),
        "Gujarat" to listOf("Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar"),
        "Haryana" to listOf("Gurugram", "Faridabad", "Panipat", "Ambala", "Hisar"),
        "Himachal Pradesh" to listOf("Shimla", "Dharamshala", "Solan", "Mandi"),
        "Jharkhand" to listOf("Ranchi", "Jamshedpur", "Dhanbad", "Bokaro", "Deoghar"),
        "Karnataka" to listOf("Bengaluru", "Mysuru", "Hubballi", "Mangaluru", "Belagavi"),
        "Kerala" to listOf("Thiruvananthapuram", "Kochi", "Kozhikode", "Thrissur"),
        "Madhya Pradesh" to listOf("Indore", "Bhopal", "Jabalpur", "Gwalior", "Ujjain"),
        "Maharashtra" to listOf("Mumbai", "Pune", "Nagpur", "Nashik", "Thane", "Aurangabad"),
        "Manipur" to listOf("Imphal", "Thoubal", "Bishnupur"),
        "Meghalaya" to listOf("Shillong", "Tura", "Jowai"),
        "Mizoram" to listOf("Aizawl", "Lunglei", "Champhai"),
        "Nagaland" to listOf("Kohima", "Dimapur", "Mokokchung"),
        "Odisha" to listOf("Bhubaneswar", "Cuttack", "Rourkela", "Berhampur"),
        "Punjab" to listOf("Ludhiana", "Amritsar", "Jalandhar", "Patiala", "Bathinda"),
        "Rajasthan" to listOf("Jaipur", "Jodhpur", "Kota", "Bikaner", "Ajmer", "Udaipur"),
        "Sikkim" to listOf("Gangtok", "Namchi", "Geyzing"),
        "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem"),
        "Telangana" to listOf("Hyderabad", "Warangal", "Nizamabad", "Khammam", "Karimnagar"),
        "Tripura" to listOf("Agartala", "Udaipur", "Dharmanagar"),
        "Uttar Pradesh" to listOf("Lucknow", "Kanpur", "Noida", "Ghaziabad", "Agra", "Varanasi"),
        "Uttarakhand" to listOf("Dehradun", "Haridwar", "Roorkee", "Haldwani"),
        "West Bengal" to listOf("Kolkata", "Howrah", "Asansol", "Siliguri", "Durgapur"),
        "Delhi" to listOf("New Delhi", "North Delhi", "South Delhi", "West Delhi", "East Delhi")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_professional_details)
        
        sessionManager = SessionManager(this)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        val btnBack = findViewById<android.widget.ImageView>(R.id.btnBack)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        spinnerState = findViewById(R.id.spinnerState)
        spinnerCity = findViewById(R.id.spinnerWorkLocation)
        spinnerHighestEducation = findViewById(R.id.spinnerHighestEducation)
        spinnerEmployedIn = findViewById(R.id.spinnerEmployedIn)
        spinnerOccupation = findViewById(R.id.spinnerOccupation)
        spinnerAnnualIncome = findViewById(R.id.spinnerAnnualIncome)

        btnBack.setOnClickListener { finish() }
        btnContinue.setOnClickListener {
            saveProfessionalData()
        }

        // 2. Setup Dropdowns
        setupSpinner(spinnerHighestEducation, arrayOf("Select Education", "B.Tech", "B.Sc", "B.Com", "B.A", "M.Tech", "MBA", "PhD", "Other"))
        setupSpinner(spinnerEmployedIn, arrayOf("Select Sector", "Private Sector", "Government Sector", "Business", "Not Working"))
        setupSpinner(spinnerOccupation, arrayOf("Select Occupation", "Software Engineer", "Doctor", "Teacher", "Business", "Other"))
        setupSpinner(spinnerAnnualIncome, arrayOf("Select Income", "Less than 2L", "2-5L", "5-10L", "10-20L", "20L+"))

        // 3. Setup State & City Dropdowns
        val statesList = stateCityMap.keys.toList()
        val stateAdapter = ArrayAdapter(this, R.layout.spinner_item, statesList)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerState.adapter = stateAdapter

        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedState = statesList[position]
                val cities = stateCityMap[selectedState] ?: listOf("Select City")
                
                val cityAdapter = ArrayAdapter(this@ProfessionalDetailsActivity, R.layout.spinner_item, cities)
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCity.adapter = cityAdapter

                // Handle pre-fill for city
                if (isFirstLoad && pendingCity != null) {
                    setSpinnerValue(spinnerCity, pendingCity)
                    pendingCity = null
                    isFirstLoad = false
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fetchExistingData()
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setSpinnerValue(spinner: Spinner, value: String?) {
        value?.let {
            val adapter = spinner.adapter as? ArrayAdapter<String>
            val pos = adapter?.getPosition(it) ?: -1
            if (pos >= 0) spinner.setSelection(pos)
        }
    }

    private fun fetchExistingData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile(sessionManager.getBearerToken())
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    
                    user?.professionalDetails?.let { prof ->
                        setSpinnerValue(spinnerHighestEducation, prof.education)
                        setSpinnerValue(spinnerOccupation, prof.occupation)
                        setSpinnerValue(spinnerEmployedIn, prof.jobType)
                        
                        // Map Long income back to Spinner String
                        val incomeText = when {
                            prof.annualIncome == null -> "Select Income"
                            prof.annualIncome <= 200000L -> "Less than 2L"
                            prof.annualIncome <= 500000L -> "2-5L"
                            prof.annualIncome <= 1000000L -> "5-10L"
                            prof.annualIncome <= 2000000L -> "10-20L"
                            else -> "20L+"
                        }
                        setSpinnerValue(spinnerAnnualIncome, incomeText)
                    }
                    
                    user?.locationDetails?.let { loc ->
                        pendingCity = loc.currentCity
                        setSpinnerValue(spinnerState, loc.state)
                    }
                }
            } catch (e: Exception) {
                // Silent fail for fetch
            }
        }
    }

    private fun saveProfessionalData() {
        val education = spinnerHighestEducation.selectedItem.toString()
        val sector = spinnerEmployedIn.selectedItem.toString()
        val occupation = spinnerOccupation.selectedItem.toString()
        val incomeStr = spinnerAnnualIncome.selectedItem.toString()
        val state = spinnerState.selectedItem.toString()
        val city = spinnerCity.selectedItem.toString()

        if (education == "Select Education" || sector == "Select Sector" || 
            occupation == "Select Occupation" || incomeStr == "Select Income" || 
            state == "Select State" || city == "Select City") {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val incomeValue = when (incomeStr) {
            "Less than 2L" -> 200000L
            "2-5L" -> 500000L
            "5-10L" -> 1000000L
            "10-20L" -> 2000000L
            "20L+" -> 5000000L
            else -> 0L
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.isEnabled = false
        btnContinue.text = "Saving..."

        lifecycleScope.launch {
            try {
                val data = mapOf(
                    "professionalDetails" to mapOf(
                        "education" to education,
                        "occupation" to occupation,
                        "jobType" to sector,
                        "annualIncome" to incomeValue
                    ),
                    "locationDetails" to mapOf(
                        "state" to state,
                        "currentCity" to city
                    ),
                    "profileCompletedSteps" to 4
                )
                
                val response = RetrofitClient.apiService.completeRegistration(sessionManager.getBearerToken(), data)
                if (response.isSuccessful) {
                    startActivity(Intent(this@ProfessionalDetailsActivity, AboutYourselfActivity::class.java))
                } else {
                    val errorMsg = try {
                        val errorObj = JSONObject(response.errorBody()?.string() ?: "{}")
                        errorObj.optString("message", "Failed to save data")
                    } catch (e: Exception) {
                        "Server error: ${response.code()}"
                    }
                    Toast.makeText(this@ProfessionalDetailsActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfessionalDetailsActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                btnContinue.isEnabled = true
                btnContinue.text = "Continue"
            }
        }
    }
}
