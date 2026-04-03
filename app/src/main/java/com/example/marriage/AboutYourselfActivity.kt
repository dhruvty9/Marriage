package com.example.marriage

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

class AboutYourselfActivity : AppCompatActivity() {
    
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var ivProfilePreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_yourself)

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
            if (etAbout.text.toString().isEmpty()) {
                etAbout.error = "Please write something about yourself"
            } else {
                Toast.makeText(this, "Profile Completed Successfully!", Toast.LENGTH_LONG).show()
                // Navigate to Dashboard...
            }
        }
    }
}