package com.example.workassistant

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson
import java.net.URL

class AdminToolsActivity : AppCompatActivity() {

    var userID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admintools)

        val settings = getSharedPreferences("UserInfo", 0)
        userID = settings.getString("userID", "").toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar4)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Настройки")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        //Проверку на админа еще поставить и если не админ скурывать этот список и не загрудать его вообще load roles list
        val rvRoles = findViewById<RecyclerView>(R.id.rvRoles)
        rvRoles.layoutManager = LinearLayoutManager(this)
        rvRoles.adapter = RCAdapterRoles(imageLoader!!, fillRoleList())

    }

    private fun fillRoleList(): List<MyRole> {
        val res = URL(apiCurURL + "/roles/get/").getText()
        return Gson().fromJson(res, Array<MyRole>::class.java).asList()
    }

}