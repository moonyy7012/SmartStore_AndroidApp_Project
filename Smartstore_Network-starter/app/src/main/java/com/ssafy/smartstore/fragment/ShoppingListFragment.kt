package com.ssafy.smartstore.fragment

import android.app.AlertDialog
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.activity.MainActivity

import com.ssafy.smartstore.adapter.ShoppingListAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.config.ApplicationClass.Companion.flag
import com.ssafy.smartstore.config.ApplicationClass.Companion.shoppingList
import com.ssafy.smartstore.config.ApplicationClass.Companion.tableN
import com.ssafy.smartstore.databinding.FragmentShoppingListBinding
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.util.RetrofitCallback
import java.util.ArrayList

//장바구니 Fragment
class ShoppingListFragment : Fragment(){
    private lateinit var shoppingListRecyclerView: RecyclerView
    private var shoppingListAdapter : ShoppingListAdapter? = ShoppingListAdapter()
    private lateinit var mainActivity: MainActivity
    private lateinit var btnShop : Button
    private lateinit var btnTakeout : Button
    private lateinit var btnOrder : Button
    private var isShop : Boolean = true
    private var totalprice = 0
    private var totalcnt = 0
    private var orderId = -1
    private lateinit var binding: FragmentShoppingListBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
        arguments?.let {
            orderId = it.getInt("orderId") ?: -1
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShoppingListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnShop = binding.btnShop
        btnOrder = binding.btnOrder
        btnTakeout = binding.btnTakeout
        shoppingListRecyclerView = binding.recyclerViewShoppingList
        shoppingListAdapter = ShoppingListAdapter()
        shoppingListRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            adapter = shoppingListAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        totalcnt=0
        totalprice=0
        for(item in shoppingList){
            totalprice+=item.totalPrice
            totalcnt+=item.menuCnt
        }
        view.findViewById<TextView>(R.id.textShoppingCount).text="총 ${totalcnt}개"
        view.findViewById<TextView>(R.id.textShoppingMoney).text="${totalprice} 원"

        btnShop.setOnClickListener {
            btnShop.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            btnTakeout.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = true
        }
        btnTakeout.setOnClickListener {
            btnTakeout.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            btnShop.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = false
        }
        btnOrder.setOnClickListener {
            if(isShop) showDialogForOrderInShop()
            else {
                //거리가 200이상이라면
                if(true) showDialogForOrderTakeoutOver200m()
            }
        }
        shoppingListAdapter!!.boardClickListener=object : ShoppingListAdapter.OnBoardClickListener{
            override fun onBoardItemClick(view: View, position: Int) {


                totalprice-= shoppingList[position].totalPrice
                totalcnt-=shoppingList[position].menuCnt

                binding.textShoppingCount.setText("총 ${totalcnt}개")
                binding.textShoppingMoney.setText("${totalprice} 원")
                shoppingList.removeAt(position)
                shoppingListAdapter!!.notifyDataSetChanged()


            }
        }


    }



    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }




    private fun showDialogForOrderInShop() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 찍어주세요.\n"
        )
        builder.setCancelable(true)
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel()
        }
        builder.create().show()

        if(flag==true){
            completedOrder()

        }

    }

    private fun showDialogForOrderTakeoutOver200m() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("확인") { _, _ ->
            completedOrder()
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    private fun completedOrder(){
        Log.e("complete", "completedOrder: ", )
        var details = ArrayList<OrderDetail>()
        var quantity = 0
        var totalprice = 0
        var topImg =""
        var topProductName = ""
        for(item in shoppingList){
            if(quantity==0){
                topImg = item.menuImg
                topProductName = item.menuName
            }
            var orderDetail = OrderDetail(item.menuId, item.menuCnt)
            quantity+=item.menuCnt
            totalprice+=item.totalPrice
            details.add(orderDetail)


        }

        var order = Order(-1,ApplicationClass.sharedPreferencesUtil.getUser().id, tableN, System.currentTimeMillis().toString(), "N",details)
        order.totalQnanty=quantity
        order.totalPrice = totalprice
        order.topImg = topImg
        order.topProductName = topProductName
        OrderService().insert(order,OrderCallback())

        Toast.makeText(context,"주문이 완료되었습니다.",Toast.LENGTH_SHORT).show()
        flag=false
        shoppingList.clear()

    }

    inner class OrderCallback: RetrofitCallback<Int> {

        override fun onError(t: Throwable) {
        }

        override fun onFailure(code: Int) {
        }

        override fun onSuccess(code: Int, responseData: Int) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.frame_layout_main, MypageFragment())
//                .commit()
            mainActivity.openFragment(6)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            ShoppingListFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }

}