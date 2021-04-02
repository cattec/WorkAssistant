package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class RCAdapterComment(
    private val imageLoader: ImageLoader,
    private val token_type: String,
    private val access_token: String,
    private val apiURL:String,
    private val CadrParm: List<MyComment>) :
    RecyclerView.Adapter<RCAdapterComment.MyViewHolderComment>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderComment {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_comment,
            parent,
            false
        )
        return MyViewHolderComment(itemView)
    }

    //@InternalStreamChatApi
    override fun onBindViewHolder(holder: MyViewHolderComment, position: Int) {

        /*val request = ImageRequest.Builder(holder.parent_view!!)
            .data(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons)
            .addHeader("Authorization", token_type + ' ' + access_token)
            .target(holder.persIcon_view!!)
            .build()
        val disposable = imageLoader.enqueue(request)*/

        val request = ImageRequest.Builder(holder.parent_view!!)
            .data(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons)
            .addHeader("Authorization", token_type + ' ' + access_token)
            .build()

        GlobalScope.async {
            val resul = imageLoader.execute(request).drawable
            holder.persIcon_view?.load(resul)
        }

        //holder.persIcon_view?.load(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons) { addHeader("Authorization", token_type + ' ' + access_token) }

        holder.persName_view?.text = CadrParm[position].fname
        holder.tvMessageBody?.text = CadrParm[position].fbody
        holder.persMesDate_view?.text = CadrParm[position].fdatecreate

        holder.persIcon_view?.setOnLongClickListener {
            holder.parent_view?.startActivity(Intent(holder.parent_view, UserCardActivity::class.java).putExtra("apiCurURL", apiURL).putExtra("CurUserID", CadrParm[position].f_users_create))
            true
        }

    }

    class MyViewHolderComment(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var vCards_view: CardView? = null
        var persIcon_view: ImageView? = null
        var persName_view: TextView? = null
        var persMesDate_view: TextView? = null
        var tvMessageBody: TextView? = null
        var parent_view: Context? = null

        init {
            //vCards_view = itemView?.findViewById(R.id.vCards)
            persIcon_view = itemView?.findViewById(R.id.persIcon)
            persName_view = itemView?.findViewById(R.id.persName)
            persMesDate_view = itemView?.findViewById(R.id.persMesDate)
            tvMessageBody = itemView?.findViewById(R.id.tvMessageBody)
            parent_view = itemView?.context
        }
    }

}