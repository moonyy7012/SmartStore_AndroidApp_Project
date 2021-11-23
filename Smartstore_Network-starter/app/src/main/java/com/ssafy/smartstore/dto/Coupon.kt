package com.ssafy.smartstore.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class Coupon(
    var id: Int,
    @SerializedName("coupon_id") var couponId: Int,
    @SerializedName("user_id") var userId: String,
    var name: String,
    @SerializedName("publish_time") var publishTime: Date,
    var validate: Date,
    @SerializedName("use_time") var useTime: Date,
    @SerializedName("is_used") var isUsed: String,
    var type: String
)
