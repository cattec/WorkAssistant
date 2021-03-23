package com.example.workassistant

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SupportActivity : AppCompatActivity() {
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

    }
}