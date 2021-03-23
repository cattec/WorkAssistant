package com.example.workassistant

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import kotlin.system.exitProcess

class RegUserActivity : AppCompatActivity() {

    var apiCurURL: String = ""
    val sa_pass: String = "544f336d43472b586d4e63314141484b4b6e764a58673d3d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reg_user_screen)

        apiCurURL = intent.extras!!.getString("apiCurURL").toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Регистрация пользователя")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

    }

    fun pressTryReg(view: View) {
        try {
            val login = findViewById<TextView>(R.id.ptLogin).text.toString()
            val email = findViewById<TextView>(R.id.ptMail).text.toString()
            val pass = findViewById<TextView>(R.id.editTextTextPassword).text.toString()
            val pass2 = findViewById<TextView>(R.id.editTextTextPassword2).text.toString()
            val info  = findViewById<TextView>(R.id.tvInfo)

            if (login == "") {
                info.setText("Поле Логин не может быть пустым!")
            } else
                if (email == "") {
                    info.setText("Поле Почты не может быть пустым!")
                } else
                    if (pass == "") {
                        info.setText("Папроль не может быть пустым!")
                    } else
                        if (pass != pass2) {
                            info.setText("Вы ошиблись при повторном вводе пароля! Введите повторный пароль снова.")
                            findViewById<TextView>(R.id.editTextTextPassword2).setText("")
                        } else {
                            val check_result = checkLoginEmail(login, email)
                            if (check_result.length > 2) {
                                info.setText(check_result)
                                findViewById<TextView>(R.id.editTextTextPassword2).setText("")
                            } else {
                                val result = URL(apiCurURL + "/users/createNewLogin/?sa_pass=" + sa_pass +"&flogin=" + login + "&femail=" + email + "&fpassword=" + pass).getText_noToken()
                                if (result == "{\"status\":\"ok\"}") {

                                    Toast.makeText(this, "Новый логин создан!", Toast.LENGTH_LONG).show()
                                    //получаем новый токен
                                    getNewToken(getSharedPreferences("UserInfo", 0), apiCurURL, login, pass)
                                    //преезапускаем приложение
                                    //finish()
                                    exitProcess(0)
                                } else {
                                    Toast.makeText(this, "Неудалось создать логин!", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun checkLoginEmail(newLogin: String, newEmail: String): String {
        return URL(apiCurURL + "/users/checkLoginEmail/?sa_pass=" + sa_pass +"&flogin=" + newLogin + "&femail=" + newEmail).getText_noToken()
    }

}