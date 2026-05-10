package com.example.marriage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDay: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var tvEmployeeId: TextView
    private lateinit var tvOpenTasks: TextView
    private lateinit var tvProjects: TextView
    private lateinit var tvOpenTickets: TextView

    private val timeHandler = Handler(Looper.getMainLooper())
    private var isClockedIn = false

    private val clockRunnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            updateDateTime()
            timeHandler.postDelayed(this, 1000)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // --- Bind Views ---
        tvTime         = findViewById(R.id.tvTime)
        tvDay          = findViewById(R.id.tvDay)
        tvWelcome      = findViewById(R.id.tvWelcome)
        tvUserName     = findViewById(R.id.tvUserName)
        tvUserRole     = findViewById(R.id.tvUserRole)
        tvEmployeeId   = findViewById(R.id.tvEmployeeId)
        tvOpenTasks    = findViewById(R.id.tvOpenTasks)
        tvProjects     = findViewById(R.id.tvProjects)
        tvOpenTickets  = findViewById(R.id.tvOpenTickets)

        val btnMenu         = findViewById<ImageButton>(R.id.btnMenu)
        val btnDarkMode     = findViewById<ImageButton>(R.id.btnDarkMode)
        val btnAdd          = findViewById<ImageButton>(R.id.btnAdd)
        val btnNotification = findViewById<ImageButton>(R.id.btnNotification)
        val btnLogout       = findViewById<ImageButton>(R.id.btnLogout)
        val btnClockIn      = findViewById<Button>(R.id.btnClockIn)
        val btnSettings     = findViewById<ImageButton>(R.id.btnSettings)
        val cardMyProfile   = findViewById<LinearLayout>(R.id.cardMyProfile)
        val cardMyTasks     = findViewById<LinearLayout>(R.id.cardMyTasks)
        val cardProjects    = findViewById<LinearLayout>(R.id.cardProjects)
        val cardTickets     = findViewById<LinearLayout>(R.id.cardTickets)

        // --- Populate user data ---
        // In a real app, retrieve this from SharedPreferences / ViewModel
        val userName = "D'angelo Nitzsche"
        tvWelcome.text    = "Welcome, ${userName.split(" ").first()}!"
        tvUserName.text   = userName
        tvUserRole.text   = "Junior"
        tvEmployeeId.text = "Employee Id : EMP-1"
        tvOpenTasks.text  = "4"
        tvProjects.text   = "6"
        tvOpenTickets.text = "0"

        // --- Live clock ---
        timeHandler.post(clockRunnable)

        // --- Clock In / Clock Out toggle ---
        btnClockIn.setOnClickListener {
            isClockedIn = !isClockedIn
            if (isClockedIn) {
                btnClockIn.text = "Clock Out"
                btnClockIn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#E53935")
                    )
                Toast.makeText(this, "Clocked In ✅", Toast.LENGTH_SHORT).show()
            } else {
                btnClockIn.text = "Clock In"
                btnClockIn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#1976D2")
                    )
                Toast.makeText(this, "Clocked Out 🔴", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Top bar buttons ---
        btnMenu.setOnClickListener {
            Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show()
        }

        btnDarkMode.setOnClickListener {
            Toast.makeText(this, "Dark Mode toggled", Toast.LENGTH_SHORT).show()
        }

        btnAdd.setOnClickListener {
            Toast.makeText(this, "Quick Add", Toast.LENGTH_SHORT).show()
        }

        btnNotification.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        btnSettings.setOnClickListener {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
        }

        // --- Quick Action cards ---
        cardMyProfile.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        cardMyTasks.setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
        }

        cardProjects.setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        cardTickets.setOnClickListener {
            Toast.makeText(this, "Support coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    /** Updates the live clock and day label every second. */
    @SuppressLint("SetTextI18n")
    private fun updateDateTime() {
        val now = Date()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dayFormat  = SimpleDateFormat("EEEE", Locale.getDefault())
        tvTime.text = timeFormat.format(now)
        tvDay.text  = dayFormat.format(now)
    }

    /** Confirms logout and navigates back to LoginActivity. */
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Logout") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeHandler.removeCallbacks(clockRunnable)
    }
}
