package com.example.workassistant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import java.net.URL


class UserCardActivity: AppCompatActivity() {

    var apiCurURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_card)

        apiCurURL = intent.extras!!.getString("apiCurURL").toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Карточка пользователя")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val settings = getSharedPreferences("UserInfo", 0)
        val userID: String = settings.getString("userID", "").toString()
        val iconID: String = settings.getString("iconID", "").toString()
        val myLogin: String = settings.getString("myLogin", "").toString()
        val full_name: String = settings.getString("full_name", "")!!
        val token_type: String = settings.getString("token_type", "").toString()
        val access_token: String = settings.getString("access_token", "").toString()

        val myEmail = URL(apiCurURL + "/users/email/?fkey=" + userID).getText(token_type, access_token).replace("\"", "")
        val myDescription = URL(apiCurURL + "/users/description/?fkey=" + userID).getText(token_type, access_token).replace("\"", "")

        findViewById<TextView>(R.id.userCardLogin1).setText(myLogin)
        findViewById<EditText>(R.id.userCardName1).setText(full_name)
        findViewById<EditText>(R.id.userCardEmail1).setText(myEmail)
        findViewById<EditText>(R.id.UserCardDesc1).setText(myDescription)


        if (iconID != "") findViewById<ImageView>(R.id.userAvaCard).load(apiCurURL + "/icon/?fkey=" + iconID) { addHeader("Authorization", token_type + ' ' + access_token) }

    }

    fun pressSave(view: View) {
        try {
            Toast.makeText(this, "Save Card", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun loadImageFromGalery(view: View) {
        try {
            //Toast.makeText(this, "Load Image", Toast.LENGTH_LONG).show()

            //val intent = Intent(Intent.ACTION_PICK)
            //intent.type = "image/*"
            //startActivityForResult(intent, REQUEST_CODE)
            /*startActivity(intent)
            */
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }


}
