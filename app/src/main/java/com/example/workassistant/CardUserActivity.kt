package com.example.workassistant

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.Base64


class CardUserActivity: AppCompatActivity() {

    var CurUserID: String = ""
    var isNewIcon: Boolean = false
    var newIconID: Int = 0

    var old_full_name = ""
    var old_myEmail = ""
    var old_myDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_card)

        CurUserID = intent.extras!!.getString("CurUserID").toString()

        val settings = getSharedPreferences("UserInfo", 0)
        val userID: String = settings.getString("userID", "").toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        if (userID == CurUserID) {supportActionBar!!.setTitle("Ваша карточка пользователя")}
            else { supportActionBar!!.setTitle("Карточка пользователя") }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val userfullinfo = URL(apiCurURL + "/users/userfullinfo/?fkey=" + CurUserID).getText()
        val data = Gson().fromJson('[' + userfullinfo + ']', Array<MyUser>::class.java).asList()

        val iconID: String = data[0].f_icons
        val myLogin: String = data[0].flogin
        val full_name: String = data[0].fname
        val myEmail = data[0].femail
        val myDescription = data[0].fdescription
        findViewById<TextView>(R.id.userCardLogin1).setText(myLogin)
        findViewById<EditText>(R.id.userCardName1).setText(full_name)
        findViewById<EditText>(R.id.userCardEmail1).setText(myEmail)
        findViewById<EditText>(R.id.UserCardDesc1).setText(myDescription)

        setImageImageView(this, iconID, findViewById<ImageView>(R.id.userAvaCard))
        /*if (iconID != "") findViewById<ImageView>(R.id.userAvaCard).load(apiCurURL + "/icon/?fkey=" + iconID) { addHeader(
            "Authorization",
            myToken.token_type + ' ' + myToken.access_token
        ) }*/

        old_full_name = full_name
        old_myEmail = myEmail
        old_myDescription = myDescription

        if (userID == CurUserID) {

            findViewById<LinearLayout>(R.id.repeatNewPassLayout).visibility = View.GONE
            findViewById<Button>(R.id.tbnSendPersMessage).visibility = View.GONE
            findViewById<ImageView>(R.id.userAvaCard).setOnClickListener {
                getNewImage(this)
            }

            //после того как начинаем набирать пароль открываем строчку повторить папроль
            val passEdit = findViewById<EditText>(R.id.userCardPass1)
            passEdit.addTextChangedListener {
                if (it.toString() != "") {
                    findViewById<LinearLayout>(R.id.repeatNewPassLayout).visibility = View.VISIBLE
                } else {
                    findViewById<LinearLayout>(R.id.repeatNewPassLayout).visibility = View.GONE
                }
            }

            //после того как вводят повтор пароля проверям равен ли он с тем что ввели выше
            val repeatEdit = findViewById<EditText>(R.id.userCardPassRepeat1)
            val repeatPassInfo = findViewById<TextView>(R.id.repeatPassInfo)
            repeatPassInfo.setText("Не идентичен")
            repeatPassInfo.setTextColor(getColor(R.color.red))
            repeatEdit.addTextChangedListener {
                if (it.toString() == passEdit.text.toString()){
                    repeatPassInfo.setText("Идентичен")
                    repeatPassInfo.setTextColor(getColor(R.color.green))
                } else {
                    repeatPassInfo.setText("Не идентичен")
                    repeatPassInfo.setTextColor(getColor(R.color.red))
                }
            }

        } else {
            findViewById<LinearLayout>(R.id.layuot_SaveSettings).visibility = View.GONE
            findViewById<LinearLayout>(R.id.passLayout).visibility = View.GONE
            findViewById<EditText>(R.id.userCardName1).isEnabled = false
            findViewById<EditText>(R.id.userCardEmail1).isEnabled = false
            findViewById<EditText>(R.id.UserCardDesc1).isEnabled = false
        }

    }

    fun pressSendPersonalMessage(view: View) {
        val requestResult = URL(apiCurURL + "/messages/create/room/?user1=" + myToken.userID.toString() +"&user2=" + CurUserID).getText()
        if ((requestResult != "") and (requestResult.isDigitsOnly())) {
            //Сохраняем ключ новой записи
            val f_message = requestResult.toInt()
            startActivity(Intent(this, ChatRoomActivity::class.java)
                    .putExtra("f_messages", f_message.toString())
                    .putExtra("roomName", old_full_name)
            )
        }
    }

    fun pressSave(view: View) {
        try {
            val full_name = findViewById<EditText>(R.id.userCardName1).text.toString()
            val myEmail = findViewById<EditText>(R.id.userCardEmail1).text.toString()
            val myDescription = findViewById<EditText>(R.id.UserCardDesc1).text.toString()
            val myPass = if (findViewById<EditText>(R.id.userCardPass1).text.toString() == findViewById<EditText>(
                    R.id.userCardPassRepeat1
                ).text.toString())
                findViewById<EditText>(R.id.userCardPass1).text.toString() else ""

            var isNeedUpdate: Boolean = false

            //Если хоть что то изменилось то делаем апдетй в базу
            if ((old_full_name != full_name) or (old_myEmail != myEmail) or (old_myDescription != myDescription) or (myPass != "")) {
                //формируем запрос
                val nUserUpdate = myUserUpdate(
                    CurUserID.toInt(),
                    full_name,
                    myEmail,
                    myDescription,
                    myPass
                )
                val outResponse = Gson().toJson(nUserUpdate)
                val requestResult = URL(apiCurURL + "/users/userupdate/").sendJSONRequest(outResponse)
                if (requestResult.toString() == "\"OK\"") isNeedUpdate = true
            }

            //Сохраняем новую иконку
            if (isNewIcon == true) {
                val stream = ByteArrayOutputStream()
                val bitmap = resizeBitmap(200,(findViewById<ImageView>(R.id.userAvaCard).getDrawable() as BitmapDrawable).bitmap)
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)
                val image = stream.toByteArray()
                val base64Encoded: String = Base64.getEncoder().encodeToString(image)
                val nUserUpdateIcon = MyUpdateIcon(0, CurUserID.toInt(), base64Encoded)
                val outResponse = Gson().toJson(nUserUpdateIcon)
                val requestResult = URL(apiCurURL + "/icons/updateObjectIcon/?ftable=users").sendJSONRequest(outResponse)
                isNeedUpdate = true
                if ( (requestResult != "") and (requestResult.isDigitsOnly()) ) newIconID = requestResult.toInt()
            }

            if (isNeedUpdate == true) {
                Toast.makeText(this, "Save Card", Toast.LENGTH_LONG).show()

                val settings = getSharedPreferences("UserInfo", 0)
                val editor = settings.edit()
                if (myPass != "") editor.putString("myPassword", myPass)
                editor.putString("full_name", full_name)
                if (newIconID > 0) editor.putString("iconID", newIconID.toString())
                editor.commit()

                System.exit(0)
            }
                else Toast.makeText(this, "Cant't save user card", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Cant't save user card: " + e.toString(), Toast.LENGTH_LONG).show()
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
                    findViewById<ImageView>(R.id.userAvaCard).setImageBitmap(bitmap)
                }
            } else {
                val picturePath = res?.data
                if (picturePath != null) {
                    isNewIcon = true
                    findViewById<ImageView>(R.id.userAvaCard).setImageURI(picturePath)
                }
            }
        }
    }




}
