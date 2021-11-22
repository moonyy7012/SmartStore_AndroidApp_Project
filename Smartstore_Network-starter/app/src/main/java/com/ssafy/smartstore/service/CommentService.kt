package com.ssafy.smartstore.service


import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CommentService_싸피"
class CommentService {

    fun addComment(comment: Comment, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.insert(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun updateComment(comment: Comment, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.update(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun removeComment(id: Int, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.delete(id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.isSuccessful) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }
}