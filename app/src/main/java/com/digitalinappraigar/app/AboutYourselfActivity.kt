package com.digitalinappraigar.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigar.app.network.models.*
import com.digitalinappraigarvivah.app.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AboutYourselfActivity : AppCompatActivity() {
    
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var ivProfilePreview: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_yourself)

        ivProfilePreview = findViewById(R.id.ivProfilePreview)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                ivProfilePreview.setImageURI(uri)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val etAbout = findViewById<EditText>(R.id.etAboutYourself)
        val btnBrowse = findViewById<Button>(R.id.btnBrowse)
        val btnFinish = findViewById<Button>(R.id.btnCompleteRegistration)

        ivBack.setOnClickListener { finish() }

        btnBrowse.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getBearerToken()

        btnFinish.setOnClickListener {
            val bio = etAbout.text.toString().trim()
            
            if (bio.isEmpty()) {
                Toast.makeText(this, "Please write something about yourself", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bio.length < 20) {
                etAbout.error = "Bio must be at least 20 characters"
                return@setOnClickListener
            }
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnFinish.isEnabled = false
            btnFinish.text = "Completing..."

            lifecycleScope.launch {
                try {
                    val bioPart = bio.toRequestBody("text/plain".toMediaTypeOrNull())
                    
                    val filePart = getMultipartBodyFromUri(selectedImageUri!!, "images")
                    val imagesList = if (filePart != null) listOf(filePart) else emptyList()

                    // backendraigar identifies the user via the auth token, so userId is removed from the call
                    val response = RetrofitClient.apiService.saveAboutYourself(token, bioPart, imagesList)
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@AboutYourselfActivity, "Registration Completed!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AboutYourselfActivity, RegisterSuccessActivity::class.java))
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Failed to save"
                        Log.e("AboutYourself", "Error Code: ${response.code()} - $errorMsg")
                        Toast.makeText(this@AboutYourselfActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("AboutYourself", "Network error", e)
                    Toast.makeText(this@AboutYourselfActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    btnFinish.isEnabled = true
                    btnFinish.text = "Complete Registration"
                }
            }
        }
    }

    private fun getMultipartBodyFromUri(uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, file.name, requestFile)
        } catch (e: Exception) {
            null
        }
    }
}
