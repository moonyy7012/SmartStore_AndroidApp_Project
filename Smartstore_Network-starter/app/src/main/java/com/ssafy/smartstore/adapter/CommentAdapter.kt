package com.ssafy.smartstore.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse


private const val TAG = "CommentAdapter_싸피"
class CommentAdapter(var list:List<MenuDetailWithCommentResponse> ) :RecyclerView.Adapter<CommentAdapter.CommentHolder>(){

    interface CommentClickListener {
        fun onClick(holder: CommentHolder, position: Int, button: Int)
    }
    private lateinit var btnClickLister: CommentClickListener

    fun setBtnClickListener(commentClickListener: CommentClickListener) {
        btnClickLister = commentClickListener
    }


    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNickname: TextView = itemView.findViewById(R.id.tv_nickname)
        val tvComment: TextView = itemView.findViewById(R.id.tv_comment_content)
        val etComment: EditText = itemView.findViewById(R.id.et_comment_content)
        val ivModifyAccept: ImageView = itemView.findViewById(R.id.iv_modify_accept_comment)
        val ivModifyCancel: ImageView = itemView.findViewById(R.id.iv_modify_cancel_comment)
        val ivModify: ImageView = itemView.findViewById(R.id.iv_modify_comment)
        val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete_comment)

        fun bindInfo(data :MenuDetailWithCommentResponse) {
            tvNickname.text = data.commentUserName
            tvComment.text = data.commentContent
            etComment.visibility = View.GONE
            ivModifyAccept.visibility = View.GONE
            ivModifyCancel.visibility = View.GONE

            if (data.userId != ApplicationClass.sharedPreferencesUtil.getUser().id) {
                ivModify.visibility = View.GONE
                ivDelete.visibility = View.GONE
            } else {
                ivModify.visibility = View.VISIBLE
                ivDelete.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.apply {
            bindInfo(list[position])

            ivModify.setOnClickListener         { btnClickLister.onClick(this, layoutPosition, ApplicationClass.MODIFY) }
            ivModifyAccept.setOnClickListener   { btnClickLister.onClick(this, layoutPosition, ApplicationClass.MODIFY_ACCCEPT) }
            ivModifyCancel.setOnClickListener   { btnClickLister.onClick(this, layoutPosition, ApplicationClass.MODIFY_CANCEL) }
            ivDelete.setOnClickListener         { btnClickLister.onClick(this, layoutPosition, ApplicationClass.DELETE) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

