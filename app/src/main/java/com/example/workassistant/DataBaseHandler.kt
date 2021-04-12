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
        db?.execSQL("create table if not exists lastChatMessage (f_chat INTEGER, f_messages INTEGER);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db);
    }

    fun setLastMesage(f_chat: Int, f_messages: Int)
    {
        val db = this.writableDatabase
        if (existsMessage(f_chat) > 0)
        {
            val contentValues = ContentValues()
            contentValues.put("f_messages", f_messages)
            db.update("lastChatMessage", contentValues,"f_chat = ?", arrayOf(f_chat.toString()))
        } else {
            val contentValues = ContentValues()
            contentValues.put("f_chat", f_chat)
            contentValues.put("f_messages", f_messages)
            db.insert("lastChatMessage", null, contentValues)
        }
    }

    fun existsMessage(f_chat: Int): Int {
        val db = this.writableDatabase
        val query = "select f_chat from lastChatMessage where f_chat = " + f_chat.toString()
        val result = db.rawQuery(query, null)
        if (result.count == 0) return 0
        else {
            result.close()
            return f_chat
        }
    }

    fun getLastChatMessage(f_chat: Int): Int {
        val db = this.readableDatabase
        val query = "select f_messages from lastChatMessage where f_chat = " + f_chat.toString()
        val result = db.rawQuery(query, null)
        if (result.count == 0) return 0
        else {
            result.moveToFirst()
            val f_messages = result.getInt(0)
            result.close()
            return f_messages
        }
    }

    fun insertIcon(iconID: Int, image: ByteArray): String {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("iconID", iconID)
        contentValues.put("fdata", image)
        val result = db.insert("icons", null, contentValues)
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
            result.close()
            return img
        }
    }

}