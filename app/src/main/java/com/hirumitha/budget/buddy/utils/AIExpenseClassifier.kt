package com.hirumitha.budget.buddy.utils

import android.content.Context
import java.util.Locale

/**
 * A utility class that provides AI-based expense categorization functionality.
 * This class analyzes expense descriptions and suggests appropriate categories.
 */
class AIExpenseClassifier(private val context: Context) {
    
    /**
     * Classifies an expense based on its description and amount.
     * Uses keyword matching for basic classification.
     * 
     * @param description The description of the expense
     * @param amount The amount of the expense (optional, may be used for better classification)
     * @return The suggested category for the expense
     */
    fun classifyExpense(description: String, amount: Double = 0.0): String {
        val lowerDesc = description.lowercase(Locale.ROOT)
        
        // Food related keywords
        if (containsAny(lowerDesc, listOf("food", "restaurant", "meal", "lunch", "dinner", "breakfast", 
                "cafe", "pizza", "burger", "grocery", "groceries"))) {
            return "Food"
        }
        
        // Transportation related keywords
        if (containsAny(lowerDesc, listOf("gas", "fuel", "uber", "lyft", "taxi", "cab", "ride", 
                "fare", "bus", "train", "subway", "metro", "transport", "car", "vehicle"))) {
            return "Transportation"
        }
        
        // Housing related keywords
        if (containsAny(lowerDesc, listOf("rent", "mortgage", "housing", "apartment", "house", 
                "property", "real estate", "accommodation"))) {
            return "Housing"
        }
        
        // Utilities related keywords
        if (containsAny(lowerDesc, listOf("electricity", "water", "gas", "internet", "wifi", 
                "utility", "bill", "phone", "broadband"))) {
            return "Utilities"
        }
        
        // Entertainment related keywords
        if (containsAny(lowerDesc, listOf("movie", "theatre", "theater", "concert", "show", 
                "entertainment", "netflix", "spotify", "subscription", "game", "gaming"))) {
            return "Entertainment"
        }
        
        // Health related keywords
        if (containsAny(lowerDesc, listOf("doctor", "medical", "medicine", "pharmacy", "health", 
                "hospital", "clinic", "dentist", "therapy", "healthcare"))) {
            return "Health"
        }
        
        // Education related keywords
        if (containsAny(lowerDesc, listOf("tuition", "education", "school", "college", "university", 
                "course", "class", "book", "textbook", "learning"))) {
            return "Education"
        }
        
        // Shopping related keywords
        if (containsAny(lowerDesc, listOf("shopping", "clothes", "clothing", "fashion", "apparel", 
                "shoes", "accessories", "electronics", "purchase"))) {
            return "Shopping"
        }
        
        // Travel related keywords
        if (containsAny(lowerDesc, listOf("travel", "hotel", "flight", "airbnb", "vacation", 
                "holiday", "trip", "tour", "booking"))) {
            return "Travel"
        }
        
        // Insurance related keywords
        if (containsAny(lowerDesc, listOf("insurance", "coverage", "policy", "premium"))) {
            return "Insurance"
        }
        
        // Investments related keywords
        if (containsAny(lowerDesc, listOf("investment", "stock", "bond", "mutual fund", "etf", 
                "crypto", "bitcoin", "invest", "portfolio", "dividend"))) {
            return "Investments"
        }
        
        // Personal Care related keywords
        if (containsAny(lowerDesc, listOf("haircut", "salon", "spa", "beauty", "cosmetics", 
                "personal care", "grooming", "makeup", "skincare"))) {
            return "Personal Care"
        }
        
        // Default fallback
        return "Other"
    }
    
    /**
     * Helper function to check if a string contains any of the keywords in the list
     */
    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it) }
    }
} 