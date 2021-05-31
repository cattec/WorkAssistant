package com.example.workassistant

import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener


class ChatRoomActivity : AppCompatActivity(), FragRoomMembers.OnChangeCanalNameListener {

    var f_messages: Int = 0
    var roomName: String = "Комната"
    var MessageList: ArrayList<MyComment>? = null
    var lastMessageID: Int = 0
    var roomTimer: TimerTask? = null
    var newMessageWait: Int = 5
    var youLastMessageRead: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room_activity)

        if (intent.extras != null) {
            val cfkey = intent.extras!!.getString("f_messages")!!
            if (cfkey.isDigitsOnly())
                f_messages = cfkey.toInt()
            roomName = intent.extras!!.getString("roomName")!!
            youLastMessageRead = intent.extras!!.getString("youLastMessageRead")!!.toInt()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar8)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle(roomName)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            if (findViewById<View>(R.id.layoutFragFindUser1).visibility == View.VISIBLE) {
                findViewById<View>(R.id.layoutFragFindUser1).visibility = View.GONE
                findViewById<ImageButton>(R.id.btnAddUserToRoom).visibility = View.VISIBLE
                supportActionBar!!.setTitle(roomName)
            } else {
                finish()
            }
        })

        findViewById<View>(R.id.layoutFragFindUser1).visibility = View.GONE

        MessageList = fillPersMessageList()
        lastMessageID = MessageList!![MessageList?.count()!!-1].fkey.toInt()
        wasist_db?.setLastMesage(f_messages, lastMessageID)
        val rvPersMessage = findViewById<RecyclerView>(R.id.rvPersRoom)
        rvPersMessage.layoutManager = LinearLayoutManager(this)
        RoomFill(rvPersMessage)

        //Отправка сообщения
        val btnSendMessageRoom = findViewById<Button>(R.id.btnSendMessageRoom)
        findViewById<Button>(R.id.btnSendMessageRoom).setOnClickListener {
            val tvComment_textRoom = findViewById<EditText>(R.id.tvComment_textRoom)
            val commentText = tvComment_textRoom.text.toString()
            if (commentText.trim() != "") {
                btnSendMessageRoom.isEnabled = false
                //формируем запрос
                val nMess = MyCommentOut(myToken.userID, commentText, f_messages)
                val outComment = Gson().toJson(nMess)
                val requestResult = URL(apiCurURL + "/comments/add/").sendJSONRequest(outComment)
                tvComment_textRoom.text = null
                //this.hideKeyBoard(it)
                if (requestResult.isDigitsOnly()) {
                    val newID = requestResult.toInt()
                    if (newID > lastMessageID) {
                        lastMessageID = newID
                        wasist_db?.setLastMesage(f_messages, lastMessageID)
                        AddMessage(rvPersMessage, MyComment(requestResult, myToken.userID.toString(), "", now(), commentText, myToken.iconID.toString()))
                    }
                }
                btnSendMessageRoom.isEnabled = true
            }
        }

        //Добавление нового собеседника в чат
        findViewById<ImageButton>(R.id.btnAddUserToRoom).setOnClickListener {
            findViewById<View>(R.id.layoutFragFindUser1).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.btnAddUserToRoom).visibility = View.GONE
            supportActionBar!!.setTitle("Вернуться")
            val fmanager = supportFragmentManager
            fmanager.setFragmentResult("FragRoomID", bundleOf("f_messages" to f_messages.toString()))
            fmanager.setFragmentResult("FragRoomName", bundleOf("roomName" to roomName))
            //AddUserToRoom_old()
        }


    }

/*
    override fun selectedUsers(users: ArrayList<Int>) {
        findViewById<View>(R.id.layoutFragFindUser1).visibility = View.GONE
        findViewById<ImageButton>(R.id.btnAddUserToRoom).visibility = View.VISIBLE
        supportActionBar!!.setTitle(roomName)
        users.forEach(){ AddUser(it.toString()) }
    }

    fun AddUser(userStr: String) {
        URL(apiCurURL + "/messages/room/add/?f_messages=" + f_messages + "&f_user=" + userStr).getText()
    }
 */

/*
    fun AddUserToRoom_old() {
        val res = URL(apiCurURL + "/users/get/").getText()
        val data = Gson().fromJson(res, Array<MyUserShort>::class.java).asList()
        val users = Array<String>(data.size) { i -> "[" + data[i].fkey.toString() + "] " + data[i].fname }
        MaterialAlertDialogBuilder(this)
            .setTitle("Добавить в беседу пользователя:")
            .setIcon(R.drawable.ic_baseline_help_outline_24)
            .setItems(users) { dialog, which ->
                AddUser(users[which])
            }
            .show()
    }

    fun AddUser_old(userStr: String) {
        val idbeg = userStr.indexOf("[", 0, true) + 1
        val idend =  userStr.indexOf("]", 0, true)
        val userID: String = userStr.substring(idbeg, idend)
        URL(apiCurURL + "/messages/room/add/?f_messages=" + f_messages + "&f_user=" + userID).getText()
    }
*/

    fun RoomFill(rvPersMessage: RecyclerView) {
        rvPersMessage.adapter = RCAdapterComment(false, youLastMessageRead, MessageList!!)
        rvPersMessage.scrollToPosition(rvPersMessage.adapter?.itemCount!! - 1)
    }

    fun CheckIncomeMassage(rvPersMessage: RecyclerView) {
        val newMessages = fillPersMessageList()
        if (newMessages.count() > 0) {
            //бновляем ключь а последний сразу
            val lastKey = newMessages[newMessages.count() - 1].fkey.toInt()
            if (lastKey > lastMessageID) {
                lastMessageID = lastKey
                wasist_db?.setLastMesage(f_messages, lastMessageID)
            }
            //дбовляем все новые записи
            newMessages.forEach() {
                AddMessage(rvPersMessage, it)
            }
            //сбрасываем таймер новых сообщений
            if (youLastMessageRead > 0 ) {
                if (newMessageWait > 0) newMessageWait -= newMessageWait
                else youLastMessageRead = 0
            }
        }
    }

    fun AddMessage(rvPersMessage: RecyclerView, newComment: MyComment) {
        MessageList?.add(newComment)
        rvPersMessage.adapter?.notifyItemInserted(rvPersMessage.adapter?.itemCount!!)
        rvPersMessage.scrollToPosition(rvPersMessage.adapter?.itemCount!! - 1)
    }

    private fun fillPersMessageList(): ArrayList<MyComment> {
        val res = URL(apiCurURL + "/messages/get/room/?f_messages=" + f_messages.toString() + "&lastID=" + lastMessageID.toString()).getText()
        //return Gson().fromJson(res, Array<MyComment>::class.java).asList() as ArrayList<MyComment>
        return Gson().fromJson(res, Array<MyComment>::class.java).let { intList ->
            ArrayList<MyComment>(intList.size).apply { intList.forEach { add(it) } }
        }
    }

    override fun newCanalName(newName: String) {
        roomName = newName
    }


    override fun onStart() {
        super.onStart()
        //бновление чата по таймеру
        roomTimer = Timer("chat_" + f_messages.toString(), false).schedule(5000, period = 5000){
            runOnUiThread(object : TimerTask() {
                override fun run() {
                    CheckIncomeMassage(findViewById<RecyclerView>(R.id.rvPersRoom))
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        roomTimer?.cancel()
    }

}