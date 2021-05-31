package com.example.workassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.net.URL
import java.util.*
import kotlin.concurrent.schedule

class ChatChanelsActivity : AppCompatActivity(), FragFindUser.OnSelectedButtonListener {

    var chanelsTimer: TimerTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_chanels_activity)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar7)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Чат")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            if (findViewById<View>(R.id.layoutFragFindUser1).visibility == View.VISIBLE) {
                findViewById<View>(R.id.layoutFragFindUser1).visibility = View.GONE
                findViewById<ImageButton>(R.id.btnAddChatRoom).visibility = View.VISIBLE
                supportActionBar!!.setTitle("Чат")
            } else {
                finish()
            }
        })


        findViewById<View>(R.id.layoutFragFindUser1).visibility = View.GONE

        findViewById<ImageButton>(R.id.btnAddChatRoom).setOnClickListener {
            findViewById<View>(R.id.layoutFragFindUser1).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.btnAddChatRoom).visibility = View.GONE
            supportActionBar!!.setTitle("Вернуться")
            //addChatChanel()
        }
        
        findViewById<RecyclerView>(R.id.rvPersMessage).layoutManager = LinearLayoutManager(this)

    }

    override fun selectedUsers(users: ArrayList<Int>) {
        findViewById<View>(R.id.layoutFragFindUser1).visibility = View.GONE
        findViewById<ImageButton>(R.id.btnAddChatRoom).visibility = View.VISIBLE
        supportActionBar!!.setTitle("Чат")
        CreateChat(users)
    }

    fun CreateChat(users: ArrayList<Int>): Int {
        users.add(myToken.userID)
        val userStr: String = users.map { it.toString() }.toString().replace(" ","")
        val requestResult = URL(apiCurURL + "/messages/create/grouproom/?users=" + userStr).getText()
        if ((requestResult != "") and (requestResult.isDigitsOnly())) {
            return requestResult.toInt()
        }
        return 0
    }

    fun addChatChanel() {
        val res = URL(apiCurURL + "/users/get/").getText()
        val data = Gson().fromJson(res, Array<MyUserShort>::class.java).asList()
        val users = Array<String>(data.size) { i -> "[" + data[i].fkey.toString() + "] " + data[i].fname }
        MaterialAlertDialogBuilder(this)
            .setTitle("Добавить в чат пользователя:")
            .setIcon(R.drawable.ic_baseline_help_outline_24)
            .setItems(users) { dialog, which ->
                val chatID = CreateChat_old(users[which])
                if (chatID > 0) {
                    messageListRefresh()
                    startActivity(
                        Intent(this, ChatRoomActivity::class.java)
                        .putExtra("f_messages", chatID.toString())
                        .putExtra("roomName", users[which]))
                }
            }
            .show()
    }

    fun CreateChat_old(userStr: String): Int {
        val idbeg = userStr.indexOf("[",0,true) + 1
        val idend =  userStr.indexOf("]",0,true)
        val userID2: String = userStr.substring(idbeg, idend)
        val requestResult = URL(apiCurURL + "/messages/create/room/?user1=" + myToken.userID.toString() +"&user2=" + userID2).getText()
        if ((requestResult != "") and (requestResult.isDigitsOnly())) {
            return requestResult.toInt()
        }
        return 0
    }

    fun messageListRefresh() {
        val rvPersMessage = findViewById<RecyclerView>(R.id.rvPersMessage)
        rvPersMessage.adapter = RCAdapterPersMessages(fillPersMessageList())
    }

    private fun fillPersMessageList(): List<MyPersMessage> {
        val res = URL(apiCurURL + "/messages/get/pers/?f_users=" + myToken.userID).getText()
        return Gson().fromJson(res, Array<MyPersMessage>::class.java).asList()
    }

    override fun onStart() {
        super.onStart()
        //загрузка токена
        chanelsTimer = Timer("chanelsTimer", false).schedule(10, period = 10000){
            runOnUiThread(object : TimerTask() {
                override fun run() {
                    messageListRefresh()
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        chanelsTimer?.cancel()
    }

}