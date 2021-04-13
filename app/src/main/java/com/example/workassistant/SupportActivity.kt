package com.example.workassistant

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SupportActivity : AppCompatActivity(), FragFindUser.OnSelectedButtonListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_screen)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar5)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Тех. поддержка")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        findViewById<View>(R.id.layoutFragFindUser).visibility = View.GONE
        findViewById<Button>(R.id.btnSendTask).setOnClickListener(){
            findViewById<View>(R.id.layoutFragFindUser).visibility = View.VISIBLE
        }

    }

    override fun selectedUsers(users: ArrayList<Int>) {
        findViewById<View>(R.id.layoutFragFindUser).visibility = View.GONE
        findViewById<EditText>(R.id.editTextTextPersonName).setText(("Select Users: " + users.map { it.toString() }))
    }

}