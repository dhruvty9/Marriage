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

class ProfessionalDetailsActivity : AppCompatActivity() {

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
        
        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        val btnBack = findViewById<android.widget.ImageView>(R.id.btnBack)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val spinnerState = findViewById<Spinner>(R.id.spinnerState)
        val spinnerCity = findViewById<Spinner>(R.id.spinnerWorkLocation)

        btnBack.setOnClickListener { finish() }
        btnContinue.setOnClickListener {
            val intent = Intent(this, AboutYourselfActivity::class.java)
            startActivity(intent)
        }

        // 2. Setup All Dropdowns
        setupSpinner(R.id.spinnerHighestEducation, arrayOf("Select Education", "B.Tech", "B.Sc", "B.Com", "B.A", "M.Tech", "MBA", "PhD", "Other"))
        setupSpinner(R.id.spinnerEmployedIn, arrayOf("Select Sector", "Private Sector", "Government Sector", "Business", "Not Working"))
        setupSpinner(R.id.spinnerOccupation, arrayOf("Select Occupation", "Software Engineer", "Doctor", "Teacher", "Business", "Other"))
        setupSpinner(R.id.spinnerAnnualIncome, arrayOf("Select Income", "Less than 2L", "2-5L", "5-10L", "10-20L", "20L+"))

        // 3. Setup Smart State & City Dropdowns
        val statesList = stateCityMap.keys.toTypedArray()
        val stateAdapter = ArrayAdapter(this, R.layout.spinner_item, statesList)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerState.adapter = stateAdapter

        spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedState = statesList[position]
                val cities = stateCityMap[selectedState] ?: listOf("Select City")
                
                // Update City Spinner with new cities
                val cityAdapter = ArrayAdapter(this@ProfessionalDetailsActivity, R.layout.spinner_item, cities)
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCity.adapter = cityAdapter
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSpinner(spinnerId: Int, items: Array<String>) {
        val spinner = findViewById<Spinner>(spinnerId)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

}