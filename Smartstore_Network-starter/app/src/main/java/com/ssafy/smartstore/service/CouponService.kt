package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.Coupon
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouponService {

    fun getCouponList(userId: String, callback: RetrofitCallback<MutableList<Coupon>>) {
        RetrofitUtil.couponService.getCouponsByUser(userId).enqueue(object : Callback<MutableList<Coupon>> {
            override fun onResponse(call: Call<MutableList<Coupon>>, response: Response<MutableList<Coupon>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<MutableList<Coupon>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getCouponHistory(userId: String, callback: RetrofitCallback<MutableList<Coupon>>) {
        RetrofitUtil.couponService.getUsedCouponsByUser(userId).enqueue(object : Callback<MutableList<Coupon>> {
            override fun onResponse(call: Call<MutableList<Coupon>>, response: Response<MutableList<Coupon>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<MutableList<Coupon>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }
}