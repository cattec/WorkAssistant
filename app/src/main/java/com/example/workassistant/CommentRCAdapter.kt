package com.example.workassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.getstream.sdk.chat.ImageLoader.load
import coil.load


class CommentRCAdapter(private val token_type: String,
                       private val access_token: String,
                       private val apiURL:String ,
                       private val CadrParm: List<MyComment>) :
    RecyclerView.Adapter<CommentRCAdapter.MyViewHolder111>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder111 {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.comment_card,
            parent,
            false
        )
        return MyViewHolder111(itemView)
    }

    //@InternalStreamChatApi
    override fun onBindViewHolder(holder: MyViewHolder111, position: Int) {
        holder.persIcon_view?.load(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons.toInt()) { addHeader("Authorization", token_type + ' ' + access_token) }
        holder.persName_view?.text = CadrParm[position].fname
        holder.tvMessageBody?.text = CadrParm[position].fbody
        holder.persMesDate_view?.text = CadrParm[position].fdatecreate
    }

    class MyViewHolder111(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var vCards_view: CardView? = null
        var persIcon_view: ImageView? = null
        var persName_view: TextView? = null
        var persMesDate_view: TextView? = null
        var tvMessageBody: TextView? = null

        init {
            //vCards_view = itemView?.findViewById(R.id.vCards)
            persIcon_view = itemView?.findViewById(R.id.persIcon)
            persName_view = itemView?.findViewById(R.id.persName)
            persMesDate_view = itemView?.findViewById(R.id.persMesDate)
            tvMessageBody = itemView?.findViewById(R.id.tvMessageBody)
        }
    }

}