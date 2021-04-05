package com.example.workassistant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
import coil.request.ImageRequest
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL
import java.util.*

class cToken (
        val userID: Int,
        val iconID: Int,
        val full_name: String,
        val access_token: String,
        val token_type: String
)

class MyCategories (
        var fkey: Int,
        var fname: String
)

class MyRole (
        var fkey: Int,
        var fname: String,
        var fdescription: String,
        var usercount: Int,
        var f_icons: Int,
        val fsystem: Boolean
)

class MyMessage (
        var fkey: String,
        var fname: String,
        var fdatecreate: String,
        var fbody: String,
        var categ_name: String,
        var f_categories: String,
        var f_icons: String,
        var f_users_create: String
)

class MyComment (
        var fkey: String,
        var f_users_create: String,
        var fname: String,
        var fdatecreate: String,
        var fbody: String,
        var f_icons: String
)

class MyUser (
    var fkey: String,
    var flogin: String,
    var fname: String,
    var fdisable: String,
    var fdescription: String,
    var femail: String,
    var f_icons: String
)

class MyCommentOut (
        val f_users_create: Int,
        val fbody: String,
        val f_messages: Int
)

class myUserUpdate (
    val fkey: Int,
    val fname: String,
    val femail: String,
    val fdescription: String,
    val fpass: String
)

class myRoleUpdate (
    val fkey: Int,
    val fname: String,
    val fdescription: String,
    val fsystem: Boolean
)

class MyUpdateIcon (
    var f_icons: Int,
    var f_tableKey: Int,
    var fdata: String
)

fun getNewToken(settings: SharedPreferences, myLogin: String, myPassword: String): cToken? {

    val tokenResponse = URL(apiCurURL + "/token").getToken(myLogin, myPassword)

    //Если не было ошибки то возвращаем токен
    if (!tokenResponse.contains("you_error_detail")) {
        val myNewToken = Gson().fromJson(
                '[' + tokenResponse + ']',
                Array<cToken>::class.java
        ) [0]

        //После того как был успешный логин сохраняем последние удачные данные
        val editor = settings.edit()
        editor.putString("myLogin", myLogin)
        editor.putString("myPassword", myPassword)
        editor.putString("userID", myNewToken.userID.toString())
        editor.putString("iconID", myNewToken.iconID.toString())
        editor.putString("full_name", myNewToken.full_name)
        editor.putString("token_type", myNewToken.token_type)
        editor.putString("access_token", myNewToken.access_token)
        editor.putString("token_limit_date", Calendar.getInstance().time.time.toString())
        editor.commit()
        return myNewToken
    } else {
        return null
    }
}

fun Context.hideKeyBoard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getFromGalery(): Intent {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    return intent
}

fun getFromCamera(ac: Activity): Intent? {
    val permissionStatus = ContextCompat.checkSelfPermission(
            ac,
            android.Manifest.permission.CAMERA
    )
    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        return intent
    } else {
        ActivityCompat.requestPermissions(
                ac,
                arrayOf(android.Manifest.permission.CAMERA),
                101
        );
    }
    return null
}

fun getCategories (): Array<String> {
    val res = URL(apiCurURL + "/categories/").getText()
    val data = Gson().fromJson(res, Array<MyCategories>::class.java).asList()
    return Array<String>(data.size) { i -> data[i].fname }
}

fun cropBitmap (bitmap: Bitmap): Bitmap {
    val x = bitmap.width
    val y = bitmap.height
    val lenMin = if (x > y) y else x
    val xbeg = x/2 - lenMin/2
    val ybeg = y/2 - lenMin/2
    return Bitmap.createBitmap(bitmap, xbeg, ybeg, lenMin, lenMin)
}

fun resizeBitmap (maxSize: Int, bitmap: Bitmap): Bitmap {
    val cropedImg = cropBitmap(bitmap)
    var x = cropedImg.width
    var y = cropedImg.height
    if (x < y) {
        x = maxSize
        y = maxSize * y/x
    } else {
        x = maxSize * x/y
        y = maxSize
    }
    return Bitmap.createScaledBitmap(cropedImg, x, y, true)
}

fun getImageFromURL(context: Context, f_icons: String): ImageRequest {
    return ImageRequest.Builder(context)
        .data(apiCurURL + "/icon/?fkey=" + f_icons)
        .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
        .build()
}

fun setImageImageView (context: Context, f_icons: String, imgView: ImageView) {
    GlobalScope.async {
        val request = ImageRequest.Builder(context)
            .data(apiCurURL + "/icon/?fkey=" + f_icons)
            .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
            .build()
        val resul = imageLoader!!.execute(request).drawable
        imgView.load(resul)
    }
}