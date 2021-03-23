package com.example.workassistant

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView

class SettingsActivity : AppCompatActivity() {

    private val TimeReceiver: MyTimeReceiver = MyTimeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_screen)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar4)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Настройки")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

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