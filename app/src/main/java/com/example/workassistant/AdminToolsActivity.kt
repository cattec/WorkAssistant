package com.example.workassistant

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
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
        supportActionBar!!.setTitle("Администрирование ролей")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val rvRoles = findViewById<RecyclerView>(R.id.rvRoles)
        rvRoles.layoutManager = LinearLayoutManager(this)
        rvRoles.adapter = RCAdapterRoles(fillRoleList())

        findViewById<Button>(R.id.addNewRole).setOnClickListener(){
            addNewRole()
        }

    }

    private fun fillRoleList(): List<MyRole> {
        val res = URL(apiCurURL + "/roles/get/?f_roles=0").getText()
        return Gson().fromJson(res, Array<MyRole>::class.java).asList()
    }

    fun addNewRole() {
        //startActivity(Intent(this, CardRoleActivity::class.java).putExtra("CurRoleID", 0))
        val intent = Intent(this, CardRoleActivity::class.java).putExtra("CurRoleID", 0)
        startForResult.launch(intent)
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val res = result.data
            if (res?.extras != null) {
                val obj = res?.extras!!["needUpate"].toString()
                if (obj == "1") {
                    findViewById<RecyclerView>(R.id.rvRoles).adapter = RCAdapterRoles(fillRoleList())
                }
            }
        }
    }

}