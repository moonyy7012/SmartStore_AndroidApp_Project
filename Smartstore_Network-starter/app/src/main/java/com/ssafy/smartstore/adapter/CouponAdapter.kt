package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.Coupon
import com.ssafy.smartstore.util.CommonUtils

class CouponAdapter(var list: List<Coupon>) : RecyclerView.Adapter<CouponAdapter.CouponHolder>() {
    var selected = 1

    inner class CouponHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCouponName = itemView.findViewById<TextView>(R.id.tv_coupon_name)
        private val tvValidDate = itemView.findViewById<TextView>(R.id.tv_valid_date)
        private val tvDivider = itemView.findViewById<TextView>(R.id.tv_divider)
        private val tvUsedDate = itemView.findViewById<TextView>(R.id.tv_used_date)
        val btnGoUse = itemView.findViewById<Button>(R.id.btn_go_use)

        fun bindInfo(coupon: Coupon) {
            tvCouponName.text = coupon.name
            tvValidDate.text = "${CommonUtils.getFormattedStringByDate(coupon.validate)}까지"
            if (selected == 1) {  // 사용 가능한 쿠폰
                tvDivider.visibility = View.GONE
                tvUsedDate.visibility = View.GONE
                btnGoUse.visibility = View.VISIBLE
            } else {    // 쿠폰 히스토리
                tvDivider.visibility = View.VISIBLE
                tvUsedDate.visibility = View.VISIBLE
                btnGoUse.visibility = View.GONE

                tvUsedDate.text = "${CommonUtils.getFormattedStringByDate(coupon.useTime)}사용"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_coupon, parent, false)
        return CouponHolder(view)
    }

    override fun onBindViewHolder(holder: CouponHolder, position: Int) {
        holder.apply {
            bindInfo(list[position])
            btnGoUse.setOnClickListener {
                itemClickListener.onClick(itemView, layoutPosition, list[position].id)  // Coupon 클래스 아직 미확정 -> 변동 가능성 있음
            }
        }
    }

    override fun getItemCount(): Int = list.size

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, userCouponId: Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListener: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}