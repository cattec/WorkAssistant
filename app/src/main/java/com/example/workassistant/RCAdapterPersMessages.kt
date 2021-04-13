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

        val youLastMessageRead = wasist_db?.getLastChatMessage(CadrParm[position].f_messages)!!
        if (CadrParm[position].f_messages_last > youLastMessageRead)
        {
            holder.ivNotReaded_view?.visibility = View.VISIBLE
        }

        holder.layoutRoom_view?.setOnClickListener() {
            holder.parent_view?.startActivity(Intent(holder.parent_view, ChatRoomActivity::class.java)
                    .putExtra("f_messages", CadrParm[position].f_messages.toString())
                    .putExtra("roomName", CadrParm[position].fname)
                    .putExtra("youLastMessageRead", youLastMessageRead.toString())
            )
        }

    }

    class MyViewHolderMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var MesIcon_view: ImageView? = itemView.findViewById(R.id.MesIcon)
        var tvMembers_view: TextView? = itemView.findViewById(R.id.tvMembers)
        var tvChatDate_view: TextView? = itemView.findViewById(R.id.tvChatDate)
        var tvChatLastMess_view: TextView? = itemView.findViewById(R.id.tvChatLastMess)
        var parent_view: Context? = itemView.context
        var layoutRoom_view: LinearLayout? = itemView.findViewById(R.id.layoutRoom)
        var ivNotReaded_view: ImageView? = itemView.findViewById(R.id.ivNotReaded)
    }

}