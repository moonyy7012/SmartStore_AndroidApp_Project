package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.util.CommonUtils


class ShoppingListAdapter(val activity: MainActivity) :RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>(){
    var list = activity.shppingListViewModel.shoppingList.value!!

    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.textShoppingMenuName)
        val price: TextView = itemView.findViewById(R.id.textShoppingMenuMoney)
        val count: TextView = itemView.findViewById(R.id.textShoppingMenuCount)
        val totalPrice: TextView = itemView.findViewById(R.id.textShoppingMenuMoneyAll)
        val image: ImageView = itemView.findViewById(R.id.menuImage)
        val btnDelete:ImageView = itemView.findViewById(R.id.delete)

        fun bindInfo(shoppingCart: ShoppingCart){
            name.text = shoppingCart.menuName
            price.text = CommonUtils.makeComma(shoppingCart.menuPrice)
            count.text = "${shoppingCart.menuCnt}개"
            totalPrice.text = CommonUtils.makeComma(shoppingCart.totalPrice)
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${shoppingCart.menuImg}")
                .into(image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shopping_list, parent, false)
        return ShoppingListHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListAdapter.ShoppingListHolder, position: Int) {
        holder.apply{
            bindInfo(list[position])
            btnDelete.setOnClickListener { boardClickListener.onBoardItemClick(itemView, position) }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getTotalCount(): Int {
        var count = 0

        for (item in list) {
            count += item.menuCnt
        }

        return count
    }

    fun getTotalPrice(): Int {
        var sum = 0

        for (item in list) {
            sum += item.totalPrice
        }

        return sum
    }

    /*이벤트 처리를 위한 Listener*/
    //클릭 인터페이스 정의. Activity에서 이 interface 구현체를 만들어서 호출한다.
    interface OnBoardClickListener {
        fun onBoardItemClick(view: View, position: Int)
    }

    //클릭리스너 선언
    lateinit var boardClickListener: OnBoardClickListener

    //클릭리스너 등록 매소드
    fun setOnBoardClickListener(boardClickListener: OnBoardClickListener) {
        this.boardClickListener = boardClickListener
    }
}

