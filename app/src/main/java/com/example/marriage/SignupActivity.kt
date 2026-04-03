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
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 1. Initialize Views
        val tvLogin = findViewById<TextView>(R.id.tvLoginhere)
        val btnSignup = findViewById<Button>(R.id.btnRegister)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val dropdown = findViewById<AutoCompleteTextView>(R.id.tvProfileDropdown)

        // 2. DROP-DOWN LOGIC
        val profileOptions = arrayOf("Myself", "Son", "Daughter", "Brother", "Sister", "Friend")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, profileOptions)
        dropdown.setAdapter(adapter)

        dropdown.setOnClickListener {
            dropdown.showDropDown()
        }

        // 3. PHONE PREFIX LOGIC (+91)
        if (etPhone.text.isEmpty()) {
            etPhone.setText("+91 ")
            Selection.setSelection(etPhone.text, etPhone.text.length)
        }

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

        // 4. NAVIGATION: LOGIN HERE
        tvLogin.setOnClickListener {
            android.widget.Toast.makeText(this, "Going to Login Page", android.widget.Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 5. NAVIGATION: REGISTER BUTTON (Goes to BasicActivity)
        btnSignup.setOnClickListener {
            android.widget.Toast.makeText(this, "Opening Basic Details...", android.widget.Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BasicActivity::class.java)
            startActivity(intent)
        }
    }
}