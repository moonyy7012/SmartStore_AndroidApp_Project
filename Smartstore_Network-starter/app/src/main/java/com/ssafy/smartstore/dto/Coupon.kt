package com.ssafy.smartstore.dto

data class Coupon(
    var id: Int,
    var user_id: String,
    var name: String,
    var publishTime: String,
    var validate: String,
    var isUsed: String,
    var type: String
)
