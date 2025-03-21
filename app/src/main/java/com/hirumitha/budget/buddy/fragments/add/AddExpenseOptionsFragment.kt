package com.hirumitha.budget.buddy.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.databinding.FragmentAddExpenseOptionsBinding

class AddExpenseOptionsFragment : Fragment() {

    private var _binding: FragmentAddExpenseOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Manual expense entry
        binding.cardManualEntry.setOnClickListener {
            findNavController().navigate(R.id.action_add_to_manual_expense)
        }

        // Receipt scanner
        binding.cardReceiptScanner.setOnClickListener {
            findNavController().navigate(R.id.action_add_to_receipt_scanner)
        }

        // Bank statement import
        binding.cardBankImport.setOnClickListener {
            findNavController().navigate(R.id.action_add_to_bank_import)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 