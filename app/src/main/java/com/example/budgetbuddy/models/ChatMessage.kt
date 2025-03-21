package com.example.budgetbuddy.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
} 