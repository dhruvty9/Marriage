package com.digitalinappraigar.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.network.models.RegisterRequest
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import kotlinx.coroutines.launch
import com.google.gson.Gson

class SignupActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sessionManager = SessionManager(this)

        val tvLogin   = findViewById<TextView>(R.id.tvLoginhere)
        val btnSignup = findViewById<Button>(R.id.btnRegister)
        val etPhone   = findViewById<EditText>(R.id.etPhone)
        val etName    = findViewById<EditText>(R.id.etFullName)
        val etPass    = findViewById<EditText>(R.id.etPassword)
        val dropdown  = findViewById<AutoCompleteTextView>(R.id.tvProfileDropdown)

        val profileOptions = arrayOf("Myself", "Son", "Daughter", "Brother", "Sister", "Friend")
        dropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, profileOptions))
        dropdown.setOnClickListener { dropdown.showDropDown() }

        etPhone.setText("+91 ")
        Selection.setSelection(etPhone.text, etPhone.text.length)
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().startsWith("+91 ")) {
                    etPhone.setText("+91 ")
                    etPhone.setSelection(etPhone.text.length)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignup.setOnClickListener {
            val name    = etName?.text?.toString()?.trim() ?: ""
            val phone   = etPhone.text.toString().replace("+91 ", "").trim()
            val pass    = etPass?.text?.toString()?.trim() ?: ""
            val profile = dropdown.text.toString().trim().ifEmpty { "Myself" }

            if (name.isEmpty()) { Toast.makeText(this, "Enter full name", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (phone.length < 10) { Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (pass.length < 6) { Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            btnSignup.isEnabled = false
            btnSignup.text = "Registering..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.register(
                        RegisterRequest(
                            profile      = profile,
                            fullName     = name,
                            mobileNumber = phone,
                            password     = pass
                        )
                    )
                    if (response.isSuccessful && response.body()?.success == true) {
                        val body = response.body()!!
                        sessionManager.saveTokens(body.token ?: "", body.refreshToken ?: "")
                        body.data?.let { sessionManager.saveUser(it) }

                        Toast.makeText(this@SignupActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignupActivity, BasicActivity::class.java))
                        finish()
                    } else {
                        val errorJson = response.errorBody()?.string()
                        val errorMsg = try {
                            val map = Gson().fromJson(errorJson, Map::class.java)
                            map["message"]?.toString() ?: "Registration failed"
                        } catch (e: Exception) {
                            "Registration failed"
                        }
                        Toast.makeText(this@SignupActivity, errorMsg, Toast.LENGTH_LONG).show()
                        btnSignup.isEnabled = true
                        btnSignup.text = "Register"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignupActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnSignup.isEnabled = true
                    btnSignup.text = "Register"
                }
            }
        }
    }
}
