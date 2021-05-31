package com.example.workassistant

//import com.getstream.sdk.chat.ImageLoader.load
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.net.URL


class RCAdapterNewsMessages(
    private val CadrParm: List<MyMessage>
) :
    RecyclerView.Adapter<RCAdapterNewsMessages.MyViewHolderMessage>() {

    override fun getItemCount() = CadrParm.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderMessage {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_news_message,
            parent,
            false
        )
        return MyViewHolderMessage(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolderMessage, position: Int) {
        //загрузить картинку
        //val img: Bitmap = BitmapFactory.decodeStream(URL(apiCurURL + "/icon/?fkey=" + CadrParm[position].f_icons.toInt()).getIcon())
        //holder.imgCardSmall_view?.load(img)
        //holder.imgCard_view?.load(img)

        /*val request = ImageRequest.Builder(holder.parent_view!!)
            .data(apiCurURL + "/icon/?fkey=" + CadrParm[position].f_icons)
            .addHeader("Authorization", myToken.token_type + ' ' + myToken.access_token)
            .build()

        GlobalScope.async {
            val resul = imageLoader.execute(request).drawable
            holder.imgCardSmall_view?.load(resul)
            holder.imgCard_view?.load(resul)
        }*/

        setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.imgCard_view!!)

        holder.tCard1_view?.text = CadrParm[position].fname
        holder.tCard2_view?.text = CadrParm[position].fbody
        holder.tCard3_view?.text = CadrParm[position].categ_name
        holder.tDate_view?.text = CadrParm[position].fdatecreate

        holder.imgCard_view?.setOnClickListener {
            holder.leyoutComment_view?.visibility = View.VISIBLE
            holder.imgCardMain_view?.visibility = View.GONE
            holder.layout_small_image?.visibility = View.VISIBLE
            setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.imgCardSmall_view!!)
            //holder.layoutLeft_view?.layoutParams!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
            //load Comments if needed
            holder.rvComments_view?.layoutManager = LinearLayoutManager(holder.parent_view)
            holder.rvComments_view?.adapter = refreshAdapter(CadrParm[position].fkey)
            if (holder.rvComments_view?.childCount!! > 0) holder.tvComment_view?.visibility = View.VISIBLE
                else holder.tvComment_view?.visibility = View.GONE
        }

        holder.imgCard_view?.setOnLongClickListener {
            holder.parent_view?.startActivity(Intent(holder.parent_view, CardMessageActivity::class.java).putExtra("f_messages", CadrParm[position].fkey))
            true
        }

        holder.imgCardSmall_view?.setOnClickListener {
            holder.imgCardMain_view?.visibility = View.VISIBLE
            holder.layout_small_image?.visibility = View.GONE
            holder.leyoutComment_view?.visibility = View.GONE
        }

        holder.iBFullView_view?.setOnClickListener {
            if (holder.imgCardMain_view?.visibility == View.VISIBLE) {
                holder.iBFullView_view?.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                holder.imgCardMain_view?.visibility = View.GONE
                holder.layout_small_image?.visibility = View.VISIBLE
                holder.leyoutComment_view?.visibility = View.VISIBLE
                setImageImageView(holder.parent_view!!, CadrParm[position].f_icons, holder.imgCardSmall_view!!)
                holder.rvComments_view?.layoutManager = LinearLayoutManager(holder.parent_view)
                holder.rvComments_view?.adapter = refreshAdapter(CadrParm[position].fkey)
                if (holder.rvComments_view?.childCount!! > 0) holder.tvComment_view?.visibility = View.VISIBLE
                else holder.tvComment_view?.visibility = View.GONE
            } else {
                holder.iBFullView_view?.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                holder.imgCardMain_view?.visibility = View.VISIBLE
                holder.layout_small_image?.visibility = View.GONE
                holder.leyoutComment_view?.visibility = View.GONE
            }
        }

        holder.btnSendMessage_view?.setOnClickListener {
            if ((holder.tvComment_text_view?.text != null) and (holder.tvComment_text_view?.text.toString().trim() != "")) {
                holder.btnSendMessage_view?.isEnabled = false
                //формируем запрос
                val nMess = MyCommentOut(
                        myToken.userID,
                        holder.tvComment_text_view?.text.toString(),
                        CadrParm[position].fkey.toInt()
                )
                val outComment = Gson().toJson(nMess)
                val requestResult = URL(apiCurURL + "/comments/add/").sendJSONRequest(outComment)
                //Toast.makeText(holder.parent_view, requestResult, Toast.LENGTH_LONG).show()
                holder.tvComment_text_view?.text = null
                holder.parent_view?.hideKeyBoard(it)
                holder.rvComments_view?.adapter = refreshAdapter(CadrParm[position].fkey)
                holder.btnSendMessage_view?.isEnabled = true
            }
        }
    }

    private fun refreshAdapter(fkey: String): RCAdapterComment {
        return RCAdapterComment(true, 0, fillComments(fkey))
    }

    private fun fillComments(fkey: String): ArrayList<MyComment> {
        val res = URL(apiCurURL + "/comments/?f_messages=" + fkey).getText()
        val data = Gson().fromJson(res, Array<MyComment>::class.java).let { intList ->
            ArrayList<MyComment>(intList.size).apply { intList.forEach { add(it) } }
        }
        return data
    }

    class MyViewHolderMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vCards_view: CardView? = null
        var imgCardMain_view: CardView? = itemView.findViewById(R.id.imgCardMain)
        var imgCard_view: ImageView? = null
        var layout_small_image: FrameLayout? = null
        var imgCardSmall_view: ImageView? = null
        var tCard1_view: TextView? = null
        var tCard2_view: TextView? = null
        var tCard3_view: TextView? = null
        var tDate_view: TextView? = null
        var leyoutComment_view: LinearLayout? = null
        var rvComments_view: RecyclerView? = null
        var parent_view: Context? = null
        var btnSendMessage_view: Button? = null
        var tvComment_text_view: EditText? = null
        var tvComment_view: TextView? = null
        var iBFullView_view: ImageButton? = null
        var layoutLeft_view: LinearLayout? = null

        init {
            vCards_view = itemView?.findViewById(R.id.vCards)
            imgCard_view = itemView?.findViewById(R.id.imgCard)
            layout_small_image = itemView?.findViewById(R.id.layout_small_image)
            imgCardSmall_view = itemView?.findViewById(R.id.imgCardSmall)
            tCard1_view = itemView?.findViewById(R.id.tMesName)
            tCard2_view = itemView?.findViewById(R.id.tMesText)
            tCard3_view = itemView?.findViewById(R.id.tMesCateg)
            tDate_view = itemView?.findViewById(R.id.tMesDate)
            leyoutComment_view = itemView?.findViewById(R.id.leyoutComment)
            rvComments_view = itemView?.findViewById(R.id.rvComments)
            parent_view = itemView?.context
            btnSendMessage_view = itemView?.findViewById(R.id.btnSendMessage)
            tvComment_text_view = itemView?.findViewById(R.id.tvComment_text)
            tvComment_view = itemView?.findViewById(R.id.tvComment)
            iBFullView_view = itemView?.findViewById(R.id.iBFullView)
            layoutLeft_view = itemView?.findViewById(R.id.layoutLeft)
        }

    }

}