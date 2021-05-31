package com.example.workassistant

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.text.isDigitsOnly
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.Base64

class CardMessageActivity: AppCompatActivity()  {

    var fkey: Int = 0
    var isNewIcon: Boolean = false
    var userID: String = ""
    var old_fname: String = ""
    var old_fbody: String = ""
    var old_categ_name: String = ""
    var canedit: Boolean = true
    var spec: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_card)

        if (intent.extras != null) {
            val cfkey = intent.extras!!.getString("f_messages")
            if (cfkey != null) if (cfkey.isDigitsOnly()) fkey = cfkey.toInt()
            val spec_in = intent.extras!!.getString("spec")
            if (spec_in != null) spec = spec_in
        }

        val settings = getSharedPreferences("UserInfo", 0)
        userID = settings.getString("userID", "").toString()

        if (fkey > 0) {
            val res = URL(apiCurURL + "/messages/get/?fkey=" + fkey.toString() + "&categ_name=none&personal=false").getText()
            val curMessage = Gson().fromJson(res, Array<MyMessage>::class.java).asList()
            old_fname = curMessage[0].fname
            old_fbody = curMessage[0].fbody
            old_categ_name = curMessage[0].categ_name
            findViewById<EditText>(R.id.tMesName).setText(curMessage[0].fname)
            findViewById<TextView>(R.id.tMesDate).setText(curMessage[0].fdatecreate)
            findViewById<EditText>(R.id.tMesText).setText(curMessage[0].fbody)
            findViewById<TextView>(R.id.tMesCateg).setText(curMessage[0].categ_name)
            if (curMessage[0].f_icons != "") setImageImageView(this, curMessage[0].f_icons, findViewById<ImageView>(R.id.imgCard))
            /*if (curMessage[0].f_icons != "")
                findViewById<ImageView>(R.id.imgCard).load(apiCurURL + "/icon/?fkey=" + curMessage[0].f_icons) { addHeader("Authorization",myToken.token_type + ' ' + myToken.access_token) }*/
            canedit = curMessage[0].canedit
        } else {
            findViewById<TextView>(R.id.tMesDate).setText(now())
        }

        //если не автор и не админ то делаем все недоступным для редактиования
        if (canedit) {
            findViewById<ImageView>(R.id.imgCard).setOnClickListener {
                getNewImage(this)
            }
            findViewById<TextView>(R.id.tMesCateg).setOnClickListener {
                getCategory(this)
            }
            findViewById<Button>(R.id.btnSaveCard).setOnClickListener {
                if (saveMessage()) {
                    Toast.makeText(this, "Save Massage!!!!", Toast.LENGTH_LONG).show()
                    finish();
                }
            }
        } else {
            findViewById<EditText>(R.id.tMesName).isEnabled = false
            findViewById<EditText>(R.id.tMesText).isEnabled = false
            findViewById<Button>(R.id.btnSaveCard).visibility = View.GONE
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar6)
        setSupportActionBar(toolbar)
        //supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveMessage(): Boolean {
        var isSave = false
        val fname: String = findViewById<TextView>(R.id.tMesName).text.toString()
        val fdatecreate: String = findViewById<TextView>(R.id.tMesDate).text.toString()
        val fbody: String = findViewById<TextView>(R.id.tMesText).text.toString()
        val categ_name: String = findViewById<TextView>(R.id.tMesCateg).text.toString()

        val message = MyMessage(fkey.toString(), fname, fdatecreate, fbody, categ_name, "0", "0", userID, true)
        val outResponse = Gson().toJson(message)

        //Если хоть что то изменилось то делаем апдетй в базу
        if (fkey > 0) {
            if ((fname != old_fname) or (fbody != old_fbody) or (categ_name != old_categ_name) ) {
                //что то измениллось апдейтим запись
                val requestResult = URL(apiCurURL + "/messages/save/").sendJSONRequest(outResponse)
                isSave = true
            }
        } else {
            //создание новой записи
            if (fname == "") {
                Toast.makeText(this, "Cat't save message NO Title!", Toast.LENGTH_LONG).show()
            } else if (fbody == "") {
                Toast.makeText(this, "Cat't save message NO Message Text!", Toast.LENGTH_LONG)
                    .show()
            } else {
                val requestResult = URL(apiCurURL + "/messages/save/").sendJSONRequest(outResponse)
                if ((requestResult != "") and (requestResult.isDigitsOnly())) {
                    //Сохраняем ключ новой записи
                    fkey = requestResult.toInt()
                    isSave = true
                }
            }
        }

        if (isNewIcon) {
            //сохранение картинки
            if (fkey > 0) {
                val bitmap = resizeBitmap(200,(findViewById<ImageView>(R.id.imgCard).getDrawable() as BitmapDrawable).bitmap)
                insertIcon("messages", fkey, bitmap)
                isSave = true
            }
        }
        return isSave
    }

    fun getCategory(ac: Activity) {
        try {
            /*val res = URL(apiCurURL + "/categories/").getText(token_type, access_token)
            val data = Gson().fromJson(res, Array<MyCategories>::class.java).asList()
            val categories = Array<String>(data.size) { i -> data[i].fname }*/
            val categories = getCategories(spec)
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
                val obj = res.extras!!["data"]
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