package com.example.budgetbuddy.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.hirumitha.budget.buddy.R

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var userDBHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginTextView = findViewById(R.id.loginTextView)

        // Initialize database helper
        userDBHelper = UserDBHelper(this)

        // Set click listeners
        registerButton.setOnClickListener {
            registerUser()
        }

        loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.error_passwords_dont_match), Toast.LENGTH_SHORT).show()
            return
        }

        // Check if user already exists
        if (userDBHelper.checkUserExists(email)) {
            Toast.makeText(this, getString(R.string.error_user_exists), Toast.LENGTH_SHORT).show()
            return
        }

        // Register user
        val id = userDBHelper.registerUser(username, email, password)
        if (id > 0) {
            Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show()
            
            // Navigate to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
        }
    }
} 