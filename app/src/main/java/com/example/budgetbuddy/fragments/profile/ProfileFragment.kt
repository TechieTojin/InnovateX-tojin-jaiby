package com.example.budgetbuddy.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hirumitha.budget.buddy.R
import com.example.budgetbuddy.auth.SessionManager
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var joinDateTextView: TextView
    private lateinit var transactionsValue: TextView
    private lateinit var savingsRateValue: TextView
    private lateinit var topCategoryValue: TextView
    private lateinit var editProfileButton: MaterialButton
    private lateinit var logoutButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        // Initialize SessionManager
        sessionManager = SessionManager(requireContext())
        
        // Initialize views
        usernameTextView = view.findViewById(R.id.usernameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        joinDateTextView = view.findViewById(R.id.joinDateTextView)
        transactionsValue = view.findViewById(R.id.transactionsValue)
        savingsRateValue = view.findViewById(R.id.savingsRateValue)
        topCategoryValue = view.findViewById(R.id.topCategoryValue)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        logoutButton = view.findViewById(R.id.logoutButton)
        
        // Load user data
        loadUserData()
        
        // Load financial summary
        loadFinancialSummary()
        
        // Set up button click listeners
        setupButtonListeners()
        
        return view
    }
    
    private fun loadUserData() {
        val userDetails = sessionManager.getUserDetails()
        
        // Set user data to views
        usernameTextView.text = userDetails[SessionManager.KEY_USERNAME] ?: "User"
        emailTextView.text = userDetails[SessionManager.KEY_EMAIL] ?: "user@example.com"
        
        // For demo purposes, we'll use a static date
        // In a real app, you would store and retrieve the registration date
        joinDateTextView.text = "Member since: January 2023"
    }
    
    private fun loadFinancialSummary() {
        // In a real app, you would fetch this data from your database
        // For demo purposes, we'll use static values
        val totalTransactions = 124
        val savingsRate = 23.5
        val topCategory = "Housing"
        
        // Update UI with values
        transactionsValue.text = totalTransactions.toString()
        savingsRateValue.text = String.format("%.1f%%", savingsRate)
        topCategoryValue.text = topCategory
    }
    
    private fun setupButtonListeners() {
        editProfileButton.setOnClickListener {
            // In a real app, you would navigate to an edit profile screen
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }
        
        logoutButton.setOnClickListener {
            // Log out the user
            sessionManager.logoutUser()
            
            // In a real app, you would navigate to the login screen
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            
            // For demo purposes, we'll just show a toast
            // In a real app, you would use something like:
            // val intent = Intent(requireContext(), LoginActivity::class.java)
            // startActivity(intent)
            // requireActivity().finish()
        }
    }
} 