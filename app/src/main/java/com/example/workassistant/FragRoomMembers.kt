package com.example.workassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.net.URL
import java.util.ArrayList

class FragRoomMembers: Fragment(), FragFindUser.OnSelectedButtonListener {

    var rootView: View? = null
    var f_messages: Int = 0
    var oldCanalName = ""

    interface OnChangeCanalNameListener {
        fun newCanalName(newName: String)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_room_members, container, false)

        rootView?.findViewById<View>(R.id.layoutFrgmentFind)?.visibility = View.GONE
        rootView?.findViewById<Button>(R.id.btnCancelFind)?.setOnClickListener() {
            rootView?.findViewById<View>(R.id.layoutFrgmentFind)?.visibility = View.GONE
            rootView?.findViewById<View>(R.id.RoomFragMain)?.visibility = View.VISIBLE
        }

        val rvRoomUser = rootView?.findViewById<RecyclerView>(R.id.rvRoomUser)
        rvRoomUser?.layoutManager = LinearLayoutManager(rootView?.context)
        rvRoomUserUpdate()

        rootView?.findViewById<Button>(R.id.btnDelRoom)?.setOnClickListener() {
            leaveRoom()
        }

        rootView?.findViewById<Button>(R.id.btnAddUsertoRoom)?.setOnClickListener() {
            addUsers()
        }

        setFragmentResultListener("FragRoomID") { requestKey, bundle ->
            f_messages = bundle.getString("f_messages")!!.toInt()
            rvRoomUserUpdate()
        }

        setFragmentResultListener("FragRoomName") { requestKey, bundle ->
            oldCanalName = bundle.getString("roomName")!!
            rootView?.findViewById<EditText>(R.id.ptCanalName)?.setText(oldCanalName)
        }

        val btnSaveName = rootView?.findViewById<ImageButton>(R.id.btnSaveCanalName)
        btnSaveName?.visibility = View.GONE
        rootView?.findViewById<EditText>(R.id.ptCanalName)?.addTextChangedListener {
            if (it.toString() == oldCanalName) {
                btnSaveName?.visibility = View.GONE
            } else {
                btnSaveName?.visibility = View.VISIBLE
            }
        }
        btnSaveName?.setOnClickListener() {
            val newName = rootView?.findViewById<EditText>(R.id.ptCanalName)?.text!!.toString()
            if (newName.length > 0) {
                URL(apiCurURL + "/messages/rename/room/?f_messages=" + f_messages.toString() + "&newname=" + newName).getText()
                it.visibility = View.GONE
                val listener = activity as FragRoomMembers.OnChangeCanalNameListener?
                listener?.newCanalName(newName)
            }
        }

        return rootView
    }

    fun rvRoomUserUpdate() {
        val res = URL(apiCurURL + "/messages/get/room/members/?f_messages=" + f_messages.toString()).getText()
        rootView?.findViewById<RecyclerView>(R.id.rvRoomUser)?.adapter = RCAdapterUsersRoom(f_messages, Gson().fromJson(res, Array<MyRoomMembers>::class.java).asList())
    }

    fun leaveRoom() {
        MaterialAlertDialogBuilder(rootView?.context!!)
                .setTitle("Выход из канала")
                .setMessage("Вы уверены что хотите покинуть этот канал?")
                .setNegativeButton("Отмена") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("Выйти") { dialog, which ->
                    // Respond to positive button press
                    URL(apiCurURL + "/messages/room/delete/?f_messages=" + f_messages.toString()
                            + "&f_users=" + myToken.userID.toString()
                            + "&f_roles=0"
                    ).getText()
                    rvRoomUserUpdate()
                }
                .show()
    }

    fun addUsers() {
        rootView?.findViewById<View>(R.id.layoutFrgmentFind)?.visibility = View.VISIBLE
        rootView?.findViewById<View>(R.id.RoomFragMain)?.visibility = View.GONE
    }

    override fun selectedUsers(users: ArrayList<Int>) {
        rootView?.findViewById<View>(R.id.layoutFrgmentFind)?.visibility = View.GONE
        rootView?.findViewById<View>(R.id.RoomFragMain)?.visibility = View.VISIBLE
        if (users.count() > 0) {
            if (InsertUsers(users) > 0) rvRoomUserUpdate()
        }
    }

    fun InsertUsers(users: ArrayList<Int>): Int {
        val userStr: String = users.map { it.toString() }.toString().replace(" ","")
        val requestResult = URL(apiCurURL + "/messages/room/add/users/?f_messages=" + f_messages.toString() + "&users=" + userStr).getText()
        if ((requestResult != "") and (requestResult.isDigitsOnly())) {
            return requestResult.toInt()
        }
        return 0
    }

}