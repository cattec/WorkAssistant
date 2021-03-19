package com.example.workassistant

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.transform.CircleCropTransformation
import com.getstream.sdk.chat.ImageLoader.load
import io.getstream.chat.android.core.internal.InternalStreamChatApi


class CommentRCAdapter(private val apiURL:String , private val CadrParm: List<MyComment>) :
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

    @InternalStreamChatApi
    override fun onBindViewHolder(holder: MyViewHolder111, position: Int) {
        holder.persIcon_view?.load(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons.toInt())
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