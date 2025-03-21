package com.hirumitha.budget.buddy.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.auth.LoginActivity
import com.example.budgetbuddy.auth.SessionManager
import com.google.android.material.button.MaterialButton
import com.hirumitha.budget.buddy.R

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var joinDateTextView: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var editProfileButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize session manager
        sessionManager = SessionManager(requireContext())
        
        // Initialize views
        usernameTextView = view.findViewById(R.id.usernameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        joinDateTextView = view.findViewById(R.id.joinDateTextView)
        logoutButton = view.findViewById(R.id.logoutButton)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        
        // Set up button click listeners
        setupButtonListeners()
        
        // Load user data
        loadUserData()
    }
    
    private fun setupButtonListeners() {
        logoutButton.setOnClickListener {
            logout()
        }
        
        editProfileButton.setOnClickListener {
            // In a real app, this would navigate to an edit profile screen
            // For now, we'll just show a message
            // Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadUserData() {
        // In a real app, this would load data from the session or database
        // For now, we'll use mock data or data from session manager
        
        val username = sessionManager.getUsername() ?: "John Doe"
        val email = sessionManager.getEmail() ?: "john.doe@example.com"
        val joinDate = "January 1, 2023" // This would come from the database in a real app
        
        usernameTextView.text = username
        emailTextView.text = email
        joinDateTextView.text = "Member since: $joinDate"
    }
    
    private fun logout() {
        sessionManager.logoutUser()
        startLoginActivity()
    }
    
    private fun startLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    
    // Extension functions for SessionManager
    private fun SessionManager.getUsername(): String? {
        return this.getUserDetails()[SessionManager.KEY_USERNAME]
    }
    
    private fun SessionManager.getEmail(): String? {
        return this.getUserDetails()[SessionManager.KEY_EMAIL]
    }
} 