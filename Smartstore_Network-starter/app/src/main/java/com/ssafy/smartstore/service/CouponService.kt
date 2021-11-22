package com.ssafy.smartstore.service

import com.ssafy.smartstore.dto.Coupon
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouponService {

    fun getCouponList(userId: String, callback: RetrofitCallback<List<Coupon>>) {
        RetrofitUtil.couponService.getCouponsByUser(userId).enqueue(object : Callback<List<Coupon>> {
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getCouponHistory(userId: String, callback: RetrofitCallback<List<Coupon>>) {
        RetrofitUtil.couponService.getUsedCouponsByUser(userId).enqueue(object : Callback<List<Coupon>> {
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun updateCouponUsed(couponId: Int, callback: RetrofitCallback<Unit>) {
        RetrofitUtil.couponService.updateCouponUseState(couponId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(t)
            }

        })
    }
}