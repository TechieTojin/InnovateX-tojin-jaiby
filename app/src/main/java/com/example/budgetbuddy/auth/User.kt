package com.example.budgetbuddy.auth

data class User(
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String
) 