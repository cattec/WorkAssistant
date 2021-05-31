package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.net.URL


class RCAdapterUsersRoom (
        private val f_messages: Int,
        val CadrParm: List<MyRoomMembers>) :
        RecyclerView.Adapter<RCAdapterUsersRoom.MyViewHolderUser>() {

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

        setImageImageView(holder.parent_view!!, CadrParm[position].f_icons.toString(), holder.userIcon_view!!)

        holder.UserLogin_view?.visibility = View.GONE
        holder.UserName_view?.text = CadrParm[position].fname
        holder.UserDescription_view?.text = CadrParm[position].fdescription

        holder.userIcon_view?.setOnLongClickListener {
            if (CadrParm[position].f_users > 0)
                holder.parent_view?.startActivity(Intent(holder.parent_view, CardUserActivity::class.java).putExtra("CurUserID", CadrParm[position].f_users))
            else
                holder.parent_view?.startActivity(Intent(holder.parent_view, CardRoleActivity::class.java).putExtra("CurRoleID", CadrParm[position].f_roles))
            true
        }

        holder.btnDeleteUserFromRole_view?.setOnClickListener {
            MaterialAlertDialogBuilder(holder.parent_view!!)
                    .setTitle("Удаление из комнаты")
                    .setMessage("Вы уверены что хотите удалить <"+ CadrParm[position].fname +"> из Комнаты?")
                    .setNegativeButton("Отмена") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("Удалить") { dialog, which ->
                        // Respond to positive button press
                        URL(apiCurURL + "/messages/room/delete/?f_messages=" + f_messages.toString()
                                + "&f_users=" + CadrParm[position].f_users.toString()
                                + "&f_roles=" + CadrParm[position].f_roles.toString()
                        ).getText()
                        holder.userCardView_view?.visibility = View.GONE
                    }
                    .show()
        }

    }

    class MyViewHolderUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIcon_view: ImageView? = itemView.findViewById(R.id.userIcon)
        var UserLogin_view: TextView? = itemView.findViewById(R.id.UserLogin)
        var UserName_view: TextView? = itemView.findViewById(R.id.UserName)
        var UserDescription_view: TextView? = itemView.findViewById(R.id.UserDescription)
        var parent_view: Context? = itemView.context
        var btnDeleteUserFromRole_view: ImageButton? = itemView.findViewById(R.id.btnDeleteUserFromRole)
        var userCardView_view: LinearLayout? = itemView.findViewById(R.id.userCardView)
        var layoutInfo_view: LinearLayout? = itemView.findViewById(R.id.layoutInfo)
    }

}