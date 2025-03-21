package com.hirumitha.budget.buddy.models

data class BudgetModel(
    val id: Int,
    val name: String,
    val amount: Double,
    val period: String
) 