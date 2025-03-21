package com.hirumitha.budget.buddy.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.hirumitha.budget.buddy.database.TransactionDBHelper
import com.hirumitha.budget.buddy.models.TransactionModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

/**
 * Utility class for importing bank transaction data from CSV files
 */
class BankDataImporter(private val context: Context) {
    
    private val dbHelper: TransactionDBHelper by lazy { TransactionDBHelper(context) }
    private val aiClassifier: AIExpenseClassifier by lazy { AIExpenseClassifier(context) }
    
    /**
     * Import transactions from a CSV file
     * 
     * @param fileUri The URI of the CSV file
     * @return The number of transactions successfully imported
     */
    fun importFromCsv(fileUri: Uri): Int {
        var successCount = 0
        val tag = "BankDataImporter"
        
        try {
            Log.d(tag, "Opening file: $fileUri")
            val inputStream = context.contentResolver.openInputStream(fileUri)
            if (inputStream == null) {
                Log.e(tag, "Failed to open input stream for URI: $fileUri")
                return 0
            }

            inputStream.use { stream ->
                val reader = BufferedReader(InputStreamReader(stream))
                try {
                    // Skip header line
                    val header = reader.readLine()
                    Log.d(tag, "CSV Header: $header")
                    
                    // Process each line
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { 
                            Log.d(tag, "Processing line: $it")
                            val transaction = processLine(it)
                            if (transaction != null) {
                                try {
                                    val id = dbHelper.insertTransaction(transaction)
                                    if (id > 0) {
                                        successCount++
                                        Log.d(tag, "Inserted transaction #$successCount: $id")
                                    } else {
                                        Log.w(tag, "Failed to insert transaction: $transaction")
                                    }
                                } catch (e: Exception) {
                                    Log.e(tag, "Error inserting transaction", e)
                                }
                            } else {
                                Log.w(tag, "Failed to parse line: $it")
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e(tag, "Error reading CSV file", e)
                } finally {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e(tag, "Error closing reader", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error importing CSV", e)
        }
        
        Log.d(tag, "Import completed with $successCount successful transactions")
        return successCount
    }
    
    /**
     * Import demo data from the Kaggle dataset
     * 
     * @return The number of transactions successfully imported
     */
    fun importKaggleDemo(): Int {
        var successCount = 0
        val tag = "BankDataImporter"
        
        try {
            Log.d(tag, "Opening demo dataset from assets")
            // Check if the asset exists first
            val assetManager = context.assets
            val assetFiles = assetManager.list("") ?: emptyArray()
            
            if (!assetFiles.contains("bank_transactions.csv")) {
                Log.e(tag, "Demo file not found in assets! Available assets: ${assetFiles.joinToString()}")
                // Create minimal demo data since file is missing
                return createFallbackDemoData()
            }
            
            // Open the demo CSV file from assets
            context.assets.open("bank_transactions.csv").use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                try {
                    // Skip header line
                    val header = reader.readLine()
                    Log.d(tag, "Demo CSV Header: $header")
                    
                    // Process each line
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        line?.let {
                            Log.d(tag, "Processing demo line: $it")
                            val transaction = processLine(it)
                            if (transaction != null) {
                                try {
                                    val id = dbHelper.insertTransaction(transaction)
                                    if (id > 0) {
                                        successCount++
                                        Log.d(tag, "Inserted demo transaction #$successCount: $id")
                                    } else {
                                        Log.w(tag, "Failed to insert demo transaction: $transaction")
                                    }
                                } catch (e: Exception) {
                                    Log.e(tag, "Error inserting demo transaction", e)
                                }
                            } else {
                                Log.w(tag, "Failed to parse demo line: $it")
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e(tag, "Error reading demo CSV file", e)
                } finally {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e(tag, "Error closing demo reader", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error importing demo data", e)
            // If something went wrong with the asset, create fallback data
            if (successCount == 0) {
                successCount = createFallbackDemoData()
            }
        }
        
        Log.d(tag, "Demo import completed with $successCount successful transactions")
        return successCount
    }
    
    /**
     * Create fallback demo data if the asset file is missing
     */
    private fun createFallbackDemoData(): Int {
        var count = 0
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastMonth = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
            Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
        )
        
        // Sample transactions
        val demoTransactions = listOf(
            TransactionModel(0, "Income", "Salary", 2500.0f, lastMonth),
            TransactionModel(0, "Expense", "Groceries", 125.50f, today),
            TransactionModel(0, "Expense", "Rent", 1200.0f, lastMonth),
            TransactionModel(0, "Expense", "Utilities", 85.75f, today),
            TransactionModel(0, "Income", "Bonus", 500.0f, today),
            TransactionModel(0, "Expense", "Dining", 65.25f, today),
            TransactionModel(0, "Expense", "Transportation", 45.0f, today)
        )
        
        // Insert each transaction
        for (transaction in demoTransactions) {
            try {
                val id = dbHelper.insertTransaction(transaction)
                if (id > 0) count++
            } catch (e: Exception) {
                Log.e("BankDataImporter", "Error inserting fallback transaction", e)
            }
        }
        
        return count
    }
    
    /**
     * Process a single line from the CSV file
     * 
     * @param line A line from the CSV file
     * @return A TransactionModel object, or null if parsing failed
     */
    private fun processLine(line: String): TransactionModel? {
        val tag = "BankDataImporter"
        try {
            // Enhanced CSV parsing with better support for quoted fields
            val fields = parseCSVLine(line)
            
            if (fields.size < 3) {
                Log.w(tag, "Not enough columns in CSV line (need at least 3): $line")
                return null
            }
            
            // Extract values from CSV - handle potential ArrayIndexOutOfBoundsException
            val date = fields.getOrNull(0)?.trim() ?: ""
            val description = fields.getOrNull(1)?.trim() ?: ""
            val amountStr = fields.getOrNull(2)?.trim() ?: ""
            
            Log.d(tag, "Parsed date: $date, description: $description, amount: $amountStr")
            
            // Parse amount safely - handle various formats
            val amount = try {
                // Remove currency symbols and handle European/US formats
                val cleanedAmount = amountStr
                    .replace("[^0-9,.-]".toRegex(), "") // Remove all non-numeric chars except . , -
                    .replace(",", ".") // Standardize on period as decimal separator
                cleanedAmount.toFloat()
            } catch (e: Exception) {
                Log.e(tag, "Failed to parse amount: $amountStr", e)
                return null
            }
            
            // Determine transaction type
            val type = if (amount >= 0) "Income" else "Expense"
            
            // Use AI to categorize transaction
            val category = if (type == "Expense") {
                try {
                    aiClassifier.classifyExpense(description, amount.toDouble())
                } catch (e: Exception) {
                    Log.e(tag, "Error during AI classification, using 'Other'", e)
                    "Other"
                }
            } else {
                "Income"
            }
            
            // Format date to app format
            val formattedDate = try {
                // Try multiple date formats (add more as needed)
                val dateFormats = listOf(
                    "yyyy-MM-dd", 
                    "MM/dd/yyyy",
                    "dd/MM/yyyy",
                    "yyyy/MM/dd"
                )
                
                var parsedDate: Date? = null
                for (format in dateFormats) {
                    try {
                        parsedDate = SimpleDateFormat(format, Locale.US).parse(date)
                        if (parsedDate != null) break
                    } catch (e: Exception) {
                        // Try next format
                    }
                }
                
                if (parsedDate != null) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsedDate)
                } else {
                    Log.w(tag, "Failed to parse date: $date, using current date")
                    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())
                }
            } catch (e: Exception) {
                Log.e(tag, "Error formatting date: $date", e)
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())
            }
            
            // Create transaction model with absolute amount value for expenses
            return TransactionModel(
                0,
                type,
                category,
                Math.abs(amount),
                formattedDate
            )
        } catch (e: Exception) {
            Log.e(tag, "Error processing CSV line: $line", e)
            return null
        }
    }
    
    /**
     * Parse a CSV line properly handling quoted values
     */
    private fun parseCSVLine(line: String): List<String> {
        val result = ArrayList<String>()
        var current = StringBuilder()
        var inQuotes = false
        
        for (i in line.indices) {
            val c = line[i]
            when {
                c == '"' -> {
                    // Toggle the inQuotes flag when we see a double quote
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        // Escaped quote (two double quotes together)
                        current.append('"')
                        i.inc() // Skip the next quote
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == ',' && !inQuotes -> {
                    // End of current field
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> {
                    current.append(c)
                }
            }
        }
        
        // Add the last field
        result.add(current.toString())
        
        return result
    }
} 