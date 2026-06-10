package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marriage.utils.SessionManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager
    private var verificationId: String? = null
    
    private lateinit var btnLogin: Button
    private lateinit var etPhone: EditText
    private lateinit var tvOtpLabel: TextView
    private lateinit var llOtpContainer: LinearLayout
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText

    private val TAG = "Firebase_Auth_Debug"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToHome()
            return
        }

        // Initialize Views
        etPhone = findViewById(R.id.etPhone)
        btnLogin = findViewById(R.id.btnLogin)
        tvOtpLabel = findViewById(R.id.tvOtpLabel)
        llOtpContainer = findViewById(R.id.llOtpContainer)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        otp6 = findViewById(R.id.otp6)

        // OTP navigation logic
        setupOtpLogic(otp1, otp2, null)
        setupOtpLogic(otp2, otp3, otp1)
        setupOtpLogic(otp3, otp4, otp2)
        setupOtpLogic(otp4, otp5, otp3)
        setupOtpLogic(otp5, otp6, otp4)
        setupOtpLogic(otp6, null, otp5)

        // Initial phone prefix
        if (etPhone.text.isNullOrEmpty()) {
            etPhone.setText("+91 ")
        }
        etPhone.setSelection(etPhone.text.length)

        tvSignUp?.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString().replace(" ", "").trim()
            val otp = "${otp1.text}${otp2.text}${otp3.text}${otp4.text}${otp5.text}${otp6.text}".trim()

            if (phone.length < 12) {
                Toast.makeText(this, "Enter valid 10-digit number after +91", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (verificationId == null) {
                sendVerificationCode(phone)
            } else {
                if (otp.length < 6) {
                    Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                } else {
                    verifyCode(otp)
                }
            }
        }
    }

    private fun sendVerificationCode(phone: String) {
        btnLogin.isEnabled = false
        btnLogin.text = "Sending OTP..."

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "Firebase Error: ${e.message}")
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    resetButton()
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                    
                    // SHOW OTP BOXES NOW
                    tvOtpLabel.visibility = View.VISIBLE
                    llOtpContainer.visibility = View.VISIBLE
                    etPhone.isEnabled = false // Optional: Disable phone change during verification
                    
                    Toast.makeText(this@LoginActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = "Verify OTP"
                    otp1.requestFocus() // Auto-focus on first OTP box
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        btnLogin.isEnabled = false
        btnLogin.text = "Verifying..."
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val idToken = tokenTask.result.token
                            sessionManager.saveTokens(idToken ?: "token", "refresh")
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                    btnLogin.text = "Verify OTP"
                }
            }
    }

    private fun resetButton() {
        btnLogin.isEnabled = true
        btnLogin.text = "Send OTP"
        etPhone.isEnabled = true
        tvOtpLabel.visibility = View.GONE
        llOtpContainer.visibility = View.GONE
        verificationId = null
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
