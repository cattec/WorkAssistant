package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import top.defaults.drawabletoolbox.setGradientRadius


class RCAdapterComment(
        private val isfull: Boolean,
        private val youLastMessageRead: Int,
        private val CadrParm: ArrayList<MyComment>) :
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

    override fun onBindViewHolder(holder: MyViewHolderComment, position: Int) {

        if (youLastMessageRead > 0)
            if (youLastMessageRead < CadrParm[position].fkey.toInt()) {
                holder.ivNotReaded_view?.visibility = View.VISIBLE
            }

        holder.persName_view?.text = CadrParm[position].fname
        holder.tvMessageBody?.text = CadrParm[position].fbody
        holder.persMesDate_view?.text = CadrParm[position].fdatecreate

        //полная инфа в сообщении или не полная
        if (!isfull) {
            holder.persName_view?.visibility = View.GONE
        }

        //о разному отрисовывать сообщения свои и чужие
        if (CadrParm[position].f_users_create == myToken.userID.toString()) {
            setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.persIcon_viewme!!)
            holder.persIcon_viewme?.setOnLongClickListener {
                holder.parent_view?.startActivity(Intent(holder.parent_view, CardUserActivity::class.java).putExtra("CurUserID", CadrParm[position].f_users_create))
                true
            }
            holder.leyoutText_view?.setBackgroundColor(Color.argb(125,242,255,243))
            holder.persIcon_view?.visibility = View.GONE
            holder.persIcon_viewme?.visibility = View.VISIBLE
            holder.layoutHeadComment_view?.gravity = Gravity.END
            holder.lv1namedate_view?.gravity = Gravity.END
        } else {
            setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.persIcon_view!!)
            holder.leyoutText_view?.setBackgroundColor(Color.argb(125,196,231,255))
            holder.persIcon_view?.setOnLongClickListener {
                holder.parent_view?.startActivity(Intent(holder.parent_view, CardUserActivity::class.java).putExtra("CurUserID", CadrParm[position].f_users_create))
                true
            }
            holder.persIcon_view?.visibility = View.VISIBLE
            holder.persIcon_viewme?.visibility = View.GONE
            holder.layoutHeadComment_view?.gravity = Gravity.START
            holder.lv1namedate_view?.gravity = Gravity.START
        }

    }

    class MyViewHolderComment(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var persIcon_view: ImageView? = itemView.findViewById(R.id.persIcon)
        var persIcon_viewme: ImageView? = itemView.findViewById(R.id.persIconme)
        var persName_view: TextView? = itemView.findViewById(R.id.persName)
        var persMesDate_view: TextView? = itemView.findViewById(R.id.persMesDate)
        var tvMessageBody: TextView? = itemView.findViewById(R.id.tvMessageBody)
        var parent_view: Context? = itemView.context
        var layoutHeadComment_view: LinearLayout? = itemView.findViewById(R.id.layoutHeadComment)
        var layoutUser_view: FrameLayout? = itemView.findViewById(R.id.layoutUser)
        var leyoutText_view: LinearLayout? = itemView.findViewById(R.id.layoutText)
        var ivNotReaded_view: ImageView? = itemView.findViewById(R.id.ivNotReaded)
        var lv1namedate_view: LinearLayout? = itemView.findViewById(R.id.lv1namedate)

    }

}