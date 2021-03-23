package com.example.workassistant

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.net.URL


class UserCardActivity: AppCompatActivity() {

    var apiCurURL: String = ""
    var CurUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_card)

        apiCurURL = intent.extras!!.getString("apiCurURL").toString()
        CurUserID = intent.extras!!.getString("CurUserID").toString()

        val settings = getSharedPreferences("UserInfo", 0)
        val userID: String = settings.getString("userID", "").toString()
        val token_type: String = settings.getString("token_type", "").toString()
        val access_token: String = settings.getString("access_token", "").toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        if (userID == CurUserID) {supportActionBar!!.setTitle("Ваша карточка пользователя")}
            else { supportActionBar!!.setTitle("Карточка пользователя") }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val userfullinfo = URL(apiCurURL + "/users/userfullinfo/?fkey=" + CurUserID).getText(token_type, access_token)
        val data = Gson().fromJson('[' +userfullinfo + ']', Array<MyUser>::class.java).asList()

        val iconID: String = data[0].f_icons
        val myLogin: String = data[0].flogin
        val full_name: String = data[0].fname
        val myEmail = data[0].femail
        val myDescription = data[0].fdescription
        findViewById<TextView>(R.id.userCardLogin1).setText(myLogin)
        findViewById<EditText>(R.id.userCardName1).setText(full_name)
        findViewById<EditText>(R.id.userCardEmail1).setText(myEmail)
        findViewById<EditText>(R.id.UserCardDesc1).setText(myDescription)
        if (iconID != "") findViewById<ImageView>(R.id.userAvaCard).load(apiCurURL + "/icon/?fkey=" + iconID) { addHeader("Authorization", token_type + ' ' + access_token) }

        if (userID == CurUserID) {
            findViewById<Button>(R.id.tbnSendPersMessage).visibility = View.GONE
            findViewById<ImageView>(R.id.userAvaCard).setOnClickListener {
                loadImageFromGalery(it)
            }
        } else {
            findViewById<LinearLayout>(R.id.layuot_SaveSettings).visibility = View.GONE
            findViewById<LinearLayout>(R.id.passLayout).visibility = View.GONE
            findViewById<EditText>(R.id.userCardName1).isEnabled = false
            findViewById<EditText>(R.id.userCardEmail1).isEnabled = false
            findViewById<EditText>(R.id.UserCardDesc1).isEnabled = false
        }

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

            val items = arrayOf("Галерея", "Камера")

            MaterialAlertDialogBuilder(this)
                    .setTitle("Откуда будем брать изображение?")
                    .setIcon(R.drawable.arni)
                    .setItems(items) { dialog, which ->
                        if (which == 0) loadGalery()
                            else getFromCamera()
                    }
                    .show()

            //Toast.makeText(this, "Load Image", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun loadGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivity(intent)
    }

    fun getFromCamera() {
        val permissionStatus = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //val res = startActivity(intent)
/*
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }*/
            //startActivityForResult(intent, 1)
            //val res = setResult(RESULT_OK,intent)

            startForResult.launch(intent)

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 101);
        }
    }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val res = result.data
                //val intent = result
                //result.data()
                // Handle the Intent
            }
        }


}
