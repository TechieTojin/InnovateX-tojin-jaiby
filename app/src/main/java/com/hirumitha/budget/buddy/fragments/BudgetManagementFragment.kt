package com.hirumitha.budget.buddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.database.BudgetDBHelper
import com.hirumitha.budget.buddy.databinding.FragmentBudgetManagementBinding
import com.hirumitha.budget.buddy.models.BudgetModel

class BudgetManagementFragment : Fragment() {

    private var _binding: FragmentBudgetManagementBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: BudgetDBHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize database helper
        dbHelper = BudgetDBHelper(requireContext())
        
        // Set up period spinner
        val periods = arrayOf(
            getString(R.string.daily),
            getString(R.string.weekly),
            getString(R.string.monthly),
            getString(R.string.yearly)
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            periods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = adapter
        
        // Set up submit button
        binding.btnSetBudget.setOnClickListener {
            saveBudget()
        }
        
        // Load existing budgets
        loadBudgets()
    }
    
    private fun saveBudget() {
        val name = binding.etBudgetName.text.toString().trim()
        val amountStr = binding.etBudgetAmount.text.toString().trim()
        val period = binding.spinnerPeriod.selectedItem.toString()
        
        if (name.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        
        val budget = BudgetModel(0, name, amount, period)
        val success = dbHelper.insertBudget(budget)
        
        if (success > 0) {
            Toast.makeText(requireContext(), "Budget saved successfully", Toast.LENGTH_SHORT).show()
            clearFields()
            loadBudgets()
        } else {
            Toast.makeText(requireContext(), "Failed to save budget", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadBudgets() {
        val budgets = dbHelper.getAllBudgets()
        
        // Update UI with budgets
        if (budgets.isEmpty()) {
            binding.tvNoBudgets.visibility = View.VISIBLE
            binding.recyclerBudgets.visibility = View.GONE
        } else {
            binding.tvNoBudgets.visibility = View.GONE
            binding.recyclerBudgets.visibility = View.VISIBLE
            
            // Set up recycler view adapter (implementation depends on your adapter)
            // val adapter = BudgetAdapter(budgets)
            // binding.recyclerBudgets.adapter = adapter
        }
    }
    
    private fun clearFields() {
        binding.etBudgetName.text?.clear()
        binding.etBudgetAmount.text?.clear()
        binding.spinnerPeriod.setSelection(0)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 