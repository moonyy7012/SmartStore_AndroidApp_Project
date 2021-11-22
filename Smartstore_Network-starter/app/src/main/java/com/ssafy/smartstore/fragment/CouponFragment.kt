package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.ssafy.smartstore.dto.Coupon
import com.ssafy.smartstore.service.CouponService
import com.ssafy.smartstore.util.RetrofitCallback

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

        val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
        CouponService().getCouponList(userId, CouponCallback(1))

        binding.couponRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = couponAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        couponAdapter.setItemClickListener(object : CouponAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, couponId: Int) {
                mainActivity.supportFragmentManager.apply {
                    beginTransaction().remove(this@CouponFragment)
                    popBackStack()
                }
                mainActivity.openFragment(1, "couponId", couponId)
            }
        })

        binding.toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when(checkedId) {
                    R.id.btn_not_used -> {
                        CouponService().getCouponList(userId, CouponCallback(1))
                    }
                    R.id.btn_used -> {
                        CouponService().getCouponHistory(userId, CouponCallback(2))
                    }
                }
            }
        }
    }

    inner class CouponCallback(private val choice: Int) : RetrofitCallback<List<Coupon>> {
        override fun onSuccess(code: Int, couponList: List<Coupon>) {
            Log.d(TAG, "onSuccess: choice: $choice, couponList : $couponList")
            couponAdapter.apply {
                list = couponList
                selected = choice
                notifyDataSetChanged()
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: $t")
            couponAdapter.apply {
                list = emptyList()
                notifyDataSetChanged()
            }
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: $code")
        }

    }
}