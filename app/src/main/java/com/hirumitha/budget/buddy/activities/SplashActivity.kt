package com.hirumitha.budget.buddy.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.example.budgetbuddy.auth.LoginActivity
import com.example.budgetbuddy.auth.SessionManager
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize session manager
        sessionManager = SessionManager(this)

        // Set initial states
        setupInitialStates()
        
        // Start animations
        startAnimations()
    }

    private fun setupInitialStates() {
        val views = listOf(
            binding.logoContainer,
            binding.contentContainer,
            binding.bottomContainer
        )
        
        views.forEach { view ->
            view.alpha = 0f
            view.scaleX = 0.8f
            view.scaleY = 0.8f
        }

        binding.backgroundShape.scaleX = 0f
        binding.backgroundShape.scaleY = 0f
        binding.backgroundShape.alpha = 0f
    }

    private fun startAnimations() {
        // Animate background shape
        ViewCompat.animate(binding.backgroundShape)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(0.1f)
            .setDuration(1000)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Animate logo with bounce effect
        ViewCompat.animate(binding.logoContainer)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()

        // Animate content with sequence
        Handler(Looper.getMainLooper()).postDelayed({
            ViewCompat.animate(binding.contentContainer)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }, 300)

        // Animate bottom container
        Handler(Looper.getMainLooper()).postDelayed({
            ViewCompat.animate(binding.bottomContainer)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    // Start navigation animation after all animations complete
                    startNavigationAnimation()
                }
                .start()
        }, 500)
    }

    private fun startNavigationAnimation() {
        Handler(Looper.getMainLooper()).postDelayed({
            // Create fade out animation for all views
            val views = listOf(
                binding.logoContainer,
                binding.contentContainer,
                binding.bottomContainer,
                binding.backgroundShape
            )

            views.forEach { view ->
                ViewCompat.animate(view)
                    .alpha(0f)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }

            // Navigate after fade out
            Handler(Looper.getMainLooper()).postDelayed({
                if (sessionManager.isLoggedIn()) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }, 300)
        }, 1200) // Total animation duration before navigation
    }
}