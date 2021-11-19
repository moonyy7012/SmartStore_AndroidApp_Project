package com.ssafy.smartstore.service

import android.util.Log
import android.widget.Toast
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil.Companion.commentService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST

private const val TAG = "CommentService_싸피"
class CommentService {

    fun addComment(comment: Comment, callback: RetrofitCallback<Comment>) {

        commentService.insert(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                if (response.code() == 200) {
                    callback.onSuccess(response.code(), comment)
                    Log.d(TAG, "onResponse: 코멘트 입력성공")
                } else {
                    callback.onFailure(response.code())
                    Log.d(TAG, "onResponse: ***코멘트 입력실패***")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "통신오류 - ${t}")
                callback.onError(t)
            }
        })
    }

    fun updateComment(comment: Comment) {

        commentService.update(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: 코멘트 수정성공")
                } else {
                    Log.d(TAG, "onResponse: ***코멘트 수정실패***")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "통신오류")
            }
        })
    }

    fun removeComment(id: Int) {

        commentService.delete(id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: 코멘트 삭제성공")
                } else {
                    Log.d(TAG, "onResponse: ***코멘트 삭제실패***")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, t.message ?: "통신오류")
            }

        })
    }
}