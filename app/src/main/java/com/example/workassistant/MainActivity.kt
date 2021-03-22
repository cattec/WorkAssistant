package com.example.workassistant

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.navigation.NavigationView
import com.google.gson.*
import java.net.URL


class MainActivity : AppCompatActivity() {

    private var NOTIFY_ID: Int = 154
    private val CHANNEL_ID : String= "INNC note"
    val CHANEL_NAME = "INNC Work Assistant"
    val CHANEL_DESC = "Оповещение отметка здоровья"
    val CHANEL_TEXT = "Пожулйста заполните ежедневную предрабочую карточку состояния здоровья"

    val apiURL_heroku: String = "https://wassistant.herokuapp.com"
    val apiURL_local: String = "http://10.226.96.21:5000"
    val apiCurURL: String = apiURL_heroku

    var myToken: cToken = cToken(0, 0, "", "", "")

    //private val TimeReceiver: MyTimeReceiver = MyTimeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            //установка необходимых прав
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            //загрузка токена
            val resultTokenResponse = loadToken()
            if (resultTokenResponse == false) {
                startActivity(Intent(this, LoginActivity::class.java).putExtra("apiCurURL", apiCurURL))
            }

            //обработка собый основной ленты сообщений
            mainListRefresh()

            //События навигационного меню
            navMenuEvents()
        }
        catch (e: Exception)
        {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            etInfoShow(e.toString())
        }
    }

    fun loadToken(): Boolean {

        //Загружаем из настроек последнего залогиненного пользователя
        val settings = getSharedPreferences("UserInfo", 0)
        val userID_str: String = settings.getString("userID", "").toString()
        val iconID_str: String = settings.getString("iconID", "").toString()
        val full_name: String = settings.getString("full_name", "").toString()
        val token_type: String = settings.getString("token_type", "").toString()
        val access_token: String = settings.getString("access_token", "").toString()
        val myLogin: String = settings.getString("myLogin", "").toString()
        val myPassword: String = settings.getString("myPassword", "").toString()

        val userID: Int = if (userID_str == "") 0 else userID_str.toInt()
        val iconID: Int = if (iconID_str == "") 0 else iconID_str.toInt()

        //Проверяем старый токен действует еще или уже нет
        val result = URL(apiCurURL + "/users/me/").checkToken(token_type, access_token)

        //если вернуло ответ то токен еще рабочий
        if ( result != "" ) {
            myToken = cToken(userID, iconID, full_name, access_token, token_type)
            return true
        }

        //токен устарел надо попрбовать сделать новый с реквизитами последнего пользователя
        val newToken = getNewToken(getSharedPreferences("UserInfo", 0), apiCurURL, myLogin, myPassword)

        if(newToken != null) {
            myToken = newToken
            return true
        }

        //Неполучилось значит просим пользователя снова авторизоваться
        return false
    }

    fun etInfoShow(inpStr: String) {
        val etInfo = findViewById<TextView>(R.id.etInfo)
        etInfo.setText(inpStr)
        etInfo.visibility = View.VISIBLE
    }

    fun navMenuEvents() {

        val settings = getSharedPreferences("UserInfo", 0)
        val full_name: String = settings.getString("full_name", "")!!
        val iconID: String = settings.getString("iconID", "").toString()
        val token_type: String = settings.getString("token_type", "").toString()
        val access_token: String = settings.getString("access_token", "").toString()

        val myNav = findViewById<NavigationView>(R.id.nav_view)
        val header = myNav.getHeaderView(0)
        val nav_head_text = header.findViewById<TextView>(R.id.nav_header_textView)
        nav_head_text.setText(full_name)
        val nav_header_imageView = header.findViewById<ImageView>(R.id.nav_header_imageView)
        if (iconID != "") nav_header_imageView.load(apiCurURL + "/icon/?fkey=" + iconID) { addHeader("Authorization", token_type + ' ' + access_token) }

        val navHeader = myNav.getHeaderView(0)
        navHeader.findViewById<LinearLayout>(R.id.head_leyout).setOnClickListener {
            startActivity(Intent(this, UserCardActivity::class.java).putExtra("apiCurURL", apiCurURL))
            //Toast.makeText(this, "Open User Profile Card!", Toast.LENGTH_SHORT).show()
        }

        myNav.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item_three0 -> openCloseNavigationDrawer(findViewById<NavigationView>(R.id.nav_view))
                R.id.nav_item_three1 -> createINNCNotify()
                R.id.nav_item_three2 -> startActivity(
                        Intent(
                                this,
                                ChannelsListActivity::class.java
                        )
                )
                R.id.nav_item_three3 -> startActivity(Intent(this, SupportActivity::class.java))
                R.id.nav_item_three4 -> startActivity(Intent(this, HelpActivity::class.java))
                R.id.nav_item_three5 -> startActivity(
                        Intent(
                                this,
                                SettingsActivity::class.java
                        )
                )
                R.id.nav_item_three_change_user -> startActivity(Intent(this, LoginActivity::class.java).putExtra("apiCurURL", apiCurURL))
            }
            true
        }
    }

    fun mainListRefresh() {
        //реализация обновления списка: толком не работает тормозит дико, потому что генерит милион событий
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = NewsRCAdapter(myToken.userID, myToken.token_type, myToken.access_token, getSharedPreferences("UserInfo", 0), apiCurURL, fillList1(apiCurURL))

        /*
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.onScrollToStart {
            Toast.makeText(this, "Refresh list", Toast.LENGTH_SHORT).show()
            refreshMainList(findViewById<NavigationView>(R.id.nav_view))
        }
        rv.onScrollToEnd {
            Toast.makeText(this, "Message ended", Toast.LENGTH_SHORT).show()
        }*/
    }

/*
    fun RecyclerView.onScrollToStart( onScrollNearStart: (Unit) -> Unit) =
        addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == 1) {
                        val yOffset = computeVerticalScrollOffset()
                        if (yOffset == 0) {

                            val initialField = RecyclerView::class.java.getDeclaredField("mInitialTouchY")
                            val lastField = RecyclerView::class.java.getDeclaredField("mLastTouchY")
                            initialField.isAccessible = true
                            lastField.isAccessible = true
                            val initial = initialField.getInt(recyclerView)
                            val last = lastField.getInt(recyclerView)

                            if (last > initial) {
                                onScrollNearStart(Unit);
                            }
                        }
                    }

                }
            })

    fun RecyclerView.onScrollToEnd(
        onScrollNearEnd: (Unit) -> Unit
    ) = addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!recyclerView.canScrollVertically(1)) {
                onScrollNearEnd(Unit)
            }
        }
    })*/


    fun testURLResponse(view: View) {
        try {
            val res = URL(apiCurURL + "/users/").getText_noToken()
            Toast.makeText(this, res, Toast.LENGTH_LONG).show()
        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun refreshMainList(view: View) {
        try {
            mainListRefresh()
        }
        catch (e: Exception)
        {
            Toast.makeText(this, "Cant refresh main list: " + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun createINNCNotify() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val contentIntent = PendingIntent.getActivity(
                        this,
                        0,
                        Intent(
                                this,
                                MainActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_CANCEL_CURRENT
                )

                val contentIntentYes = PendingIntent.getActivity(
                        this,
                        0,
                        Intent(
                                this,
                                SupportActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_CANCEL_CURRENT
                )

                val contentIntentNo = PendingIntent.getActivity(
                        this,
                        0,
                        Intent(
                                this,
                                HelpActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_CANCEL_CURRENT
                )

                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.arni))
                        .setContentTitle(CHANEL_NAME)
                        .setContentText(CHANEL_TEXT)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setColor(Color.GREEN)
                        .addAction(R.drawable.ic_baseline_message_24, "Support", contentIntentYes)
                        .addAction(R.drawable.ic_baseline_help_outline_24, "Help", contentIntentNo)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(CHANEL_TEXT))

                val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANEL_DESC
                }
                val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(NOTIFY_ID++, builder.build())
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun fillList1(apiURL: String): List<MyMessage>{
        val res = URL(apiURL + "/message_main/").getText(myToken.token_type, myToken.access_token)
        val data = Gson().fromJson(res, Array<MyMessage>::class.java).asList()
        return data
    }

    fun openCloseNavigationDrawer(view: View) {

        this.hideKeyBoard(view)

        val drawer_layout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

}
