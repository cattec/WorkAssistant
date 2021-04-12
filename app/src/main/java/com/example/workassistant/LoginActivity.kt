package com.example.workassistant

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL


class LoginActivity : AppCompatActivity() {

    var myToken: cToken? = null
    var oldLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Авторизация пользователя")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val settings = getSharedPreferences("UserInfo", 0)
        val myLogin: String = settings.getString("myLogin", "").toString()
        val myPassword: String = settings.getString("myPassword", "").toString()
        findViewById<EditText>(R.id.eLogin).setText(myLogin)
        findViewById<EditText>(R.id.ePass).setText(myPassword)
        oldLogin = myLogin

        val ePass = findViewById<EditText>(R.id.ePass)
        ePass.setOnEditorActionListener() {it, kcode, event ->
            if (kcode == KeyEvent.ACTION_DOWN || kcode == KeyEvent.KEYCODE_ENTER || kcode == KeyEvent.KEYCODE_CALL)
                pressOK(ePass)
            true
        }

    }

    fun pressOK(view: View) {
        try {
            val myLogin = findViewById<EditText>(R.id.eLogin).text.toString()
            val myPassword = findViewById<EditText>(R.id.ePass).text.toString()

            myToken = getNewToken(getSharedPreferences("UserInfo", 0), myLogin, myPassword)

            if(myToken != null) {
                if (oldLogin == myLogin)
                    finish()
                else
                    System.exit(0)
            }
            else {
                Toast.makeText(this, "Can't get new Token", Toast.LENGTH_LONG).show()
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun pressReg(view: View) {
        try {
            startActivity(Intent(this, RegUserActivity::class.java).putExtra("apiCurURL", apiCurURL))
        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun pressForgotPassword(view: View) {
        try {
            Toast.makeText(this, "Забыли пароль? давайте восстановим...", Toast.LENGTH_LONG).show()
        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

}