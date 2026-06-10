package com.example.marriage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch

class AboutYourselfActivity : AppCompatActivity() {
    
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var ivProfilePreview: ImageView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_yourself)

        sessionManager = SessionManager(this)

        // 1. Initialize result launcher for photo picking
        ivProfilePreview = findViewById(R.id.ivProfilePreview)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // Set the image on the preview box
                ivProfilePreview.setImageURI(uri)
                Toast.makeText(this, "Photo Selected!", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Edge-to-edge padding support
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Initialize Views
        val ivBack = findViewById<android.widget.ImageView>(R.id.ivBack)
        val etAbout = findViewById<EditText>(R.id.etAboutYourself)
        val btnBrowse = findViewById<Button>(R.id.btnBrowse)
        val btnFinish = findViewById<Button>(R.id.btnCompleteRegistration)

        // 4. Back Button Navigation
        ivBack.setOnClickListener {
            finish()
        }

        // 5. Open Image Picker on Browse click
        btnBrowse.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 6. Finish/Complete Logic
        btnFinish.setOnClickListener {
            val aboutText = etAbout.text.toString().trim()
            if (aboutText.isEmpty()) {
                etAbout.error = "Please write something about yourself"
            } else {
                saveFinalData(aboutText)
            }
        }

        fetchExistingData()
    }

    private fun fetchExistingData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile(sessionManager.getBearerToken())
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    findViewById<EditText>(R.id.etAboutYourself).setText(user?.aboutSection?.aboutMe ?: "")
                }
            } catch (e: Exception) {}
        }
    }

    private fun saveFinalData(aboutText: String) {
        val btnFinish = findViewById<Button>(R.id.btnCompleteRegistration)
        btnFinish.isEnabled = false
        btnFinish.text = "Completing..."

        lifecycleScope.launch {
            try {
                val data = mapOf(
                    "aboutSection" to mapOf(
                        "aboutMe" to aboutText
                    ),
                    "profileStatus" to "COMPLETED",
                    "profileCompletedSteps" to 5
                )
                
                val response = RetrofitClient.apiService.completeRegistration(sessionManager.getBearerToken(), data)
                if (response.isSuccessful) {
                    val intent = Intent(this@AboutYourselfActivity, RegisterSuccessActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@AboutYourselfActivity, "Failed to complete registration", Toast.LENGTH_SHORT).show()
                    btnFinish.isEnabled = true
                    btnFinish.text = "Complete Registration"
                }
            } catch (e: Exception) {
                Toast.makeText(this@AboutYourselfActivity, "Network Error", Toast.LENGTH_SHORT).show()
                btnFinish.isEnabled = true
                btnFinish.text = "Complete Registration"
            }
        }
    }
}
