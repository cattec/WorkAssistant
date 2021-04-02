package com.example.workassistant

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.Base64


class CardRoleActivity: AppCompatActivity() {

    var CurRoleID: String = ""
    var isNewIcon: Boolean = false
    var newIconID: Int = 0

    var old_name = ""
    var old_myDescription = ""
    var old_fsystem: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.role_card)

        val resID = intent.extras!!.getString("CurRoleID")
        if (resID != null) CurRoleID = resID.toString()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Карточка роли")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        if (CurRoleID > "") {
            val userfullinfo = URL(apiCurURL + "/roles/get/?f_roles=" + CurRoleID).getText()
            val data = Gson().fromJson(userfullinfo, Array<MyRole>::class.java).asList()

            val iconID = data[0].f_icons
            old_name = data[0].fname
            old_myDescription = data[0].fdescription
            val usercount = data[0].usercount
            old_fsystem = data[0].fsystem

            findViewById<Switch>(R.id.isSystem).isChecked = old_fsystem
            findViewById<EditText>(R.id.roleCardName1).setText(old_name)
            findViewById<EditText>(R.id.roleCardDesc1).setText(old_myDescription)
            val roleUserCount = findViewById<TextView>(R.id.roleUserCount)
            roleUserCount.setText(roleUserCount.text.toString() + " " + usercount.toString())
            setImageImageView(this, iconID.toString(), findViewById<ImageView>(R.id.roleAvaCard))
        }

        findViewById<ImageView>(R.id.roleAvaCard).setOnClickListener {getNewImage(this)}

    }

    fun pressSave(view: View) {
        try {
            val name = findViewById<EditText>(R.id.roleCardName1).text.toString()
            val myDescription = findViewById<EditText>(R.id.roleCardDesc1).text.toString()
            val fsystem = findViewById<Switch>(R.id.isSystem).isChecked

            var isNeedUpdate: Boolean = false

            //Если хоть что то изменилось то делаем апдетй в базу
            if ((old_name != name) or (old_fsystem != fsystem) or (old_myDescription != myDescription)) {
                //формируем запрос
                val nRoleUpdate = myRoleUpdate(
                    CurRoleID.toInt(),
                    name,
                    myDescription,
                    fsystem
                )
                val outResponse = Gson().toJson(nRoleUpdate)
                val requestResult = URL(apiCurURL + "/role/roleupdate/").sendJSONRequest(outResponse)
                if (requestResult.toString() == "\"OK\"") isNeedUpdate = true
            }

            //Сохраняем новую иконку
            if (isNewIcon == true) {
                val stream = ByteArrayOutputStream()
                val bitmap = resizeBitmap(200,(findViewById<ImageView>(R.id.userAvaCard).getDrawable() as BitmapDrawable).bitmap)
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)
                val image = stream.toByteArray()
                val base64Encoded: String = Base64.getEncoder().encodeToString(image)

                val nUserUpdateIcon = MyUpdateIcon(
                    0,
                    CurRoleID.toInt(),
                    base64Encoded
                )
                val outResponse = Gson().toJson(nUserUpdateIcon)
                val requestResult = URL(apiCurURL + "/icons/updateObjectIcon/?ftable=roles").sendJSONRequest(outResponse)
                isNeedUpdate = true

                if ( (requestResult != "") and (requestResult.isDigitsOnly()) ) newIconID = requestResult.toInt()

            }

            if (isNeedUpdate == true) {
                Toast.makeText(this, "Role Saved", Toast.LENGTH_LONG).show()
                finish()
            }
            else Toast.makeText(this, "Cant't save role card", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Cant't save role card: " + e.toString(), Toast.LENGTH_LONG).show()
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
                    findViewById<ImageView>(R.id.roleAvaCard).setImageBitmap(bitmap)
                }
            } else {
                val picturePath = res?.data
                if (picturePath != null) {
                    isNewIcon = true
                    findViewById<ImageView>(R.id.roleAvaCard).setImageURI(picturePath)
                }
            }
        }
    }




}
