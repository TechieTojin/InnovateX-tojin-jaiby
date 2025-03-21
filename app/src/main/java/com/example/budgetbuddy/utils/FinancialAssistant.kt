package com.example.budgetbuddy.utils

import android.os.Handler
import android.os.Looper
import java.util.Random

/**
 * A simple AI assistant that provides financial advice without using external APIs.
 * This class uses predefined responses based on keywords in the user's query.
 */
class FinancialAssistant {
    
    private val random = Random()
    
    // Map of keywords to possible responses
    private val responses = mapOf(
        "save" to listOf(
            "To save money effectively, try the 50/30/20 rule: 50% for needs, 30% for wants, and 20% for savings.",
            "Consider automating your savings by setting up automatic transfers to a separate savings account.",
            "Try tracking all your expenses for a month to identify areas where you can cut back and save more.",
            "Look for small daily expenses that add up over time, like coffee or subscription services you don't use often."
        ),
        "budget" to listOf(
            "Creating a budget starts with understanding your income and expenses. Track everything for a month to get a clear picture.",
            "Try using the envelope budgeting method, where you allocate cash to different spending categories.",
            "Zero-based budgeting can be effective - assign every dollar of income to a specific purpose until you reach zero.",
            "Review your budget regularly and adjust as needed. A budget should be flexible as your life circumstances change."
        ),
        "invest" to listOf(
            "For beginners, consider starting with index funds which provide broad market exposure with lower fees.",
            "Remember to diversify your investments across different asset classes to manage risk.",
            "Consider your time horizon - longer-term goals can typically handle more investment risk.",
            "Dollar-cost averaging (investing a fixed amount regularly) can help reduce the impact of market volatility."
        ),
        "debt" to listOf(
            "Consider using the debt avalanche method - paying off high-interest debt first while making minimum payments on others.",
            "The debt snowball method focuses on paying off smaller debts first to build momentum and motivation.",
            "Look into consolidating high-interest debts into a lower-interest option if possible.",
            "Always pay more than the minimum payment on credit cards to avoid long-term interest costs."
        ),
        "credit" to listOf(
            "To improve your credit score, always pay bills on time and keep credit card balances low.",
            "Regularly check your credit report for errors and dispute any inaccuracies you find.",
            "Avoid opening too many new credit accounts in a short period as this can lower your score.",
            "Maintaining older credit accounts can help as length of credit history affects your score."
        ),
        "retirement" to listOf(
            "Start saving for retirement as early as possible to take advantage of compound interest.",
            "Try to contribute enough to your employer's retirement plan to get the full matching contribution if available.",
            "Consider diversifying retirement savings between traditional and Roth accounts for tax flexibility.",
            "As you approach retirement, gradually shift investments to more conservative options to protect your savings."
        ),
        "emergency" to listOf(
            "Aim to build an emergency fund that covers 3-6 months of essential expenses.",
            "Keep your emergency fund in a liquid account like a high-yield savings account for easy access.",
            "Start small if needed - even $500-$1000 can help cover minor emergencies.",
            "Replenish your emergency fund as soon as possible after using it."
        ),
        "income" to listOf(
            "Consider developing multiple income streams to increase financial stability.",
            "Look for opportunities to increase your primary income through skill development or job changes.",
            "Side hustles can be a good way to earn extra income using your existing skills and interests.",
            "Passive income sources like dividends or rental income can help build long-term wealth."
        )
    )
    
    // Generic responses for when no keywords match
    private val genericResponses = listOf(
        "I'd be happy to help with your financial questions. Could you provide more specific details about what you're looking for?",
        "That's an interesting financial question. To give you better advice, could you elaborate a bit more?",
        "I'm here to help with your financial planning. What specific aspect would you like advice on?",
        "Financial planning is important! Could you clarify what specific area you're interested in learning about?",
        "I can provide guidance on saving, budgeting, investing, debt management, and more. What would you like to focus on?"
    )
    
    // Greeting responses
    private val greetingResponses = listOf(
        "Hello! I'm your Trackyyy financial assistant. How can I help you today?",
        "Hi there! I'm here to help with your financial questions. What would you like to know?",
        "Welcome to Trackyyy! I'm your AI financial advisor. What financial topics can I assist you with?",
        "Greetings! I'm your personal financial assistant. How can I help you manage your finances better today?"
    )
    
    // Analysis responses
    private val analysisResponses = listOf(
        "Based on your recent transactions, you've spent 30% more on dining out this month compared to last month. Consider setting a budget for eating out.",
        "I notice your grocery spending has decreased by 15% - great job finding savings there!",
        "Your utility bills seem to be increasing. You might want to check for ways to improve energy efficiency.",
        "You're consistently saving about 10% of your income, which is good. Consider increasing it to 15-20% if possible.",
        "Your subscription services add up to $85 monthly. Review if you're using all of them regularly."
    )
    
    /**
     * Generate a response based on the user's message
     */
    fun generateResponse(userMessage: String, callback: (String) -> Unit) {
        // Simulate processing time
        Handler(Looper.getMainLooper()).postDelayed({
            val response = when {
                isGreeting(userMessage) -> getRandomResponse(greetingResponses)
                containsAnalysisRequest(userMessage) -> getRandomResponse(analysisResponses)
                else -> findResponseByKeywords(userMessage)
            }
            callback(response)
        }, 1000 + random.nextInt(1000).toLong())
    }
    
    private fun isGreeting(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("hello") || 
               lowerMessage.contains("hi") || 
               lowerMessage.contains("hey") || 
               lowerMessage.contains("greetings") ||
               lowerMessage == "hello" ||
               lowerMessage == "hi"
    }
    
    private fun containsAnalysisRequest(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return (lowerMessage.contains("analyze") || 
                lowerMessage.contains("analysis") || 
                lowerMessage.contains("review")) &&
               (lowerMessage.contains("spending") || 
                lowerMessage.contains("expenses") || 
                lowerMessage.contains("transactions"))
    }
    
    private fun findResponseByKeywords(message: String): String {
        val lowerMessage = message.lowercase()
        
        // Check for keywords in the message
        for ((keyword, possibleResponses) in responses) {
            if (lowerMessage.contains(keyword)) {
                return getRandomResponse(possibleResponses)
            }
        }
        
        // If no keywords match, return a generic response
        return getRandomResponse(genericResponses)
    }
    
    private fun getRandomResponse(responses: List<String>): String {
        return responses[random.nextInt(responses.size)]
    }
} 