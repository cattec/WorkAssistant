package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView


class RCAdapterUsersFind (
    val CadrParm: List<MyUser>) :
    RecyclerView.Adapter<RCAdapterUsersFind.MyViewHolderUser>() {

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

        setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.userIcon_view!!)

        holder.UserLogin_view?.visibility = View.GONE
        holder.UserName_view?.text = CadrParm[position].fname
        holder.UserDescription_view?.text = CadrParm[position].fdescription
        holder.btnDeleteUserFromRole_view?.visibility = View.GONE

        holder.userIcon_view?.setOnLongClickListener {
            holder.parent_view?.startActivity(Intent(holder.parent_view, CardUserActivity::class.java).putExtra("apiCurURL", apiCurURL).putExtra("CurUserID", CadrParm[position].fkey))
            true
        }

        holder.layoutInfo_view?.setOnClickListener() {
            if (CadrParm[position].isSelected) {
                holder.layoutInfo_view?.setBackgroundColor(Color.argb(255, 241, 246, 246))
            }
            else {
                holder.layoutInfo_view?.setBackgroundColor(Color.argb(160, 198, 228, 178))
            }
            CadrParm[position].isSelected = !CadrParm[position].isSelected
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