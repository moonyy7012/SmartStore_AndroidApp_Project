package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.Notification


class NoticeAdapter :RecyclerView.Adapter<NoticeAdapter.NoticeHolder>(){
    private lateinit var notiList: ArrayList<Notification>
    lateinit var onItemClickListener: OnItemClickListener

    fun setList(items: ArrayList<Notification>){
        this.notiList = items
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    inner class NoticeHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindInfo(data : Notification){
            itemView.findViewById<TextView>(R.id.textNoticeContent).text = data.msg
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notice, parent, false)
        return NoticeHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        holder.apply {
            bindInfo(notiList[position])
            itemView.findViewById<ImageView>(R.id.xBtn).setOnClickListener {
                onItemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int = notiList.size
}


