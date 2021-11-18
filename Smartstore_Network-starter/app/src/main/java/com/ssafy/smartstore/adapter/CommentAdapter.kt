package com.ssafy.smartstore.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse


private const val TAG = "CommentAdapter_싸피"
class CommentAdapter(var list:List<MenuDetailWithCommentResponse> ) :RecyclerView.Adapter<CommentAdapter.CommentHolder>(){

    lateinit var onItemClickListener: OnItemClickListener

    var userId = ""
    var update: Boolean = false

    @JvmName("setUserId1")
    fun setUserId(uId: String) {
        this.userId = uId
    }

    interface OnItemClickListener {
        fun onEditClick(view: View, position: Int, commentId: Int)
        fun onRemoveClick(view: View, position: Int, commentId: Int)
        fun onSaveClick(view: View, position: Int, commentId: Int)
        fun onCancelClick(view: View, position: Int, commentId: Int)
    }

    fun setItems(items: List<MenuDetailWithCommentResponse>) {
        this.list = items
    }

    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvNoticeContent: TextView = itemView.findViewById(R.id.textNoticeContent)
        val etCommentContent: EditText = itemView.findViewById(R.id.et_comment_content)
        val imgAccept: ImageView = itemView.findViewById(R.id.iv_modify_accept_comment)
        val imgCancel: ImageView = itemView.findViewById(R.id.iv_modify_cancel_comment)
        val imgComment: ImageView = itemView.findViewById(R.id.iv_modify_comment)
        val imgDelete: ImageView = itemView.findViewById(R.id.iv_delete_comment)

        fun bindInfo(data :MenuDetailWithCommentResponse){
            etCommentContent.visibility = View.GONE
            Log.d(TAG, "bindInfo: $data")
            tvNoticeContent.text = data.commentContent

            if (data.userId != userId) {
                imgComment.visibility = View.GONE
                imgDelete.visibility = View.GONE
                imgAccept.visibility = View.GONE
                imgCancel.visibility = View.GONE
            } else {
                if (update == false) {
                    imgComment.visibility = View.VISIBLE
                    imgDelete.visibility = View.VISIBLE
                    imgAccept.visibility = View.GONE
                    imgCancel.visibility = View.GONE
                } else {
                    imgComment.visibility = View.GONE
                    imgDelete.visibility = View.GONE
                    imgAccept.visibility = View.VISIBLE
                    imgCancel.visibility = View.VISIBLE
                }
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
            // 수정
            imgComment.setOnClickListener {
                imgComment.visibility = View.GONE
                imgDelete.visibility = View.GONE
                imgAccept.visibility = View.VISIBLE
                imgCancel.visibility = View.VISIBLE
                update = true
                onItemClickListener.onEditClick(it, position, list[position].commentId)
            }

            // 삭제
            imgDelete.setOnClickListener {
                update = false
                onItemClickListener.onRemoveClick(it, position, list[position].commentId)
            }

            // 수정완료
            imgAccept.setOnClickListener {
                imgComment.visibility = View.VISIBLE
                imgDelete.visibility = View.VISIBLE
                imgAccept.visibility = View.GONE
                imgCancel.visibility = View.GONE
                update = false
                onItemClickListener.onSaveClick(it, position, list[position].commentId)
            }

            // 취소
            imgCancel.setOnClickListener {
                imgComment.visibility = View.VISIBLE
                imgDelete.visibility = View.VISIBLE
                imgAccept.visibility = View.GONE
                imgCancel.visibility = View.GONE
                update = false
                onItemClickListener.onCancelClick(it, position, list[position].commentId)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

