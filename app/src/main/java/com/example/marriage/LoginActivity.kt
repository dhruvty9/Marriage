package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.marriage.network.RetrofitClient
import com.example.marriage.network.models.LoginRequest
import com.example.marriage.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // If already logged in → go to Home
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        val otp1 = findViewById<EditText>(R.id.otp1)
        val otp2 = findViewById<EditText>(R.id.otp2)
        val otp3 = findViewById<EditText>(R.id.otp3)
        val otp4 = findViewById<EditText>(R.id.otp4)
        val otp5 = findViewById<EditText>(R.id.otp5)
        val otp6 = findViewById<EditText>(R.id.otp6)

        setupOtpLogic(otp1, otp2, null)
        setupOtpLogic(otp2, otp3, otp1)
        setupOtpLogic(otp3, otp4, otp2)
        setupOtpLogic(otp4, otp5, otp3)
        setupOtpLogic(otp5, otp6, otp4)
        setupOtpLogic(otp6, null, otp5)

        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        tvSignUp.paintFlags = tvSignUp.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

        val etPhone  = findViewById<EditText>(R.id.etPhone)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        etPhone.setText("+91 ")
        etPhone.setSelection(etPhone.text.length)

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

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString().replace("+91 ", "").trim()
            val otp   = "${otp1.text}${otp2.text}${otp3.text}${otp4.text}${otp5.text}${otp6.text}"

            if (phone.length < 10) {
                Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (otp.length < 6) {
                Toast.makeText(this, "Enter 6-digit OTP / Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.login(
                        LoginRequest(mobileNumber = phone, password = otp)
                    )
                    if (response.isSuccessful && response.body()?.success == true) {
                        val body = response.body()!!
                        sessionManager.saveTokens(body.accessToken!!, body.refreshToken!!)
                        body.user?.let { sessionManager.saveUser(it) }

                        Toast.makeText(this@LoginActivity, "Welcome back!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Login failed"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                }
            }
        }
    }

    private fun setupOtpLogic(current: EditText, next: EditText?, prev: EditText?) {
        current.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { if (s?.length == 1) next?.requestFocus() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        current.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (current.text.isEmpty() && prev != null) {
                    prev.requestFocus()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }
}
