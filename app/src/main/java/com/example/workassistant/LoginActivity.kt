package com.example.workassistant

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL


class LoginActivity : AppCompatActivity() {

    var apiCurURL: String = ""
    //var myLogin: String = "monkey"
    //var myPassword: String = "123456"
    var myToken: cToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        val settings = getSharedPreferences("UserInfo", 0)
        val myLogin: String = settings.getString("myLogin", "").toString()
        val myPassword: String = settings.getString("myPassword", "").toString()
        findViewById<EditText>(R.id.eLogin).setText(myLogin)
        findViewById<EditText>(R.id.ePass).setText(myPassword)

        apiCurURL = intent.extras!!.getString("apiCurURL").toString()
    }

    fun pressOK(view: View) {
        try {
            val myLogin = findViewById<EditText>(R.id.eLogin).text.toString()
            val myPassword = findViewById<EditText>(R.id.ePass).text.toString()

            myToken = getNewToken(getSharedPreferences("UserInfo", 0), apiCurURL, myLogin, myPassword)

            if(myToken != null) {
                finish()
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
            Toast.makeText(this, "Тут будем регаться", Toast.LENGTH_LONG).show()
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