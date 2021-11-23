package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.CouponAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentCouponBinding
import com.ssafy.smartstore.service.CouponService

private const val TAG = "CouponFragment_싸피"
class CouponFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var couponAdapter = CouponAdapter(emptyList())


    private lateinit var binding: FragmentCouponBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadCoupon(1)

        binding.couponRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = couponAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        couponAdapter.setItemClickListener(object : CouponAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, userCouponId: Int) {
                mainActivity.supportFragmentManager.apply {
                    beginTransaction().remove(this@CouponFragment)
                    popBackStack()
                }
                mainActivity.userCouponId = userCouponId
                mainActivity.openFragment(1)
            }
        })

        binding.toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when(checkedId) {
                    R.id.btn_not_used -> {
                        loadCoupon(1)
                    }
                    R.id.btn_used -> {
                        loadCoupon(2)
                    }
                }
            }
        }
    }

    private fun loadCoupon(choice: Int) {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
        when(choice) {
            1 -> {
                CouponService().getCouponList(userId).observe(viewLifecycleOwner, {
                    couponAdapter.apply {
                        list = it
                        selected = choice
                        notifyDataSetChanged()
                    }
                })
            }
            2 -> {
                CouponService().getCouponHistory(userId).observe(viewLifecycleOwner, {
                    couponAdapter.apply {
                        list = it
                        selected = choice
                        notifyDataSetChanged()
                    }
                })
            }
        }

    }
}