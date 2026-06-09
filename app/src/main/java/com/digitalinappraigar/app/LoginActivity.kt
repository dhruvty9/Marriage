package com.digitalinappraigar.app

import android.annotation.SuppressLint
import android.util.Log
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.digitalinappraigar.app.network.RetrofitClient
import com.digitalinappraigar.app.network.models.LoginRequest
import com.digitalinappraigar.app.utils.SessionManager
import com.digitalinappraigarvivah.app.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var isOtpSent = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()

        val etPhone  = findViewById<EditText>(R.id.etPhone)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvPasswordLabel = findViewById<TextView>(R.id.tvPasswordLabel)
        val llOtpContainer = findViewById<View>(R.id.llOtpContainer)
        val tvOtpLabel = findViewById<TextView>(R.id.tvOtpLabel)
        val rgLoginMethod = findViewById<RadioGroup>(R.id.rgLoginMethod)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        val otpFields = listOf<EditText>(
            findViewById(R.id.otp1), findViewById(R.id.otp2),
            findViewById(R.id.otp3), findViewById(R.id.otp4),
            findViewById(R.id.otp5), findViewById(R.id.otp6)
        )

        // Setup OTP auto-focus logic
        for (i in otpFields.indices) {
            val next = if (i < otpFields.size - 1) otpFields[i + 1] else null
            val prev = if (i > 0) otpFields[i - 1] else null
            setupOtpFocus(otpFields[i], next, prev)
        }

        etPhone.setText("+91 ")
        etPhone.setSelection(etPhone.text.length)

        rgLoginMethod.setOnCheckedChangeListener { _, checkedId ->
            isOtpSent = false
            if (checkedId == R.id.rbOtp) {
                etPassword.visibility = View.GONE
                tvPasswordLabel.visibility = View.GONE
                llOtpContainer.visibility = View.GONE
                tvOtpLabel.visibility = View.GONE
                btnLogin.text = "Send OTP"
            } else {
                etPassword.visibility = View.VISIBLE
                tvPasswordLabel.visibility = View.VISIBLE
                llOtpContainer.visibility = View.GONE
                tvOtpLabel.visibility = View.GONE
                btnLogin.text = "Login"
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            if (phone.length < 13) {
                Toast.makeText(this, "Enter valid phone number with +91", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (rgLoginMethod.checkedRadioButtonId == R.id.rbOtp) {
                if (!isOtpSent) {
                    sendFirebaseOtp(phone, btnLogin, llOtpContainer, tvOtpLabel)
                } else {
                    val code = otpFields.joinToString("") { it.text.toString() }
                    if (code.length < 6) {
                        Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    verifyFirebaseOtp(code, phone.replace("+91 ", ""))
                }
            } else {
                val pass = etPassword.text.toString().trim()
                if (pass.isEmpty()) {
                    Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                performBackendLogin(phone.replace("+91 ", ""), pass)
            }
        }
    }

    private fun sendFirebaseOtp(phone: String, btn: Button, container: View, label: View) {
        btn.isEnabled = false
        btn.text = "Sending..."

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (rare on some devices)
                    signInWithFirebase(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "OTP verification failed", e)
                    Log.e("AuthError", "Reason: ${e.message}", e)

                    btn.isEnabled = true
                    btn.text = "Send OTP"
                    Toast.makeText(this@LoginActivity, "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(TAG, "Code sent, verificationId=$verId")
                    verificationId = verId
                    isOtpSent = true
                    btn.isEnabled = true
                    btn.text = "Verify & Login"
                    container.visibility = View.VISIBLE
                    label.visibility = View.VISIBLE
                    Toast.makeText(this@LoginActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()
        Log.d(TAG, "Starting OTP verification for $phone")
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyFirebaseOtp(code: String, phone: String) {
        Log.d(TAG, "Verifying OTP code=$code for $phone")
        val verId = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(verId, code)
        signInWithFirebase(credential)
    }

    private fun signInWithFirebase(credential: PhoneAuthCredential) {
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.isEnabled = false
        btnLogin.text = "Verifying..."

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Firebase sign-in succeeded, phone=${auth.currentUser?.phoneNumber}")
                val phone = auth.currentUser?.phoneNumber?.replace("+91", "")?.trim() ?: ""
                // After Firebase verification, we authenticate with our own backend
                performBackendLogin(phone, "OTP_VERIFIED")
            } else {
                Log.e(TAG, "Firebase sign-in failed", task.exception)
                btnLogin.isEnabled = true
                btnLogin.text = "Verify & Login"
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performBackendLogin(phone: String, pass: String) {
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.isEnabled = false
        Log.d(TAG, "Backend login request: phone=$phone, pass=$pass")
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(phone, pass))
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d(TAG, "Backend login successful: ${response.body()?.message}")
                    val body = response.body()!!
                    sessionManager.saveTokens(body.token ?: "", body.refreshToken ?: "")
                    body.data?.let { sessionManager.saveUser(it) }

                    if (body.data?.profileStatus == "completed") {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    } else {
                        Toast.makeText(this@LoginActivity, "Complete your registration", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, BasicActivity::class.java))
                    }
                    finish()
                } else {
                    val msg = response.body()?.message ?: "Login failed. Ensure you are registered."
                    Log.d(TAG, "Backend login failed: $msg")
                    Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend login exception", e)
                Toast.makeText(this@LoginActivity, "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
                btnLogin.isEnabled = true
            }
        }
    }

    private fun setupOtpFocus(current: EditText, next: EditText?, prev: EditText?) {
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
