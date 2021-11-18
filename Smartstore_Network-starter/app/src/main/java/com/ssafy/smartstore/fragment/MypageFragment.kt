package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.OrderAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentMypageBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.dto.UserLevel
import com.ssafy.smartstore.dto.UserOrderDetail
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.RetrofitCallback
import java.lang.Math.abs

// MyPage 탭
private const val TAG = "MypageFragment_싸피"
class MypageFragment : Fragment(){
    private lateinit var orderAdapter : OrderAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var list : List<LatestOrderResponse>

    // 등급관련
    var levelImgUrl = 0
    private var userData: HashMap<String,Any> = HashMap<String,Any>()

    private lateinit var binding:FragmentMypageBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = getUserData()
        initData(id)
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        UserService().getInfo(id, getInfoCallback())

//        initClass()
        
    }

    private fun initData(id:String){

        val userLastOrderLiveData = OrderService().getLastMonthOrder(id)
        Log.d(TAG, "onViewCreated: ${userLastOrderLiveData.value}")
        userLastOrderLiveData.observe(
            viewLifecycleOwner,
            {
                list = it

                orderAdapter = OrderAdapter(mainActivity, list)
                orderAdapter.setItemClickListener(object : OrderAdapter.ItemClickListener{
                    override fun onClick(view: View, position: Int, orderid:Int) {
                        mainActivity.openFragment(2, "orderId", orderid)
                    }
                })

                binding.recyclerViewOrder.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = orderAdapter
                    //원래의 목록위치로 돌아오게함
                    adapter!!.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                binding.logout.setOnClickListener {
                    mainActivity.openFragment(5)
                }

                Log.d(TAG, "onViewCreated: $it")
            }
        )

    }

    private fun initClass(stamp: Int){
        var className = "seeds"
        if (stamp < 50) {
            levelImgUrl = R.drawable.seeds
            className = "씨앗"
        } else if (stamp < 125) {
            levelImgUrl = R.drawable.flower
            className = "꽃"
        } else if (stamp < 225) {
            levelImgUrl = R.drawable.coffee_fruit
            className = "열매"
        } else if (stamp < 350) {
            levelImgUrl = R.drawable.coffee_beans
            className = "커피콩"
        } else {
            levelImgUrl = R.drawable.coffee_tree
            className = "커피나무"
        }

        Log.d(TAG, "initClass: ${className}, $stamp")
        Glide.with(requireActivity())
            .load(levelImgUrl)
            .into(binding.imageLevel)
    }

    private fun getUserData():String{
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = user.name

        return user.id
    }

    inner class getInfoCallback: RetrofitCallback<HashMap<String, Any>> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: $t")
        }

        override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {
            Log.d(TAG, "onSuccess: $code")
            userData = responseData
            responseData.forEach {
                Log.d(TAG, "${it.key} : ${it.value}")
            }
    
            Log.d(TAG, "USERDATA: ${userData["user"]}")
            
            var user = userData.get("user").toString()
            var userParseData = user.split(",")
            var userStamps = userParseData[3].substring(8).toDouble().toInt()
            Log.d(TAG, "userStamps: ${userParseData[3].substring(8)}")
            Log.d(TAG, "onSuccess: ${userStamps.toDouble().toInt()}")
            initClass(userStamps)
            getGrade(userStamps)


        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: $code")
        }

    }

    fun getGrade(stamp: Int) {
        Log.d(TAG, "getGrade: ${stamp}")
        var gradeLevel = 0
        var gradeName = "씨앗"
        var rank = 1
        var stampLevel = 0

        var splitLevel = (UserLevel.userInfoList.get(0).max - stampLevel)/5

        if (stamp > UserLevel.userInfoList.get(0).max-1){
            stampLevel = UserLevel.userInfoList.get(0).max
            splitLevel = (UserLevel.userInfoList.get(1).max - stampLevel)/5
            gradeLevel = 1
            gradeName = "꽃"
        }
        if (stamp > UserLevel.userInfoList.get(1).max-1) {
            stampLevel = UserLevel.userInfoList.get(1).max
            splitLevel = (UserLevel.userInfoList.get(2).max - stampLevel)/5
            gradeLevel = 2
            gradeName = "열매"
        }
        if (stamp > UserLevel.userInfoList.get(2).max-1) {
            stampLevel = UserLevel.userInfoList.get(2).max
            splitLevel = (UserLevel.userInfoList.get(3).max - stampLevel)/5
            gradeLevel = 3
            gradeName = "커피콩"
        }
        if (stamp > UserLevel.userInfoList.get(3).max-1) {
            stampLevel = UserLevel.userInfoList.get(3).max
            splitLevel = (UserLevel.userInfoList.get(4).max - stampLevel)/5
            gradeLevel = 4
            gradeName = "커피나무"
        }

        Log.d(TAG, "getGrade: $stamp, $splitLevel, $stampLevel")
        if (gradeLevel != 4) {
            if (stamp < splitLevel + stampLevel) {
                binding.textUserNextLevel.text = "${ abs((splitLevel + stampLevel - stamp) - splitLevel)}/${splitLevel}"
                binding.textLevelRest.text = "다음 레벨까지 ${splitLevel + stampLevel - stamp}잔 남았습니다."
                rank = 1
            }
            else if (splitLevel <= stamp + stampLevel && stamp < splitLevel*2 + stampLevel) {
                binding.textUserNextLevel.text = "${ abs((splitLevel*2 + stampLevel - stamp) - splitLevel)}/${splitLevel}"
                binding.textLevelRest.text = "다음 레벨까지 ${splitLevel*2 + stampLevel - stamp}잔 남았습니다."
                rank = 2
            }
            else if (splitLevel*2 <= stamp + stampLevel && stamp < splitLevel*3 + stampLevel) {
                binding.textUserNextLevel.text = "${ abs((splitLevel*3 + stampLevel - stamp) - splitLevel)}/${splitLevel}"
                binding.textLevelRest.text = "다음 레벨까지 ${splitLevel*3 + stampLevel - stamp}잔 남았습니다."
                rank = 3
            }
            else if (splitLevel*3 <= stamp + stampLevel && stamp < splitLevel*4 + stampLevel) {
                binding.textUserNextLevel.text = "${ abs((splitLevel*4 + stampLevel - stamp) - splitLevel)}/${splitLevel}"
                binding.textLevelRest.text = "다음 레벨까지 ${splitLevel*4 + stampLevel - stamp}잔 남았습니다."
                rank = 4
            }
            else if (splitLevel*4 <= stamp + stampLevel && stamp < splitLevel*5 + stampLevel) {
                binding.textUserNextLevel.text = "${ abs((splitLevel*5 + stampLevel - stamp) - splitLevel)}/${splitLevel}"
                binding.textLevelRest.text = "다음 레벨까지 ${splitLevel*5 + stampLevel - stamp}잔 남았습니다."
                rank = 5
            }
        }
        binding.textUserLevel.text = "${gradeName} ${rank}단계"

        if (gradeLevel == 4) {
            binding.textUserNextLevel.text = ""
            binding.textLevelRest.text = ""
            binding.textUserLevel.text = "커피나무"
        }
    }


}