package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class RCAdapterUsers (
        private val imageLoader: ImageLoader,
        private val token_type: String,
        private val access_token: String,
        private val apiURL:String,
        private val CadrParm: List<MyUser>) :
        RecyclerView.Adapter<RCAdapterUsers.MyViewHolderUser>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderUser {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.item_user,
                parent,
                false
        )
        return MyViewHolderUser(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolderUser, position: Int) {

        val request = ImageRequest.Builder(holder.parent_view!!)
                .data(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons)
                .addHeader("Authorization", token_type + ' ' + access_token)
                .build()

        GlobalScope.async {
            val resul = imageLoader.execute(request).drawable
            holder.userIcon_view?.load(resul)
        }

        holder.UserLogin_view?.text = CadrParm[position].flogin
        holder.UserName_view?.text = CadrParm[position].fname
        holder.UserDescription_view?.text = CadrParm[position].fdescription

        holder.userIcon_view?.setOnLongClickListener {
            holder.parent_view?.startActivity(Intent(holder.parent_view, UserCardActivity::class.java).putExtra("apiCurURL", apiURL))
            true
        }

    }

    class MyViewHolderUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIcon_view: ImageView? = null
        var UserLogin_view: TextView? = null
        var UserName_view: TextView? = null
        var UserDescription_view: TextView? = null
        var parent_view: Context? = null

        init {
            userIcon_view = itemView?.findViewById(R.id.userIcon)
            UserLogin_view = itemView?.findViewById(R.id.UserLogin)
            UserName_view = itemView?.findViewById(R.id.UserName)
            UserDescription_view = itemView?.findViewById(R.id.UserDescription)
            parent_view = itemView?.context
        }
    }

}