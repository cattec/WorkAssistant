package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.net.URL


class RCAdapterUsers (
        private val roleUserCount: TextView,
        private val roleID: String,
        private val roleName: String,
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

        /*val request = ImageRequest.Builder(holder.parent_view!!)
                .data(apiCurURL + "/icon/?fkey=" + CadrParm[position].f_icons)
                .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
                .build()
        GlobalScope.async {
            val resul = imageLoader.execute(request).drawable
            holder.userIcon_view?.load(resul)
        }*/

        setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.userIcon_view!!)


        holder.UserLogin_view?.text = CadrParm[position].flogin
        holder.UserName_view?.text = CadrParm[position].fname
        holder.UserDescription_view?.text = CadrParm[position].fdescription

        holder.userIcon_view?.setOnLongClickListener {
            holder.parent_view?.startActivity(Intent(holder.parent_view, CardUserActivity::class.java).putExtra("CurUserID", CadrParm[position].fkey))
            true
        }

        holder.btnDeleteUserFromRole_view?.setOnClickListener {
            MaterialAlertDialogBuilder(holder.parent_view!!)
                .setTitle("Удаление пользователя из группы")
                .setMessage("Вы уверены что хотите удалить пользователя <"+ CadrParm[position].fname +"> из группы <" + roleName + "> ?")
                .setNegativeButton("Отмена") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("Удалить") { dialog, which ->
                    // Respond to positive button press
                    URL(apiCurURL + "/roles/del_user/?f_roles=" + roleID + "&f_users=" + CadrParm[position].fkey).getText()
                    holder.userCardView_view?.visibility = View.GONE
                    roleUserCount.setText((roleUserCount.text.toString().toInt()-1).toString())
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
    }

}