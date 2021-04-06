package com.example.workassistant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.getstream.chat.android.client.models.User

val DATABASENAME = "wasist_local_db"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "create table if not exists icons (iconID INTEGER, fdata BLOB);"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertIcon(iconID: Int, image: ByteArray): String {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("iconID", iconID)
        contentValues.put("fdata", image)
        val result = database.insert("icons", null, contentValues)
        if (result == (0).toLong()) {
            return "fail"
        }
        else {
            return "ok"
        }
    }

    fun readIcon(iconID: Int): ByteArray? {
        val db = this.readableDatabase
        val query = "select fdata from icons where iconID = " + iconID.toString()
        val result = db.rawQuery(query, null)
        if (result.count == 0) return null
        else {
            result.moveToFirst()
            val img = result.getBlob(0)
            return img
        }
    }

}