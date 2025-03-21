package com.hirumitha.budget.buddy.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.budgetbuddy.auth.LoginActivity
import com.example.budgetbuddy.auth.SessionManager
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.databinding.ActivityMainBinding
import com.hirumitha.budget.buddy.fragments.home.HomeFragment

@Suppress("UNUSED_VARIABLE")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize session manager
        sessionManager = SessionManager(this)

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startLoginActivity()
            return
        }

        val navView: BottomNavigationView = binding.navView
        
        // Fix: Properly get NavController from NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, 
                R.id.navigation_transactions, 
                R.id.navigation_add,
                R.id.navigation_budget, 
                R.id.navigation_more
            )
        )
        navView.setupWithNavController(navController)
        
        // The FAB in the middle should be handled specially
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_transactions -> {
                    navController.navigate(R.id.navigation_transactions)
                    true
                }
                R.id.navigation_add -> {
                    navController.navigate(R.id.navigation_add)
                    true
                }
                R.id.navigation_budget -> {
                    navController.navigate(R.id.navigation_budget)
                    true
                }
                R.id.navigation_more -> {
                    navController.navigate(R.id.navigation_more)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        sessionManager.logoutUser()
        startLoginActivity()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun updateHomeFragment() {
        try {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            val childFragments = currentFragment?.childFragmentManager?.fragments
            
            if (childFragments != null && childFragments.isNotEmpty()) {
                val firstFragment = childFragments.firstOrNull()
                if (firstFragment is HomeFragment) {
                    try {
                        // Safely call refreshData if it exists
                        if (firstFragment::class.java.methods.any { it.name == "refreshData" }) {
                            firstFragment.refreshData()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
