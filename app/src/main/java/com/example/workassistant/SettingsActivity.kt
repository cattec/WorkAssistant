package com.example.workassistant

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import java.net.URL

class SettingsActivity : AppCompatActivity() {

    var apiCurURL: String = ""
    var userID: String = ""
    var token_type: String = ""
    var access_token: String = ""

    private val TimeReceiver: MyTimeReceiver = MyTimeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_screen)

        apiCurURL = intent.extras!!.getString("apiCurURL").toString()
        val imageLoader = ImageLoader.Builder(this)
                .availableMemoryPercentage(0.25)
                .crossfade(true)
                .build()

        val settings = getSharedPreferences("UserInfo", 0)
        userID = settings.getString("userID", "").toString()
        token_type = settings.getString("token_type", "").toString()
        access_token = settings.getString("access_token", "").toString()

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
        rvRoles.adapter = RCAdapterRoles(imageLoader, token_type, access_token, apiCurURL, fillRoleList())

    }

    private fun fillRoleList(): List<MyRole> {
        val res = URL(apiCurURL + "/roles/get/").getText(token_type, access_token)
        return Gson().fromJson(res, Array<MyRole>::class.java).asList()
    }

    fun onClickReceiver (view: View)
    {
        val sw1: Switch = findViewById(R.id.switch1)
        if (sw1.isChecked) registerBroadcastReceiver(sw1) else unregisterBroadcastReceiver(sw1)
    }

    fun registerBroadcastReceiver(view: View?) {
        this.registerReceiver(TimeReceiver, IntentFilter("android.intent.action.TIME_TICK"))
        Toast.makeText(applicationContext, "Приёмник включен", Toast.LENGTH_SHORT).show()
    }

    fun unregisterBroadcastReceiver(view: View?) {
        this.unregisterReceiver(TimeReceiver)
        Toast.makeText(applicationContext, "Приёмник выключён", Toast.LENGTH_SHORT).show()
    }
}