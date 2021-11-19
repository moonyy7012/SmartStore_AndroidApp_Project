package com.ssafy.smartstore.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.ShoppingListAdapter
import com.ssafy.smartstore.databinding.FragmentShoppingListBinding
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.util.CommonUtils

//장바구니 Fragment
class ShoppingListFragment : Fragment(){
    private lateinit var shoppingListAdapter : ShoppingListAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var btnShop : Button
    private lateinit var btnTakeout : Button
    private lateinit var btnOrder : Button
    private var isShop : Boolean = true
    private lateinit var binding: FragmentShoppingListBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
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

        shoppingListAdapter = ShoppingListAdapter(mainActivity)
        binding.recyclerViewShoppingList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = shoppingListAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        initTotal()
        initListener()
    }

    private fun initTotal() {
        binding.textShoppingCount.text = "총 ${shoppingListAdapter.getTotalCount()}개"
        binding.textShoppingMoney.text = CommonUtils.makeComma(shoppingListAdapter.getTotalPrice())
    }

    private fun initListener() {
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
            mainActivity.shppingListViewModel.shoppingList.observe(viewLifecycleOwner, { list ->
                if (list.isEmpty()) {
                    Toast.makeText(context,"주문할 상품이 없습니다.",Toast.LENGTH_SHORT).show()
                } else {
                    if(isShop) showDialogForOrderInShop()
                    else {
                        //거리가 200이상이라면
                        if(!mainActivity.isNear) showDialogForOrderTakeoutOver200m()
                        else mainActivity.completedOrder()
                    }
                }
            })
        }
        shoppingListAdapter.setOnBoardClickListener(object : ShoppingListAdapter.OnBoardClickListener{
            override fun onBoardItemClick(view: View, position: Int) {
                mainActivity.shppingListViewModel.removeItem(position)
                shoppingListAdapter.notifyDataSetChanged()
                initTotal()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogForOrderInShop() {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when(which) {
                DialogInterface.BUTTON_NEGATIVE -> {
                    mainActivity.readable = false
                }
            }
        }

        AlertDialog.Builder(mainActivity)
            .setTitle("알림")
            .setMessage("Table NFC를 찍어주세요")
            .setNegativeButton("확인", listener)
            .show()

        mainActivity.readable = true
    }

    private fun showDialogForOrderTakeoutOver200m() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("확인") { _, _ ->
            mainActivity.completedOrder()
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
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