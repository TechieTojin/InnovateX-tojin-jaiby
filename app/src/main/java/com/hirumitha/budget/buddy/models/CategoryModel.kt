package com.hirumitha.budget.buddy.models

data class CategoryModel(
    val id: Long = 0,
    val name: String,
    val iconRes: Int,
    val color: String,
    val budgetLimit: Float = 0f,
    val isAiGenerated: Boolean = false
) 