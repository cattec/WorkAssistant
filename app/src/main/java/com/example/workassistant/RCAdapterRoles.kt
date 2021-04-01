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
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL


class RCAdapterRoles (
        private val imageLoader: ImageLoader,
        private val token_type: String,
        private val access_token: String,
        private val apiURL:String,
        private val CadrParm: List<MyRole>) :
        RecyclerView.Adapter<RCAdapterRoles.MyViewHolderRole>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderRole {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.item_role,
                parent,
                false
        )
        return MyViewHolderRole(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolderRole, position: Int) {

        val request = ImageRequest.Builder(holder.parent_view!!)
                .data(apiURL + "/icon/?fkey=" + 10)
                .addHeader("Authorization", token_type + ' ' + access_token)
                .build()

        GlobalScope.async {
            val resul = imageLoader.execute(request).drawable
            holder.userIcon_view?.load(resul)
        }

        holder.roleName_view?.text = CadrParm[position].fname
        holder.roleUserCount_view?.text = CadrParm[position].usercount.toString()
        holder.roleDescription_view?.text = CadrParm[position].fdescription

        holder.ibRoleOpen_view?.setOnClickListener {
            if (holder.layoutUsers_view?.visibility == View.VISIBLE) {
                holder.ibRoleOpen_view?.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                holder.layoutUsers_view?.visibility = View.GONE
            } else {
                holder.ibRoleOpen_view?.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                holder.layoutUsers_view?.visibility = View.VISIBLE
                holder.rvUsers_view?.layoutManager = LinearLayoutManager(holder.parent_view)
                holder.rvUsers_view?.adapter = refreshAdapter(CadrParm[position].fkey.toString())
            }
        }

    }

    private fun refreshAdapter(fkey: String): RCAdapterUsers {
        return RCAdapterUsers(imageLoader, token_type, access_token, apiURL, fillUsersInRole(fkey))
    }

    private fun fillUsersInRole(fkey: String): List<MyUser> {
        val res = URL(apiURL + "/roles/role_users/?fkey=" + fkey).getText(token_type,access_token)
        return Gson().fromJson(res, Array<MyUser>::class.java).asList()
    }

    class MyViewHolderRole(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userIcon_view: ImageView? = null
        var roleName_view: TextView? = null
        var roleUserCount_view: TextView? = null
        var roleDescription_view: TextView? = null
        var parent_view: Context? = null
        var ibRoleOpen_view: ImageButton? = null
        var layoutUsers_view: LinearLayout? = null
        var rvUsers_view: RecyclerView? = null

        init {
            userIcon_view = itemView?.findViewById(R.id.userIcon)
            roleName_view = itemView?.findViewById(R.id.roleName)
            roleUserCount_view = itemView?.findViewById(R.id.roleUserCount)
            roleDescription_view = itemView?.findViewById(R.id.roleDescription)
            parent_view = itemView?.context
            ibRoleOpen_view = itemView?.findViewById(R.id.ibRoleOpen)
            layoutUsers_view = itemView?.findViewById(R.id.layoutUsers)
            rvUsers_view = itemView?.findViewById(R.id.rvUsers)
        }
    }

}