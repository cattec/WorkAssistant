package com.example.workassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.net.URL

class BlogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar9)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle("Мой блог")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish();
        })

        val rvBlog = findViewById<RecyclerView>(R.id.rvBlog)
        rvBlog.layoutManager = LinearLayoutManager(this)
        rvBlog.adapter = RCAdapterNewsMessages(fillBlogList())

        findViewById<ImageButton>(R.id.btnAddBlog).setOnClickListener() {
            startActivity(Intent(this, CardMessageActivity::class.java).putExtra("spec","blog"))
        }

    }

    private fun fillBlogList(): List<MyMessage> {
        var curCategory = "none"
        val res = URL(apiCurURL + "/messages/get/?fkey=0&categ_name=" + curCategory + "&personal=true").getText()
        return Gson().fromJson(res, Array<MyMessage>::class.java).asList()
    }
}
