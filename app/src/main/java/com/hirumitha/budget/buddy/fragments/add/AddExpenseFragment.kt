package com.hirumitha.budget.buddy.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.activities.MainActivity
import com.hirumitha.budget.buddy.database.TransactionDBHelper
import com.hirumitha.budget.buddy.databinding.FragmentAddExpenseBinding
import com.hirumitha.budget.buddy.models.TransactionModel
import com.hirumitha.budget.buddy.utils.AIExpenseClassifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: TransactionDBHelper
    private lateinit var aiClassifier: AIExpenseClassifier

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize database helper
        dbHelper = TransactionDBHelper(requireContext())
        
        // Initialize AI classifier
        aiClassifier = AIExpenseClassifier(requireContext())
        
        // Set up category spinner
        setupCategorySpinner()
        
        // Set up save button
        binding.btnSave.setOnClickListener {
            saveExpense()
        }
        
        // Set up AI categorization
        binding.btnAiSuggestion.setOnClickListener {
            suggestCategory()
        }
    }
    
    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }
    
    private fun suggestCategory() {
        val description = binding.etDescription.text.toString().trim()
        if (description.isNotEmpty()) {
            val suggestedCategory = aiClassifier.classifyExpense(description, 0.0)
            val categories = resources.getStringArray(R.array.expense_categories)
            val position = categories.indexOf(suggestedCategory)
            if (position >= 0) {
                binding.spinnerCategory.setSelection(position)
                Toast.makeText(requireContext(), "Category suggested: $suggestedCategory", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please enter a description first", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveExpense() {
        val amount = binding.etAmount.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        
        if (amount.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Create transaction
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        val transaction = TransactionModel(
            0,
            "expense",
            category,
            amountValue.toFloat(),
            currentDate
        )
        
        // Save to database
        val success = dbHelper.insertTransaction(transaction) > 0
        
        if (success) {
            Toast.makeText(requireContext(), "Expense saved successfully", Toast.LENGTH_SHORT).show()
            
            // Update home fragment if needed
            (requireActivity() as? MainActivity)?.updateHomeFragment()
            
            // Navigate back
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), "Failed to save expense", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 