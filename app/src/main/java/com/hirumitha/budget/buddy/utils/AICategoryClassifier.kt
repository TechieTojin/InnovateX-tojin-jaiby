package com.hirumitha.budget.buddy.utils

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*

/**
 * AI-powered classifier for automatically categorizing expenses based on transaction descriptions.
 */
class AICategoryClassifier(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val labels = listOf("Food", "Transport", "Shopping", "Entertainment", "Bills", "Housing", "Health", "Education", "Other")
    
    init {
        try {
            // This would load a TensorFlow Lite model, but for now we'll use a simple implementation
            // loadModel()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TensorFlow Lite model", e)
        }
    }
    
    /**
     * Simulates loading a TensorFlow Lite model - actual implementation would load from assets
     */
    private fun loadModel() {
        val assetManager = context.assets
        val modelFile = assetManager.openFd("expense_categorization_model.tflite")
        val fileInputStream = FileInputStream(modelFile.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = modelFile.startOffset
        val declaredLength = modelFile.declaredLength
        val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        
        interpreter = Interpreter(modelBuffer)
    }
    
    /**
     * Predicts the category of an expense based on its description and amount.
     * 
     * @param description The description of the expense
     * @param amount The amount spent
     * @return The predicted category and confidence level
     */
    fun predictCategory(description: String, amount: Float): Pair<String, Float> {
        // In a real implementation, this would use the TensorFlow model
        // For now, we'll use a simple keyword-based approach
        
        val lowerDescription = description.lowercase(Locale.getDefault())
        
        // Simple rule-based classification as placeholder for ML model
        return when {
            lowerDescription.contains("restaurant") || 
            lowerDescription.contains("cafe") || 
            lowerDescription.contains("food") -> Pair("Food", 0.85f)
            
            lowerDescription.contains("uber") || 
            lowerDescription.contains("taxi") || 
            lowerDescription.contains("train") || 
            lowerDescription.contains("bus") -> Pair("Transport", 0.82f)
            
            lowerDescription.contains("amazon") || 
            lowerDescription.contains("shop") || 
            lowerDescription.contains("store") -> Pair("Shopping", 0.79f)
            
            lowerDescription.contains("cinema") || 
            lowerDescription.contains("movie") || 
            lowerDescription.contains("theater") -> Pair("Entertainment", 0.81f)
            
            lowerDescription.contains("bill") || 
            lowerDescription.contains("utility") || 
            lowerDescription.contains("electricity") || 
            lowerDescription.contains("water") -> Pair("Bills", 0.88f)
            
            lowerDescription.contains("rent") || 
            lowerDescription.contains("mortgage") -> Pair("Housing", 0.90f)
            
            lowerDescription.contains("doctor") || 
            lowerDescription.contains("hospital") || 
            lowerDescription.contains("pharmacy") -> Pair("Health", 0.87f)
            
            lowerDescription.contains("tuition") || 
            lowerDescription.contains("school") || 
            lowerDescription.contains("book") -> Pair("Education", 0.83f)
            
            else -> Pair("Other", 0.60f)
        }
    }
    
    companion object {
        private const val TAG = "AICategoryClassifier"
    }
} 