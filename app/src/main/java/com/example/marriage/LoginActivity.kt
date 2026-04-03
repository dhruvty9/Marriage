package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent // Required for Backspace detection
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Initialize ALL OTP EditTexts
        val otp1 = findViewById<EditText>(R.id.otp1)
        val otp2 = findViewById<EditText>(R.id.otp2)
        val otp3 = findViewById<EditText>(R.id.otp3)
        val otp4 = findViewById<EditText>(R.id.otp4)
        val otp5 = findViewById<EditText>(R.id.otp5)
        val otp6 = findViewById<EditText>(R.id.otp6)

        // 2. Setup Auto-Shift Logic (Forward AND Backward)
        setupOtpLogic(otp1, otp2, null)   // First box: no previous
        setupOtpLogic(otp2, otp3, otp1)
        setupOtpLogic(otp3, otp4, otp2)
        setupOtpLogic(otp4, otp5, otp3)
        setupOtpLogic(otp5, otp6, otp4)
        setupOtpLogic(otp6, null, otp5)   // Last box: no next

        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        // Underline "Signup here"
        tvSignUp.paintFlags = tvSignUp.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // 3. Phone Number Prefix Logic (+91)
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
            android.widget.Toast.makeText(this, "Opening Registration...", android.widget.Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Handles forward shift when typing and backward shift when deleting.
     */
    private fun setupOtpLogic(currentBox: EditText, nextBox: EditText?, previousBox: EditText?) {

        // FORWARD SHIFT: Move to next box when a number is entered
        currentBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    nextBox?.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // BACKWARD SHIFT: Move to previous box when Backspace is pressed on an empty box
        currentBox.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                // If current box is empty, move focus to the previous box
                if (currentBox.text.isEmpty() && previousBox != null) {
                    previousBox.requestFocus()
                    // Optional: If you want to delete the character in the previous box immediately:
                    // previousBox.setText("")
                    return@setOnKeyListener true
                }
            }
            false
        }
    }
}