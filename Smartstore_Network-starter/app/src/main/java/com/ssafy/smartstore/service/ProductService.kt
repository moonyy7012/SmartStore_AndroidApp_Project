package com.ssafy.smartstore.service

import android.content.Context
import android.util.Log
import android.view.Menu
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.fragment.OrderFragment
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "UserService_싸피"
class ProductService {

    fun getProductList(callback: RetrofitCallback<List<Product>>)  {
        val menuInfoRequest: Call<List<Product>> = RetrofitUtil.productService.getProductList()
        menuInfoRequest.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getProductWithComments(productId: Int, callback: RetrofitCallback<List<MenuDetailWithCommentResponse>>) {
        val menuInfoRequest: Call<List<MenuDetailWithCommentResponse>> = RetrofitUtil.productService.getProductWithComments(productId)

        menuInfoRequest.enqueue(object : Callback<List<MenuDetailWithCommentResponse>> {
            override fun onResponse(call: Call<List<MenuDetailWithCommentResponse>>, response: Response<List<MenuDetailWithCommentResponse>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<MenuDetailWithCommentResponse>>, t: Throwable) {
                callback.onError(t)
            }
        })

    }

//    fun getProductWithComments(productId: Int): MutableLiveData<List<MenuDetailWithCommentResponse>> {
//        val responseLiveData: MutableLiveData<List<MenuDetailWithCommentResponse>> = MutableLiveData()
//        val menuInfoRequest: Call<List<MenuDetailWithCommentResponse>> = RetrofitUtil.productService.getProductWithComments(productId)
//
//        menuInfoRequest.enqueue(object : Callback<List<MenuDetailWithCommentResponse>> {
//            override fun onResponse(call: Call<List<MenuDetailWithCommentResponse>>, response: Response<List<MenuDetailWithCommentResponse>>) {
//                val res = response.body()
//                if(response.code() == 200){
//                    if (res != null) {
//                        responseLiveData.value = res
//                    }
//                } else {
//                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<MenuDetailWithCommentResponse>>, t: Throwable) {
//                Log.d(TAG, "onFailure 통신오류 - $t")
//            }
//        })
//
//        return responseLiveData
//    }
}