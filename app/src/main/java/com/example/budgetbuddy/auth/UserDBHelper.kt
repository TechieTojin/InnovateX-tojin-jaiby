package com.example.budgetbuddy.auth

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Users.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_EMAIL TEXT UNIQUE," +
                "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun registerUser(username: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password) // In a real app, you should hash the password
        }
        
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password) // In a real app, you should hash the password
        
        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    fun checkUserExists(email: String): Boolean {
        val db = this.readableDatabase
        val selection = "$COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        
        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    fun getUserName(email: String): String {
        var username = ""
        val db = this.readableDatabase
        val selection = "$COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_USERNAME),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
        }
        
        cursor.close()
        db.close()
        return username
    }
} 