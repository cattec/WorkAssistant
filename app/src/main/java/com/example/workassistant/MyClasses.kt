package com.example.workassistant

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.facebook.common.references.SharedReference
import com.google.gson.Gson
import java.net.URL

class cToken(
        val userID: Int,
        val iconID: Int,
        val full_name: String,
        val access_token: String,
        val token_type: String
)

class MyMessage(
        var fkey: String,
        var fname: String,
        var fdatecreate: String,
        var fbody: String,
        var categ_name: String,
        var f_categories: String,
        var f_icons: String
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

fun getNewToken(settings: SharedPreferences, apiCurURL: String, myLogin: String, myPassword: String): cToken? {

    val tokenResponse = URL(apiCurURL + "/token").getToken(myLogin, myPassword)

    //Если не было ошибки то возвращаем токен
    if (!tokenResponse.contains("you_error_detail")) {
        val myToken = Gson().fromJson(
                '[' + tokenResponse + ']',
                Array<cToken>::class.java
        )[0]

        //После того как был успешный логин сохраняем последние удачные данные
        val editor = settings.edit()
        editor.putString("myLogin", myLogin)
        editor.putString("myPassword", myPassword)
        editor.putString("userID", myToken.userID.toString())
        editor.putString("iconID", myToken.iconID.toString())
        editor.putString("full_name", myToken.full_name)
        editor.putString("token_type", myToken.token_type)
        editor.putString("access_token", myToken.access_token)
        editor.commit()
        return myToken
    } else {
        return null
    }
}

fun Context.hideKeyBoard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}