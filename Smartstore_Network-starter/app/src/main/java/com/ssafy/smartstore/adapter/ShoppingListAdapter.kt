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
import com.ssafy.smartstore.config.ApplicationClass.Companion.shoppingList
import com.ssafy.smartstore.dto.ShoppingCart


class ShoppingListAdapter :RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>(){

    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var name: TextView = itemView.findViewById(R.id.textShoppingMenuName)
        var price: TextView = itemView.findViewById(R.id.textShoppingMenuMoney)
        var count: TextView = itemView.findViewById(R.id.textShoppingMenuCount)
        var totalprice: TextView = itemView.findViewById(R.id.textShoppingMenuMoneyAll)
        var image: ImageView = itemView.findViewById(R.id.menuImage)
        var btnDelete:ImageView = itemView.findViewById(R.id.delete)


        fun bindInfo(shoppingCart: ShoppingCart){
            name.text = shoppingCart.menuName
            price.text = shoppingCart.menuPrice.toString()
            count.text = shoppingCart.menuCnt.toString()
            totalprice.text = shoppingCart.totalPrice.toString()
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
            bindInfo(shoppingList.get(position))
            itemView.findViewById<ImageView>(R.id.delete).setOnClickListener {
                boardClickListener.onBoardItemClick(it, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return shoppingList.size
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

