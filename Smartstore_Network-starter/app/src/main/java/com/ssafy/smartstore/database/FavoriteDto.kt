package com.ssafy.smartstore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.Product

@Entity(tableName = "favorite")
data class FavoriteDto(
    var productId: Int,
    var name: String,
    var type: String,
    var price: Int,
    var img: String,
//    var comments: ArrayList<Comment> = ArrayList()
) {

    @PrimaryKey(autoGenerate = true)
    var ID: Int = 0

//    constructor(): this(0, "","",0,"")
    constructor(product: Product) : this(product.id,product.name, product.type, product.price, product.img)
}