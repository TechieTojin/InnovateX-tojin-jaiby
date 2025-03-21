package com.hirumitha.budget.buddy.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.hirumitha.budget.buddy.models.TransactionModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Utility class for scanning receipts and extracting transaction information using ML Kit OCR.
 */
class ReceiptScanner(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val aiCategoryClassifier = AICategoryClassifier(context)

    /**
     * Scans a receipt image and extracts transaction details.
     * 
     * @param bitmap The receipt image
     * @return Extracted transaction information
     */
    suspend fun scanReceipt(bitmap: Bitmap): TransactionModel = suspendCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                try {
                    val extractedData = extractTransactionData(visionText)
                    continuation.resume(extractedData)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing receipt text", e)
                    continuation.resumeWithException(e)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Text recognition failed", e)
                continuation.resumeWithException(e)
            }
    }

    /**
     * Extracts transaction data from the recognized text.
     */
    private fun extractTransactionData(visionText: Text): TransactionModel {
        val text = visionText.text.lowercase(Locale.getDefault())
        
        // Extract total amount
        val amount = extractAmount(text)
        
        // Extract date
        val date = extractDate(text)
        
        // Extract merchant name
        val merchantName = extractMerchantName(visionText)
        
        // Get AI-based category
        val (category, _) = aiCategoryClassifier.predictCategory(merchantName, amount)
        
        return TransactionModel(
            id = 0,
            type = "expense",
            category = category,
            amount = amount,
            date = date
        )
    }

    /**
     * Extracts the total amount from receipt text.
     */
    private fun extractAmount(text: String): Float {
        // Common patterns for total amount on receipts
        val patterns = listOf(
            Pattern.compile("total:?\\s*\\$?(\\d+\\.\\d{2})"),
            Pattern.compile("amount:?\\s*\\$?(\\d+\\.\\d{2})"),
            Pattern.compile("grand total:?\\s*\\$?(\\d+\\.\\d{2})")
        )
        
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1)?.toFloatOrNull() ?: 0f
            }
        }
        
        // Fallback to find any number that might be the total
        val numberPattern = Pattern.compile("\\$?(\\d+\\.\\d{2})")
        val matcher = numberPattern.matcher(text)
        val amounts = mutableListOf<Float>()
        
        while (matcher.find()) {
            matcher.group(1)?.toFloatOrNull()?.let { amounts.add(it) }
        }
        
        // Return the largest amount found as it's likely the total
        return amounts.maxOrNull() ?: 0f
    }

    /**
     * Extracts the date from receipt text.
     */
    private fun extractDate(text: String): String {
        // Common date patterns on receipts
        val datePatterns = listOf(
            Pattern.compile("(\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4})"),
            Pattern.compile("date:?\\s*(\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4})")
        )
        
        for (pattern in datePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return formatDate(matcher.group(1) ?: "")
            }
        }
        
        // If no date found, return current date
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Formats the extracted date to a standard format.
     */
    private fun formatDate(dateStr: String): String {
        // Try to parse various date formats
        val possibleFormats = listOf(
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()),
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        
        for (format in possibleFormats) {
            try {
                val date = format.parse(dateStr)
                if (date != null) {
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                // Try next format
            }
        }
        
        // If parsing fails, return original string
        return dateStr
    }

    /**
     * Extracts merchant name from receipt text.
     */
    private fun extractMerchantName(visionText: Text): String {
        // In real receipts, merchant name is often in the first few lines
        // We'll use the first line or block that's not a date or total
        
        val firstFewBlocks = visionText.textBlocks.take(3)
        for (block in firstFewBlocks) {
            val text = block.text.trim()
            if (text.isNotEmpty() && 
                !text.matches(Regex(".*\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4}.*")) && 
                !text.contains("total", ignoreCase = true) &&
                !text.contains("receipt", ignoreCase = true)) {
                    return text
            }
        }
        
        return "Unknown Merchant"
    }

    companion object {
        private const val TAG = "ReceiptScanner"
    }
} 