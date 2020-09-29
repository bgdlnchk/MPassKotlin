package com.my.admin.password_manager.activities

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.my.admin.password_manager.models.Password
import java.lang.Exception

class DBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    /**
     * This application use SQL Database for saving passwords
     * I think that it is the best way to save application's data
     * You can find a lot of information about SQL on the Internet
     */

    //Initialize SQL Database
    companion object {
        private const val DATABASE_NAME = "Passwords.db"
        private const val DATABASE_VERSION = 1
        const val PASSWORDS_TABLE_NAME = "Passwords"
        const val COLUMN_PASSWORD_ID = "passwordId"
        const val COLUMN_PASSWORD_NAME = "passwordName"
        const val COLUMN_PASSWORD_VALUE = "passwordValue"
    }

    //Create SQL Database
    override fun onCreate(db: SQLiteDatabase?) {
        val createPasswordTable : String = ("CREATE TABLE $PASSWORDS_TABLE_NAME (" +
                "$COLUMN_PASSWORD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PASSWORD_NAME TEXT," +
                "$COLUMN_PASSWORD_VALUE TEXT)")
        db?.execSQL(createPasswordTable)
    }

    //Use this method to update your database
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    //Get information about every password from database
    fun getPasswords(mCtx: Context): ArrayList<Password> {
        val qry = "Select * From $PASSWORDS_TABLE_NAME"
        val db: SQLiteDatabase = this.readableDatabase
        val cursor: Cursor = db.rawQuery(qry, null)
        val passwords = ArrayList<Password>()
        if (cursor.count == 0)
            Toast.makeText(mCtx, "No passwords added", Toast.LENGTH_SHORT).show()
        else {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val password = Password()
                password.passwordID = cursor.getInt(cursor.getColumnIndex(COLUMN_PASSWORD_ID))
                password.passwordName = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD_NAME))
                password.passwordValue = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD_VALUE))
                passwords.add(0,password)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return passwords
    }

    //Add new password to database
    fun addPassword(mCtx: Context, password: Password){
        val values = ContentValues()
        values.put(COLUMN_PASSWORD_NAME,password.passwordName)
        values.put(COLUMN_PASSWORD_VALUE,password.passwordValue)
        val db = this.writableDatabase
        try {
            db.insert(PASSWORDS_TABLE_NAME,null, values)
            Toast.makeText(mCtx, "Password Saved", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(mCtx, e.message, Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    //Delete password from database
    fun deletePassword(passwordID : Int) : Boolean{
        val qry = "Delete From $PASSWORDS_TABLE_NAME where $COLUMN_PASSWORD_ID = $passwordID"
        val db = this.writableDatabase
        var result = false
        try {
            db.execSQL(qry)
            result = true
        }catch (e : Exception){
            Log.e(ContentValues.TAG, "Error Deleting")
        }
        db.close()
        return result
    }

}