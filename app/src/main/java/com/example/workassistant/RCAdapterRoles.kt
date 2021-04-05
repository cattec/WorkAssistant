package com.example.workassistant

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL


class RCAdapterRoles (
        private val imageLoader: ImageLoader,
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

        /*val request = ImageRequest.Builder(holder.parent_view!!)
                .data(apiCurURL + "/icon/?fkey=" + CadrParm[position].f_icons.toString())
                .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
                .build()

        GlobalScope.async {
            val resul = imageLoader.execute(request).drawable
            holder.userIcon_view?.load(resul)
        }*/
        setImageImageView(holder.parent_view!!, CadrParm[position].f_icons.toString(), holder.roleIcon_view!!)

        holder.roleName_view?.text = CadrParm[position].fname
        holder.roleUserCount_view?.text = CadrParm[position].usercount.toString()
        holder.roleDescription_view?.text = CadrParm[position].fdescription
        if (CadrParm[position].fsystem) holder.ivIsSsytem_view?.visibility = View.VISIBLE else holder.ivIsSsytem_view?.visibility = View.GONE

        holder.btnAddUsertoRole_view?.setOnClickListener {
            addUserToRole(holder?.parent_view!!, holder.rvUsers_view!!, holder.roleUserCount_view!!, CadrParm[position].fkey.toString(), CadrParm[position].fname)
            //Toast.makeText(holder.parent_view, "Add users to Role", Toast.LENGTH_LONG).show()
        }

        holder.ibRoleOpen_view?.setOnClickListener {
            if (holder.layoutUsers_view?.visibility == View.VISIBLE) {
                holder.ibRoleOpen_view?.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                holder.layoutUsers_view?.visibility = View.GONE
            } else {
                holder.ibRoleOpen_view?.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                holder.layoutUsers_view?.visibility = View.VISIBLE
                holder.rvUsers_view?.layoutManager = LinearLayoutManager(holder.parent_view)
                holder.rvUsers_view?.adapter = refreshAdapter(holder.roleUserCount_view!!, CadrParm[position].fkey.toString(),CadrParm[position].fname)
            }
        }

        holder.roleIcon_view?.setOnLongClickListener {
            holder.parent_view?.startActivity(Intent(holder.parent_view, CardRoleActivity::class.java).putExtra("CurRoleID", CadrParm[position].fkey.toString()))
            true
        }

    }

    fun addUserToRole(contex: Context, rv: RecyclerView, roleUserCount: TextView, fkey: String, roleName: String) {
        val res = URL(apiCurURL + "/roles/role_users/?in_role=false&f_roles=" + fkey).getText()
        val data = Gson().fromJson(res, Array<MyCategories>::class.java).asList()
        val users = Array<String>(data.size) { i -> "[" + data[i].fkey.toString() + "] " + data[i].fname }
        //val users_checked = BooleanArray(data.size) { i -> false }
        MaterialAlertDialogBuilder(contex)
            .setTitle("Кого добавить к роли <" + roleName + ">")
            .setIcon(R.drawable.arni)
            .setItems(users) { dialog, which ->
                addUserToRoleRequest(fkey, users[which])
                roleUserCount.setText((roleUserCount.text.toString().toInt()+1).toString())
                rv.layoutManager = LinearLayoutManager(contex)
                rv.adapter = refreshAdapter(roleUserCount, fkey, roleName)
            }
            .show()
    }

    private fun addUserToRoleRequest (f_roles: String, userStr: String) {
        val idbeg = userStr.indexOf("[",0,true) + 1
        val idend =  userStr.indexOf("]",0,true)
        var userID: String = userStr.substring(idbeg, idend)
        URL(apiCurURL + "/roles/add_user/?f_roles=" + f_roles + "&f_users=" + userID).getText()
    }

    private fun refreshAdapter(roleUserCount: TextView, fkey: String, fname: String): RCAdapterUsers {
        return RCAdapterUsers(imageLoader, roleUserCount, fkey, fname, fillUsersInRole(fkey))
    }

    private fun fillUsersInRole(fkey: String): List<MyUser> {
        val res = URL(apiCurURL + "/roles/role_users/?in_role=true&f_roles=" + fkey).getText()
        return Gson().fromJson(res, Array<MyUser>::class.java).asList()
    }

    class MyViewHolderRole(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var roleIcon_view: ImageView? = null
        var roleName_view: TextView? = null
        var roleUserCount_view: TextView? = null
        var roleDescription_view: TextView? = null
        var parent_view: Context? = null
        var ibRoleOpen_view: ImageButton? = null
        var layoutUsers_view: LinearLayout? = null
        var rvUsers_view: RecyclerView? = null
        var btnAddUsertoRole_view: Button? = null
        var ivIsSsytem_view: ImageView? = null

        init {
            roleIcon_view = itemView?.findViewById(R.id.userIcon)
            roleName_view = itemView?.findViewById(R.id.roleName)
            roleUserCount_view = itemView?.findViewById(R.id.roleUserCount)
            roleDescription_view = itemView?.findViewById(R.id.roleDescription)
            parent_view = itemView?.context
            ibRoleOpen_view = itemView?.findViewById(R.id.ibRoleOpen)
            layoutUsers_view = itemView?.findViewById(R.id.layoutUsers)
            rvUsers_view = itemView?.findViewById(R.id.rvUsers)
            btnAddUsertoRole_view = itemView?.findViewById(R.id.btnAddUsertoRole)
            ivIsSsytem_view = itemView?.findViewById(R.id.ivIsSsytem)
        }
    }

}