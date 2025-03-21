package com.hirumitha.budget.buddy.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.database.TransactionDBHelper
import com.hirumitha.budget.buddy.databinding.FragmentReceiptScannerBinding
import com.hirumitha.budget.buddy.models.TransactionModel
import com.hirumitha.budget.buddy.utils.ReceiptScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReceiptScannerFragment : Fragment() {

    private var _binding: FragmentReceiptScannerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var receiptScanner: ReceiptScanner
    private lateinit var dbHelper: TransactionDBHelper
    private var currentPhotoPath: String? = null
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicture()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to scan receipts", Toast.LENGTH_LONG).show()
        }
    }
    
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { processImageFromGallery(it) }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                processImage(bitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceiptScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize components
        receiptScanner = ReceiptScanner(requireContext())
        dbHelper = TransactionDBHelper(requireContext())
        
        // Set up click listeners
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndTakePhoto()
        }
        
        binding.btnSelectFromGallery.setOnClickListener {
            openGallery()
        }
        
        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction()
        }
        
        // Initially hide result section
        showLoadingState(false)
        binding.resultSection.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun checkCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePicture()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionRationaleDialog()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun showCameraPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Camera Permission Required")
            .setMessage("We need camera access to scan your receipts and extract expense information.")
            .setPositiveButton("Grant Permission") { _, _ ->
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun takePicture() {
        val photoFile = createImageFile()
        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            cameraLauncher.launch(photoURI)
        }
    }
    
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "RECEIPT_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(null)
        
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun processImageFromGallery(uri: Uri) {
        lifecycleScope.launch {
            showLoadingState(true)
            
            val bitmap = withContext(Dispatchers.IO) {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
            
            processImage(bitmap)
        }
    }
    
    private fun processImage(bitmap: Bitmap) {
        lifecycleScope.launch {
            try {
                // Save bitmap to a small preview
                binding.ivReceiptPreview.setImageBitmap(bitmap)
                
                // Process the receipt using OCR
                val transaction = withContext(Dispatchers.Default) {
                    receiptScanner.scanReceipt(bitmap)
                }
                
                // Display the extracted data
                displayTransactionResults(transaction)
                
                // Show success message
                Toast.makeText(
                    requireContext(),
                    "Receipt processed successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                
            } catch (e: Exception) {
                // Handle errors
                binding.resultSection.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error processing receipt: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                showLoadingState(false)
            }
        }
    }
    
    private fun displayTransactionResults(transaction: TransactionModel) {
        binding.resultSection.visibility = View.VISIBLE
        
        binding.etCategory.setText(transaction.category)
        binding.etAmount.setText(transaction.amount.toString())
        binding.etDate.setText(transaction.date)
        
        // Update UI to allow editing of extracted data
        binding.btnSaveTransaction.visibility = View.VISIBLE
    }
    
    private fun saveTransaction() {
        val category = binding.etCategory.text.toString()
        val amountText = binding.etAmount.text.toString()
        val date = binding.etDate.text.toString()
        
        if (category.isBlank() || amountText.isBlank() || date.isBlank()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val amount = amountText.toFloatOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Create transaction model
        val transaction = TransactionModel(
            id = 0,
            type = "expense",
            category = category,
            amount = amount,
            date = date
        )
        
        // Save to database
        val success = dbHelper.insertTransaction(transaction) > 0
        
        if (success) {
            Toast.makeText(requireContext(), "Transaction saved successfully!", Toast.LENGTH_SHORT).show()
            
            // Reset UI
            binding.etCategory.text?.clear()
            binding.etAmount.text?.clear()
            binding.etDate.text?.clear()
            binding.ivReceiptPreview.setImageResource(R.drawable.ic_receipt_placeholder)
            binding.resultSection.visibility = View.GONE
            
            // Refresh the home fragment if it's visible
            (activity as? com.hirumitha.budget.buddy.activities.MainActivity)?.updateHomeFragment()
        } else {
            Toast.makeText(requireContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showLoadingState(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnTakePhoto.isEnabled = !isLoading
        binding.btnSelectFromGallery.isEnabled = !isLoading
        binding.btnSaveTransaction.isEnabled = !isLoading
    }
} 