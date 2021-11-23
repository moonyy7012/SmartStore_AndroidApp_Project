package com.ssafy.smartstore.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.dto.Coupon
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CouponService_싸피"
class CouponService {

    fun getCouponList(userId: String): LiveData<List<Coupon>> {
        val responseLiveData: MutableLiveData<List<Coupon>> = MutableLiveData()

        RetrofitUtil.couponService.getCouponsByUser(userId).enqueue(object : Callback<List<Coupon>> {
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        responseLiveData.value = res
                    }
                    Log.d(TAG, "onResponse: $res")
                } else {
                    responseLiveData.value = emptyList()
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                Log.d(TAG, t.message ?: "사용 가능한 쿠폰 정보 받아오는 중 통신오류")
            }
        })
        return responseLiveData
    }

    fun getCouponHistory(userId: String): LiveData<List<Coupon>> {
        val responseLiveData: MutableLiveData<List<Coupon>> = MutableLiveData()

        RetrofitUtil.couponService.getUsedCouponsByUser(userId).enqueue(object : Callback<List<Coupon>> {
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        responseLiveData.value = res
                    }
                    Log.d(TAG, "onResponse: $res")
                } else {
                    responseLiveData.value = emptyList()
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                Log.d(TAG, t.message ?: "사용한 쿠폰 내역 받아오는 중 통신오류")
            }
        })
        return responseLiveData
    }

    fun getCoupon(id: Int, callback: RetrofitCallback<Coupon>) {
        RetrofitUtil.couponService.getCoupon(id).enqueue(object : Callback<Coupon> {
            override fun onResponse(call: Call<Coupon>, response: Response<Coupon>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Coupon>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun updateCouponUsed(id: Int, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.couponService.updateCouponUseState(id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
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