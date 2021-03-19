package com.example.workassistant

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getstream.sdk.chat.ImageLoader.load
import com.google.gson.Gson
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import java.net.HttpURLConnection
import java.net.URL


class NewsRCAdapter(private val settings: SharedPreferences, private val apiURL:String, private val CadrParm: List<MyMessage>) :
    RecyclerView.Adapter<NewsRCAdapter.MyViewHolder111>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder111 {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.recyclerview_item,
            parent,
            false
        )
        return MyViewHolder111(itemView)
    }

    @InternalStreamChatApi
    override fun onBindViewHolder(holder: MyViewHolder111, position: Int) {

        //holder.leyoutComment_view?.visibility = View.GONE
        //holder.imgCardSmall_view?.visibility = View.GONE

        holder.imgCardSmall_view?.load(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons.toInt())
        holder.imgCard_view?.load(apiURL + "/icon/?fkey=" + CadrParm[position].f_icons.toInt())

        holder.tCard1_view?.text = CadrParm[position].fname
        holder.tCard2_view?.text = CadrParm[position].fbody
        holder.tCard3_view?.text = CadrParm[position].categ_name
        holder.tDate_view?.text = CadrParm[position].fdatecreate

        holder.imgCard_view?.setOnClickListener {
            holder.imgCard_view?.visibility = View.GONE
            //holder.imgCardSmall_view?.visibility = View.VISIBLE
            //holder.imgCardSmall_revert_view?.visibility = View.VISIBLE
            holder.layout_small_image?.visibility = View.VISIBLE
            holder.leyoutComment_view?.visibility = View.VISIBLE
            //load Comments if needed
            holder.rvComments_view?.layoutManager = LinearLayoutManager(holder.parent_view)
            holder.rvComments_view?.adapter = CommentRCAdapter(apiURL, fillComments(apiURL, CadrParm[position].fkey))
        }

        holder.imgCardSmall_view?.setOnClickListener {
            holder.imgCard_view?.visibility = View.VISIBLE
            //holder.imgCardSmall_view?.visibility = View.GONE
            //holder.imgCardSmall_revert_view?.visibility = View.GONE
            holder.layout_small_image?.visibility = View.GONE
            holder.leyoutComment_view?.visibility = View.GONE
        }

        holder.btnSendMessage_view?.setOnClickListener {
            if ((holder.tvComment_text_view?.text != null) and (holder.tvComment_text_view?.text.toString().trim() != "")) {
                //получаем текущего пользоваеля
                val userID: Int = settings.getString("userID", "")!!.toInt()
                val nMess = MyCommentOut(userID, holder.tvComment_text_view?.text.toString(), CadrParm[position].fkey.toInt())
                val outComment = Gson().toJson(nMess)
                val requestResult = URL(apiURL + "/ins_comment/").sendComment(outComment)
                //Toast.makeText(holder.parent_view, requestResult, Toast.LENGTH_LONG).show()
                holder.tvComment_text_view?.text = null
                holder.parent_view?.hideKeyBoard(it)
                holder.rvComments_view?.adapter = CommentRCAdapter(apiURL, fillComments(apiURL, CadrParm[position].fkey))
            }
        }

    }

    private fun fillComments(cur_apiURL:String, fkey:String): List<MyComment> {
        val res = URL(cur_apiURL + "/comments/?f_messages=" + fkey).getText()
        val data = Gson().fromJson(res, Array<MyComment>::class.java).asList()
        return data
    }

    class MyViewHolder111(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vCards_view: CardView? = null
        var imgCard_view: ImageView? = null
        var layout_small_image: FrameLayout? = null
        var imgCardSmall_view: ImageView? = null
        //var imgCardSmall_revert_view: ImageView? = null
        var tCard1_view: TextView? = null
        var tCard2_view: TextView? = null
        var tCard3_view: TextView? = null
        var tDate_view: TextView? = null
        var leyoutComment_view: LinearLayout? = null
        var rvComments_view: RecyclerView? = null
        var parent_view: Context? = null
        var btnSendMessage_view: Button? = null
        var tvComment_text_view: EditText? = null

        init {
            vCards_view = itemView?.findViewById(R.id.vCards)
            imgCard_view = itemView?.findViewById(R.id.imgCard)
            layout_small_image = itemView?.findViewById(R.id.layout_small_image)
            imgCardSmall_view = itemView?.findViewById(R.id.imgCardSmall)
            //imgCardSmall_revert_view = itemView?.findViewById(R.id.imgCardSmall_revert)
            tCard1_view = itemView?.findViewById(R.id.tCard1)
            tCard2_view = itemView?.findViewById(R.id.tCard2)
            tCard3_view = itemView?.findViewById(R.id.tCard3)
            tDate_view = itemView?.findViewById(R.id.tDate)
            leyoutComment_view = itemView?.findViewById(R.id.leyoutComment)
            rvComments_view = itemView?.findViewById(R.id.rvComments)
            parent_view = itemView?.context
            btnSendMessage_view = itemView?.findViewById(R.id.btnSendMessage)
            tvComment_text_view = itemView?.findViewById(R.id.tvComment_text)
        }

    }

}