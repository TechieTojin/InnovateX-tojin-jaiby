package com.hirumitha.budget.buddy.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.hirumitha.budget.buddy.models.BudgetModel

class BudgetDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "budget_buddy.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_BUDGET = "budget"
        
        // Column names
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_AMOUNT = "amount"
        private const val KEY_PERIOD = "period"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_BUDGET " +
                "($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_NAME TEXT, " +
                "$KEY_AMOUNT REAL, " +
                "$KEY_PERIOD TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGET")
        onCreate(db)
    }
    
    fun insertBudget(budget: BudgetModel): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, budget.name)
        values.put(KEY_AMOUNT, budget.amount)
        values.put(KEY_PERIOD, budget.period)
        
        // Insert row
        val id = db.insert(TABLE_BUDGET, null, values)
        db.close()
        return id
    }
    
    fun getBudget(id: Int): BudgetModel? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_BUDGET,
            arrayOf(KEY_ID, KEY_NAME, KEY_AMOUNT, KEY_PERIOD),
            "$KEY_ID=?",
            arrayOf(id.toString()),
            null, null, null, null
        )
        
        var budget: BudgetModel? = null
        if (cursor.moveToFirst()) {
            budget = BudgetModel(
                cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERIOD))
            )
        }
        
        cursor.close()
        db.close()
        return budget
    }
    
    fun getAllBudgets(): List<BudgetModel> {
        val budgets = mutableListOf<BudgetModel>()
        val selectQuery = "SELECT * FROM $TABLE_BUDGET ORDER BY $KEY_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        
        if (cursor.moveToFirst()) {
            do {
                val budget = BudgetModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERIOD))
                )
                budgets.add(budget)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return budgets
    }
    
    fun updateBudget(budget: BudgetModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, budget.name)
        values.put(KEY_AMOUNT, budget.amount)
        values.put(KEY_PERIOD, budget.period)
        
        // Update row
        val count = db.update(
            TABLE_BUDGET,
            values,
            "$KEY_ID=?",
            arrayOf(budget.id.toString())
        )
        
        db.close()
        return count
    }
    
    fun deleteBudget(budgetId: Int): Int {
        val db = this.writableDatabase
        val count = db.delete(
            TABLE_BUDGET,
            "$KEY_ID=?",
            arrayOf(budgetId.toString())
        )
        
        db.close()
        return count
    }
} 