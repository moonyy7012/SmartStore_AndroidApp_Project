package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.LatestOrderAdapter
import com.ssafy.smartstore.adapter.NoticeAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.config.ApplicationClass.Companion.noticeIdx
import com.ssafy.smartstore.config.ApplicationClass.Companion.noticeList
import com.ssafy.smartstore.databinding.FragmentHomeBinding
import com.ssafy.smartstore.dto.Notification
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.service.OrderService

private const val TAG = " HomeFrag_싸피"
// Home 탭
class HomeFragment : Fragment() {
    // 최근주문 데이터가 있는 userData
    private lateinit var latestOrderAdapter : LatestOrderAdapter
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var mainActivity: MainActivity

    private lateinit var latestList :List<LatestOrderResponse>

    private lateinit var binding:FragmentHomeBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserName()
        initAdapter()

    }



    private fun initAdapter() {
        if (noticeList.size == 0){
            noticeList.add(Notification(noticeIdx++,"싸피벅스에 오신 것을 환영합니다. ${ApplicationClass.sharedPreferencesUtil.getUser().name}님"))
        }

        noticeAdapter = NoticeAdapter()
        noticeAdapter.setList(noticeList)

        binding.recyclerViewNoticeOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noticeAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        noticeAdapter.onItemClickListener = object : NoticeAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                noticeList.removeAt(position)
                noticeAdapter.setList(noticeList)
                noticeAdapter.notifyDataSetChanged()
            }

        }

        val userLastOrderLiveData = OrderService().getLastMonthOrder(ApplicationClass.sharedPreferencesUtil.getUser().id)
        userLastOrderLiveData.observe(viewLifecycleOwner, {
            latestList = it

            latestOrderAdapter = LatestOrderAdapter(mainActivity, latestList)
            latestOrderAdapter.setItemClickListener(object : LatestOrderAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, orderId: Int) {
                    Log.d(TAG, "onClick: $orderId")
                    mainActivity.openFragment(1,"orderId",orderId)
                }
            })
            binding.recyclerViewLatestOrder.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = latestOrderAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })

    }

    private fun initUserName(){
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = "${user.name} 님"

    }

}