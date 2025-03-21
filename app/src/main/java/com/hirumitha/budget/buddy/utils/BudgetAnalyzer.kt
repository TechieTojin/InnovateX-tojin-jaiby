package com.hirumitha.budget.buddy.utils

import com.hirumitha.budget.buddy.models.TransactionModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * AI-powered budget analyzer that provides insights and recommendations.
 */
class BudgetAnalyzer {
    
    /**
     * Analyzes transactions to provide spending insights.
     * 
     * @param transactions List of user transactions
     * @param monthlyIncome User's monthly income
     * @param budgetLimits Map of category to budget limits
     * @return List of insights and recommendations
     */
    fun analyzeSpending(
        transactions: List<TransactionModel>,
        monthlyIncome: Float,
        budgetLimits: Map<String, Float>
    ): List<String> {
        val insights = mutableListOf<String>()
        
        // Get current month's transactions
        val currentMonthTransactions = filterCurrentMonthTransactions(transactions)
        
        // Calculate total spending
        val totalSpending = currentMonthTransactions
            .filter { it.type == "expense" }
            .sumOf { it.amount.toDouble() }
            .toFloat()
        
        // Calculate spending by category
        val spendingByCategory = currentMonthTransactions
            .filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { entry -> 
                entry.value.sumOf { it.amount.toDouble() }.toFloat() 
            }
        
        // Add income vs. spending insight
        val savingsRate = 1 - (totalSpending / monthlyIncome)
        insights.add(generateSavingsInsight(savingsRate, monthlyIncome, totalSpending))
        
        // Add category-specific insights
        budgetLimits.forEach { (category, limit) ->
            val spent = spendingByCategory[category] ?: 0f
            if (spent > limit) {
                val overspendPercentage = ((spent - limit) / limit) * 100
                insights.add("You've exceeded your $category budget by ${overspendPercentage.toInt()}%. Consider reducing spending in this category.")
            }
        }
        
        // Find highest spending category
        spendingByCategory.maxByOrNull { it.value }?.let { (category, amount) ->
            val percentOfTotal = (amount / totalSpending) * 100
            insights.add("Your highest spending category is $category at $${amount.toInt()}, which is ${percentOfTotal.toInt()}% of your total spending.")
        }
        
        // Analyze spending trends (comparing to previous months)
        if (transactions.size > currentMonthTransactions.size) {
            val previousMonthTransactions = filterPreviousMonthTransactions(transactions)
            val previousMonthTotal = previousMonthTransactions
                .filter { it.type == "expense" }
                .sumOf { it.amount.toDouble() }
                .toFloat()
            
            val changePercentage = ((totalSpending - previousMonthTotal) / previousMonthTotal) * 100
            if (abs(changePercentage) > 10) {
                if (changePercentage > 0) {
                    insights.add("Your spending increased by ${changePercentage.toInt()}% compared to last month. Review your expenses to identify areas for potential savings.")
                } else {
                    insights.add("Great job! Your spending decreased by ${abs(changePercentage).toInt()}% compared to last month.")
                }
            }
        }
        
        // Add recommended budget adjustments
        insights.add(generateBudgetRecommendation(spendingByCategory, budgetLimits))
        
        return insights
    }
    
    /**
     * Generates a savings insight based on income and spending.
     */
    private fun generateSavingsInsight(savingsRate: Float, income: Float, spending: Float): String {
        return when {
            savingsRate < 0 -> "Alert: You're spending more than your income! You've spent $${spending.toInt()} which exceeds your monthly income of $${income.toInt()}."
            savingsRate < 0.1 -> "You're saving less than 10% of your income. Consider reducing non-essential expenses to improve your financial health."
            savingsRate < 0.2 -> "You're saving ${(savingsRate * 100).toInt()}% of your income. Aim for 20% to follow the 50/30/20 rule."
            savingsRate < 0.3 -> "Good job! You're saving ${(savingsRate * 100).toInt()}% of your income, which is on track with recommended saving rates."
            else -> "Excellent! You're saving ${(savingsRate * 100).toInt()}% of your income, which is above the recommended 20% saving rate."
        }
    }
    
    /**
     * Generates budget recommendations based on spending patterns.
     */
    private fun generateBudgetRecommendation(
        spendingByCategory: Map<String, Float>,
        budgetLimits: Map<String, Float>
    ): String {
        // Find categories that consistently exceed the budget
        val problematicCategories = mutableListOf<String>()
        
        budgetLimits.forEach { (category, limit) ->
            val spent = spendingByCategory[category] ?: 0f
            if (spent > limit * 1.1) {  // Over by 10%
                problematicCategories.add(category)
            }
        }
        
        return if (problematicCategories.isNotEmpty()) {
            "Based on your spending patterns, consider increasing your budget for ${problematicCategories.joinToString(", ")} or finding ways to reduce expenses in these categories."
        } else {
            "Your current budget allocations appear well-aligned with your spending patterns."
        }
    }
    
    /**
     * Filters transactions for the current month.
     */
    private fun filterCurrentMonthTransactions(transactions: List<TransactionModel>): List<TransactionModel> {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val currentMonth = sdf.format(Date())
        
        return transactions.filter {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
                date?.let { d -> sdf.format(d) == currentMonth } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Filters transactions for the previous month.
     */
    private fun filterPreviousMonthTransactions(transactions: List<TransactionModel>): List<TransactionModel> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val previousMonth = sdf.format(calendar.time)
        
        return transactions.filter {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
                date?.let { d -> sdf.format(d) == previousMonth } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }
} 