package com.ssafy.smartstore.api

import com.ssafy.smartstore.dto.Coupon
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface CouponApi {
    @GET("rest/coupon/{userId}")
    fun getCouponsByUser(@Path("userId") userId: String): Call<List<Coupon>>

    @GET("rest/coupon/used/{userId}")
    fun getUsedCouponsByUser(@Path("userId") userId: String): Call<List<Coupon>>

    @GET("rest/coupon/use/coupon/{id}")
    fun getCoupon(@Path("id") id: Int): Call<Coupon>

    @PUT("rest/coupon/{id}")
    fun updateCouponUseState(@Path("id") id: Int): Call<Boolean>
}