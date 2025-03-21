package com.hirumitha.budget.buddy.fragments.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.databinding.FragmentMoreOptionsBinding

class MoreOptionsFragment : Fragment() {

    private var _binding: FragmentMoreOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Profile & Settings
        binding.cardProfile.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_profile)
        }

        // Insights
        binding.cardInsights.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_insights)
        }

        // Notifications
        binding.cardNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_more_to_notifications)
        }

        // Monthly Summary
        binding.cardMonthlySummary.setOnClickListener {
            findNavController().navigate(R.id.navigation_monthly_expense)
        }

        // Summary
        binding.cardSummary.setOnClickListener {
            findNavController().navigate(R.id.navigation_summary)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 