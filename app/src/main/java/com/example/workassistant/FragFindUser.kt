package com.example.workassistant

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.net.URL

class FragFindUser: Fragment() {

    interface OnSelectedButtonListener {
        fun selectedUsers(users: ArrayList<Int>)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_select_user, container, false)

        val rvUserMessage = rootView.findViewById<RecyclerView>(R.id.rvFindUser)
        rvUserMessage.layoutManager = LinearLayoutManager(rootView.context)
        //findUsersUpdate(rvUserMessage,"")

        rootView.findViewById<Button>(R.id.btnFindUser).setOnClickListener(){
            findUsers(rootView, rvUserMessage)
        }

        rootView.findViewById<Button>(R.id.btnSelectUsers).setOnClickListener(){
            val users = ArrayList<Int>()
            (rvUserMessage.adapter as RCAdapterUsersSimple).CadrParm.forEach {
                //users += if (it.isSelected) it.fkey + "," else ""
                if (it.isSelected) users.add(it.fkey.toInt())
            }
            if (users.count() > 0) {
                val listener = activity as OnSelectedButtonListener?
                listener?.selectedUsers(users)
                (rvUserMessage.adapter as RCAdapterUsersSimple).CadrParm.forEach {
                    it.isSelected = false
                }
                (rvUserMessage.adapter as RCAdapterUsersSimple).notifyDataSetChanged()
            } else {
                Toast.makeText( it.context,"Нечего не выбрано!", Toast.LENGTH_LONG).show()
            }
        }

        rootView.findViewById<EditText>(R.id.eUserFindString).setOnEditorActionListener() {it, kcode, event ->
            if (kcode == KeyEvent.ACTION_DOWN || kcode == KeyEvent.KEYCODE_ENTER || kcode == KeyEvent.KEYCODE_CALL)
                findUsers(rootView, rvUserMessage)
            true
        }

        return rootView
    }

    fun findUsers(rootView: View, rvUserMessage: RecyclerView) {
        rootView.context.hideKeyBoard(rootView)
        val findString = rootView.findViewById<EditText>(R.id.eUserFindString).text.toString()
        findUsersUpdate(rvUserMessage, findString)
    }

    fun findUsersUpdate(rvUserMessage: RecyclerView, filter:  String) {
        val res = URL(apiCurURL + "/users/get/?filter=" + filter).getText()
        rvUserMessage.adapter = RCAdapterUsersSimple(Gson().fromJson(res, Array<MyUser>::class.java).asList())
    }

}