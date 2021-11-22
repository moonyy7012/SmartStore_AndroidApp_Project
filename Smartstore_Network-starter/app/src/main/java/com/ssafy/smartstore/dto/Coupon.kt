package com.ssafy.smartstore.dto

import java.util.*

data class Coupon(
    var id: Int,
    var user_id: String,
    var name: String,
    var publishTime: Date,
    var validate: Date,
    var useTime: Date,
    var isUsed: String,
    var type: String
)
