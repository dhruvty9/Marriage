package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.network.models.RegisterRequest
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sessionManager = SessionManager(this)

        val btnSignup = findViewById<Button>(R.id.btnRegister)
        val etPhone   = findViewById<EditText>(R.id.etPhone)
        val etName    = findViewById<EditText>(R.id.etFullName)
        val etPass    = findViewById<EditText>(R.id.etPassword)
        val dropdown  = findViewById<AutoCompleteTextView>(R.id.tvProfileDropdown)
        val tvLogin   = findViewById<TextView>(R.id.tvLoginhere)

        // Dropdown setup
        val profileOptions = arrayOf("Myself", "Son", "Daughter", "Brother", "Sister", "Friend")
        dropdown?.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, profileOptions))
        dropdown?.setOnClickListener { dropdown.showDropDown() }

        etPhone?.setText("+91 ")
        etPhone?.let { Selection.setSelection(it.text, it.text.length) }

        tvLogin?.setOnClickListener { finish() }

        btnSignup?.setOnClickListener {
            val name = etName?.text?.toString()?.trim() ?: ""
            val phone = etPhone?.text?.toString()?.replace("+91 ", "")?.trim() ?: ""
            val pass = etPass?.text?.toString()?.trim() ?: ""
            val profile = dropdown?.text?.toString() ?: "Myself"

            if (name.isEmpty() || phone.length != 10 || pass.length < 8) {
                Toast.makeText(this, "Please fill all details correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSignup.isEnabled = false
            btnSignup.text = "Registering..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.register(RegisterRequest(profile, name, phone, pass))
                    val body = response.body()
                    if (response.isSuccessful && body?.success == true) {
                        if (body.accessToken != null && body.refreshToken != null) {
                            sessionManager.saveTokens(body.accessToken, body.refreshToken)
                            body.user?.let { sessionManager.saveUser(it) }
                            startActivity(Intent(this@SignupActivity, BasicActivity::class.java))
                            finish()
                        }
                    } else {
                        val errorMessage = body?.message ?: parseErrorMessage(response.errorBody()?.string())
                        Toast.makeText(this@SignupActivity, errorMessage ?: "Signup Failed", Toast.LENGTH_SHORT).show()
                        btnSignup.isEnabled = true
                        btnSignup.text = "Register Now"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignupActivity, "Network Error", Toast.LENGTH_SHORT).show()
                    btnSignup.isEnabled = true
                    btnSignup.text = "Register Now"
                }
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
