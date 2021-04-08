package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class RCAdapterPersMessages(
        private val CadrParm: List<MyPersMessage>
) :
        RecyclerView.Adapter<RCAdapterPersMessages.MyViewHolderMessage>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderMessage {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.item_pers_message,
                parent,
                false
        )
        return MyViewHolderMessage(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolderMessage, position: Int) {

        setImageImageView(holder.parent_view!!, CadrParm[position].f_icons.toString(), holder.MesIcon_view!!)

        holder.tvMembers_view?.text = CadrParm[position].fname
        holder.tvChatDate_view?.text = CadrParm[position].fdatecreate
        holder.tvChatLastMess_view?.text = CadrParm[position].lastmessage

        holder.layoutRoom_view?.setOnClickListener() {
            holder.parent_view?.startActivity(Intent(holder.parent_view, ChatRoomActivity::class.java)
                    .putExtra("f_messages", CadrParm[position].f_messages.toString())
                    .putExtra("roomName", CadrParm[position].fname)
            )
        }

    }

    class MyViewHolderMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var MesIcon_view: ImageView? = null
        var tvMembers_view: TextView? = null
        var tvChatDate_view: TextView? = null
        var tvChatLastMess_view: TextView? = null
        var parent_view: Context? = null
        var layoutRoom_view: LinearLayout? = null

        init {
            MesIcon_view = itemView?.findViewById(R.id.MesIcon)
            tvMembers_view = itemView?.findViewById(R.id.tvMembers)
            tvChatDate_view = itemView?.findViewById(R.id.tvChatDate)
            tvChatLastMess_view = itemView?.findViewById(R.id.tvChatLastMess)
            parent_view = itemView?.context
            layoutRoom_view = itemView?.findViewById(R.id.layoutRoom)
        }

    }

}