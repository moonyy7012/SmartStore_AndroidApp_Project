package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.database.FavoriteDto
import com.ssafy.smartstore.util.CommonUtils

private const val TAG = "FavoriteAdapter_싸피"
class FavoriteAdapter(var favoriteList:List<FavoriteDto>) :RecyclerView.Adapter<FavoriteAdapter.MenuHolder>(){

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuName = itemView.findViewById<TextView>(R.id.textMenuNames)
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val menuPrice = itemView.findViewById<TextView>(R.id.textPrice)
        val mFavorite = itemView.findViewById<ImageView>(R.id.list_iv_favorite)
        val mGoOrder = itemView.findViewById<TextView>(R.id.btn_go_order)

        fun bindInfo(favoriteDto: FavoriteDto){
            menuName.text = favoriteDto.name
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${favoriteDto.img}")
                .into(menuImage)

            menuPrice.text = CommonUtils.makeComma(favoriteDto.price)
            mFavorite.isSelected = true

            itemView.setOnClickListener{
                itemClickListener.onClick(it, layoutPosition, favoriteList[layoutPosition].productId)
            }
            mFavorite.setOnClickListener {
                itemClickListener.onSelect(it, layoutPosition, favoriteList[layoutPosition].productId)
            }
            mGoOrder.setOnClickListener {
                itemClickListener.goOrder(it, layoutPosition, favoriteList[layoutPosition])
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_menu, parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply{
            bindInfo(favoriteList[position])
        }
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }


    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, productId:Int)
        fun onSelect(view: View, position: Int, productId: Int)
        fun goOrder(view: View, position: Int, fProduct: FavoriteDto)
    }
    //클릭리스너 선언
    private lateinit var itemClickListener: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

}

