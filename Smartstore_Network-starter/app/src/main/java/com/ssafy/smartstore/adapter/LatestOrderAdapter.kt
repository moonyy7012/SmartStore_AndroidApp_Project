package com.ssafy.smartstore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.util.CommonUtils


class LatestOrderAdapter(val context: Context, val list: List<LatestOrderResponse>) :RecyclerView.Adapter<LatestOrderAdapter.LatestOrderHolder>(){

    inner class LatestOrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)
        val ivReOrder = itemView.findViewById<ImageView>(R.id.btnGoOrder)

        fun bindInfo(data: LatestOrderResponse){
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(menuImage)

            // ?? 외 ~건
            if (data.orderCnt > 1) {
                textMenuNames.text = "${data.productName} 외 ${data.orderCnt-1}건"
            } else {
                textMenuNames.text = data.productName
            }

            textMenuPrice.text = CommonUtils.makeComma(data.totalPrice)
            textMenuDate.text = CommonUtils.getFormattedString(data.orderDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestOrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_latest_order, parent, false)
        return LatestOrderHolder(view)
    }

    override fun onBindViewHolder(holder: LatestOrderHolder, position: Int) {

        holder.apply {
            bindInfo(list[position])
            ivReOrder.setOnClickListener { itemClickListener.onClick(itemView, layoutPosition, list[position].orderId) }
        }
    }

    override fun getItemCount(): Int = list.size

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, orderId: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListener: ItemClickListener

    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

}

