package com.example.workassistant

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MessageCardActivity: AppCompatActivity()  {

    val fkey: Int = 0
    var isNewIcon: Boolean = false
    var userID: String = ""
    var token_type: String = ""
    var access_token: String = ""
    var apiCurURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_card)

        apiCurURL = intent.extras!!.getString("apiCurURL").toString()

        val settings = getSharedPreferences("UserInfo", 0)
        userID = settings.getString("userID", "").toString()
        token_type = settings.getString("token_type", "").toString()
        access_token = settings.getString("access_token", "").toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar6)
        setSupportActionBar(toolbar)
        //supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })
        findViewById<Button>(R.id.btnSaveCard).setOnClickListener {
            saveMessage()
            Toast.makeText(this, "Save Massage!!!!", Toast.LENGTH_LONG).show()
            finish();
        }


        val mesDate = SimpleDateFormat("HH:mm dd.MM.yyyy").format(Calendar.getInstance().time)
        findViewById<TextView>(R.id.tMesDate).setText(mesDate)

        findViewById<ImageView>(R.id.imgCard).setOnClickListener {
            getNewImage(this)
        }

        findViewById<TextView>(R.id.tMesCateg).setOnClickListener {
            getCategory(this)
        }
    }

    fun saveMessage() {
        var fname: String = findViewById<TextView>(R.id.tMesName).text.toString()
        var fdatecreate: String = findViewById<TextView>(R.id.tMesDate).text.toString()
        var fbody: String = findViewById<TextView>(R.id.tMesText).text.toString()
        var categ_name: String = findViewById<TextView>(R.id.tMesCateg).text.toString()



        //Если хоть что то изменилось то делаем апдетй в базу
        if (fname == "") { Toast.makeText(this, "Cat't save message NO Title!", Toast.LENGTH_LONG).show() }
        else if (fbody == "") { Toast.makeText(this, "Cat't save message NO Message Text!", Toast.LENGTH_LONG).show() }
        else {

            var message = MyMessage(fkey.toString(), fname, fdatecreate, fbody, categ_name, "0", "0", userID)
            val outResponse = Gson().toJson(message)
            val requestResult = URL(apiCurURL + "/messages/save/").sendJSONRequest(token_type, access_token, outResponse)
            if ( (requestResult != "") and (requestResult.isDigitsOnly()) ) {
                //сохряняем иконку
                val mesID = requestResult.toInt()
            }
        }
    }

    fun getCategory(ac: Activity) {
        try {
            val res = URL(apiCurURL + "/categories/").getText(token_type, access_token)
            val data = Gson().fromJson(res, Array<MyCategories>::class.java).asList()
            val categories = Array<String>(data.size) { i -> data[i].fname }
            MaterialAlertDialogBuilder(ac)
                    .setTitle("Категория сообщения?")
                    .setIcon(R.drawable.arni)
                    .setItems(categories) { dialog, which ->
                        findViewById<TextView>(R.id.tMesCateg).setText(categories[which])
                    }
                    .show()
        } catch (e: Exception) {
            Toast.makeText(ac, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun getNewImage(ac: Activity) {
        try {
            MaterialAlertDialogBuilder(ac)
                    .setTitle("Откуда будем брать изображение?")
                    .setIcon(R.drawable.arni)
                    .setItems(arrayOf("Галерея", "Камера")) { dialog, which ->
                        if (which == 0) startForResult.launch(getFromGalery())
                        else startForResult.launch(getFromCamera(ac))
                    }
                    .show()
        } catch (e: Exception) {
            Toast.makeText(ac, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val res = result.data
            if (res?.extras != null) {
                val obj = res?.extras!!["data"]
                if (obj is Bitmap) {
                    val bitmap = obj
                    isNewIcon = true
                    findViewById<ImageView>(R.id.imgCard).setImageBitmap(bitmap)
                }
            } else {
                val picturePath = res?.data
                if (picturePath != null) {
                    isNewIcon = true
                    findViewById<ImageView>(R.id.imgCard).setImageURI(picturePath)
                }
            }
        }
    }

}