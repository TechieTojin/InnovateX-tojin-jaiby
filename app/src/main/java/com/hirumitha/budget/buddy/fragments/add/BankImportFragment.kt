package com.hirumitha.budget.buddy.fragments.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.activities.MainActivity
import com.hirumitha.budget.buddy.databinding.FragmentBankImportBinding
import com.hirumitha.budget.buddy.utils.BankDataImporter
import java.util.concurrent.Executors

class BankImportFragment : Fragment() {

    private var _binding: FragmentBankImportBinding? = null
    private val binding get() = _binding!!
    private lateinit var bankDataImporter: BankDataImporter
    private val executor = Executors.newSingleThreadExecutor()
    
    // Use the OpenDocument contract which works more reliably than GetContent
    private val getDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            Log.d("BankImport", "URI selected: $it")
            importCsvFile(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankImportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            // Initialize bank data importer
            bankDataImporter = BankDataImporter(requireContext())
            
            // Set up CSV import button
            binding.btnImportCsv.setOnClickListener {
                openFilePicker()
            }
            
            // Set up PDF import button
            binding.btnImportPdf.setOnClickListener {
                Toast.makeText(requireContext(), "PDF import not implemented yet", Toast.LENGTH_SHORT).show()
            }
            
            // Set up Kaggle demo import button
            binding.btnImportDemo.setOnClickListener {
                importKaggleDemo()
            }
        } catch (e: Exception) {
            Log.e("BankImport", "Error in onViewCreated", e)
            Toast.makeText(context, "Error initializing: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openFilePicker() {
        try {
            // Open document with MIME types for CSV files
            getDocument.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv", "*/*"))
        } catch (e: Exception) {
            Log.e("BankImport", "Error opening file picker", e)
            Toast.makeText(context, "Unable to open file picker: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun importCsvFile(uri: Uri) {
        try {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvStatus.text = getString(R.string.importing_data)
            binding.tvStatus.visibility = View.VISIBLE
            
            // Disable buttons during import
            setButtonsEnabled(false)
            
            // Try to get persistent permission - handle errors gracefully
            try {
                val contentResolver = context?.contentResolver
                if (contentResolver != null && uri.scheme == "content") {
                    // This will throw SecurityException if the URI doesn't support taking permissions
                    contentResolver.takePersistableUriPermission(
                        uri, 
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            } catch (e: SecurityException) {
                // Log but continue - we'll still try to read the file
                Log.e("BankImport", "Cannot take persistent permission for URI: $uri", e)
            } catch (e: Exception) {
                Log.e("BankImport", "Error taking URI permission", e)
            }
            
            executor.execute {
                var importCount = 0
                try {
                    Log.d("BankImport", "Starting CSV import")
                    importCount = bankDataImporter.importFromCsv(uri)
                    Log.d("BankImport", "Imported $importCount transactions")
                } catch (e: Exception) {
                    Log.e("BankImport", "Error importing CSV", e)
                }
                
                val finalCount = importCount
                
                activity?.runOnUiThread {
                    try {
                        binding.progressBar.visibility = View.GONE
                        
                        if (finalCount > 0) {
                            binding.tvStatus.text = getString(R.string.import_success, finalCount)
                            try {
                                (activity as? MainActivity)?.updateHomeFragment()
                            } catch (e: Exception) {
                                Log.e("BankImport", "Error updating home fragment", e)
                            }
                        } else {
                            binding.tvStatus.text = getString(R.string.import_failed)
                        }
                    } catch (e: Exception) {
                        Log.e("BankImport", "Error updating UI", e)
                    } finally {
                        setButtonsEnabled(true)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BankImport", "Error in importCsvFile", e)
            binding.progressBar.visibility = View.GONE
            binding.tvStatus.text = "Error: ${e.message}"
            setButtonsEnabled(true)
        }
    }
    
    private fun importKaggleDemo() {
        try {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvStatus.text = getString(R.string.importing_demo_data)
            binding.tvStatus.visibility = View.VISIBLE
            
            // Disable buttons during import
            setButtonsEnabled(false)
            
            executor.execute {
                var importCount = 0
                try {
                    Log.d("BankImport", "Starting Kaggle demo import")
                    importCount = bankDataImporter.importKaggleDemo()
                    Log.d("BankImport", "Imported $importCount demo transactions")
                } catch (e: Exception) {
                    Log.e("BankImport", "Error importing demo data", e)
                }
                
                val finalCount = importCount
                
                activity?.runOnUiThread {
                    try {
                        binding.progressBar.visibility = View.GONE
                        
                        if (finalCount > 0) {
                            binding.tvStatus.text = getString(R.string.import_demo_success, finalCount)
                            try {
                                (activity as? MainActivity)?.updateHomeFragment()
                            } catch (e: Exception) {
                                Log.e("BankImport", "Error updating home fragment", e)
                            }
                        } else {
                            binding.tvStatus.text = getString(R.string.import_demo_failed)
                        }
                    } catch (e: Exception) {
                        Log.e("BankImport", "Error updating UI", e)
                    } finally {
                        setButtonsEnabled(true)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BankImport", "Error in importKaggleDemo", e)
            binding.progressBar.visibility = View.GONE
            binding.tvStatus.text = "Error: ${e.message}"
            setButtonsEnabled(true)
        }
    }
    
    private fun setButtonsEnabled(enabled: Boolean) {
        try {
            if (_binding != null) {
                binding.btnImportCsv.isEnabled = enabled
                binding.btnImportPdf.isEnabled = enabled
                binding.btnImportDemo.isEnabled = enabled
            }
        } catch (e: Exception) {
            Log.e("BankImport", "Error setting buttons enabled state", e)
        }
    }

    override fun onDestroyView() {
        try {
            super.onDestroyView()
            _binding = null
        } catch (e: Exception) {
            Log.e("BankImport", "Error in onDestroyView", e)
        }
    }
} 