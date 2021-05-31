package com.example.workassistant

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import coil.load
import coil.request.ImageRequest
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class cToken(
    val userID: Int,
    val iconID: Int,
    val full_name: String,
    val access_token: String,
    val token_type: String,
    val roles: String
)

class MyCategories(
    var fkey: Int,
    var fname: String
)

class MyRole(
    var fkey: Int,
    var fname: String,
    var fdescription: String,
    var usercount: Int,
    var f_icons: Int,
    val fsystem: Boolean
)

class MyMessage(
    var fkey: String,
    var fname: String,
    var fdatecreate: String,
    var fbody: String,
    var categ_name: String,
    var f_categories: String,
    var f_icons: String,
    var f_users_create: String,
    var canedit: Boolean
)

class MyPersMessage(
    var f_messages: Int,
    var f_icons: Int,
    var fname: String,
    var chat_type: String,
    var lastmessage: String,
    var fdatecreate: String,
    var f_messages_last: Int
)

class MyComment(
    var fkey: String,
    var f_users_create: String,
    var fname: String,
    var fdatecreate: String,
    var fbody: String,
    var f_icons: String
)

class MyUserShort(
    var fkey: String,
    var fname: String,
    var f_icons: String
)

class MyUser(
    var fkey: String,
    var flogin: String,
    var fname: String,
    var fdisable: String,
    var fdescription: String,
    var femail: String,
    var f_icons: String,
    var isSelected: Boolean
)

class MyCommentOut(
    val f_users_create: Int,
    val fbody: String,
    val f_messages: Int
)

class myUserUpdate(
    val fkey: Int,
    val fname: String,
    val femail: String,
    val fdescription: String,
    val fpass: String
)

class myRoleUpdate(
    val fkey: Int,
    val fname: String,
    val fdescription: String,
    val fsystem: Boolean
)

class MyUpdateIcon(
    var f_icons: Int,
    var f_tableKey: Int,
    var fdata: String
)

class MyRoomMembers(
        var fkey: String,
        var f_users: Int,
        var f_roles: Int,
        var fname: String,
        var f_icons: Int,
        var fdescription: String
)

fun getNewToken(settings: SharedPreferences, myLogin: String, myPassword: String): cToken? {

    val tokenResponse = URL(apiCurURL + "/token").getToken(myLogin, myPassword)

    //Если не было ошибки то возвращаем токен
    if (!tokenResponse.contains("you_error_detail")) {
        val myNewToken = Gson().fromJson(
            '[' + tokenResponse + ']',
            Array<cToken>::class.java
        )[0]

        //После того как был успешный логин сохраняем последние удачные данные
        val editor = settings.edit()
        editor.putString("myLogin", myLogin)
        editor.putString("myPassword", myPassword)
        editor.putString("userID", myNewToken.userID.toString())
        editor.putString("iconID", myNewToken.iconID.toString())
        editor.putString("full_name", myNewToken.full_name)
        editor.putString("token_type", myNewToken.token_type)
        editor.putString("access_token", myNewToken.access_token)
        editor.putString("roles", myNewToken.roles)
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

fun getCategories (spec: String): Array<String> {
    val res = URL(apiCurURL + "/categories/?spec=" + spec).getText()
    val data = Gson().fromJson(res, Array<MyCategories>::class.java).asList()
    return Array<String>(data.size) { i -> data[i].fname }
}

fun cropBitmap(bitmap: Bitmap): Bitmap {
    val x = bitmap.width
    val y = bitmap.height
    val lenMin = if (x > y) y else x
    val xbeg = x/2 - lenMin/2
    val ybeg = y/2 - lenMin/2
    return Bitmap.createBitmap(bitmap, xbeg, ybeg, lenMin, lenMin)
}

fun resizeBitmap(maxSize: Int, bitmap: Bitmap): Bitmap {
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

fun setImageImageView(context: Context, f_icons: String, imgView: ImageView) {
    GlobalScope.async {

        val icoResult = wasist_db?.readIcon(f_icons.toInt())
        if (icoResult != null) {
            //load image from LOCAL DB
            val img: Bitmap = BitmapFactory.decodeByteArray(icoResult, 0, icoResult.size)
            imgView.load(img)
        } else {
            //load from GLOBAL BD
            val request = ImageRequest.Builder(context)
                .data(apiCurURL + "/icon/?fkey=" + f_icons)
                .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
                .build()
            val result = imageLoader!!.execute(request).drawable
            imgView.load(result)

            //load image to LOCAL DB
            val bitmap = (result as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData: ByteArray = stream.toByteArray()
            wasist_db?.insertIcon(f_icons.toInt(), bitMapData)
        }

    }
}

fun setImageImageView_old(context: Context, f_icons: String, imgView: ImageView) {
    GlobalScope.async {
        val request = ImageRequest.Builder(context)
            .data(apiCurURL + "/icon/?fkey=" + f_icons)
            .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
            .build()
        val resul = imageLoader!!.execute(request).drawable
        imgView.load(resul)
    }
}

fun setImageImageView_working_memcach(context: Context, f_icons: String, imgView: ImageView) {
    GlobalScope.async {
        val request = ImageRequest.Builder(context)
            .data(apiCurURL + "/icon/?fkey=" + f_icons)
            .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
            .target(imgView)
            .crossfade(true)
            .build()
        imageLoader!!.enqueue(request)
    }
}

fun now(): String {
    return SimpleDateFormat("HH:mm dd.MM.yyyy").format(Calendar.getInstance().time)
}

fun isReallyOnline(): Boolean {
    val runtime = Runtime.getRuntime()
    try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        return exitValue == 0
    } catch (e: Exception) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    return false
}

class newWebViewWithToken : WebViewClient() {
    // Handle API until level 21
    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        return getNewResponse(url)
    }
    // Handle API 21+
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()
        return getNewResponse(url)
    }
    private fun getNewResponse(url: String): WebResourceResponse? {
        return try {
            val httpClient = OkHttpClient()
            val request: Request = Request.Builder()
                .url(url.trim { it <= ' ' })
                .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
                .build()
            val response: Response = httpClient.newCall(request).execute()
            WebResourceResponse(
                null,
                response.header("content-encoding", "utf-8"),
                response.body?.byteStream()
            )
        } catch (e: Exception) {
            null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun insertIcon(tbl: String, objID: Int, bitmap: Bitmap): Int {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)
    val image = stream.toByteArray()
    val base64Encoded: String = Base64.getEncoder().encodeToString(image)
    val nUserUpdateIcon = MyUpdateIcon(0, objID, base64Encoded)
    val outResponse = Gson().toJson(nUserUpdateIcon)
    val requestResult = URL(apiCurURL + "/icons/updateObjectIcon/?ftable=" + tbl).sendJSONRequest(outResponse)
    if ((requestResult != "") and (requestResult.isDigitsOnly())) return requestResult.toInt()
    return 0
}

fun isRole(roleID: String): Boolean {
    if ((myToken.roles.split(',').find { it == roleID })!!.length > 0) return true
    return false
}