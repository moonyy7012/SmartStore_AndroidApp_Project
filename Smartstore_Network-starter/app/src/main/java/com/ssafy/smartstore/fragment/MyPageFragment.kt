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
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.OrderAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentMypageBinding
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.RetrofitCallback

// MyPage 탭
private const val TAG = "MyPageFragment_싸피"
class MyPageFragment : Fragment(){
    private lateinit var orderAdapter : OrderAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var list : List<LatestOrderResponse>

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
        UserService().getInfo(id, GetInfoCallback())
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

    private fun getUserData():String{
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = user.name

        return user.id
    }

    inner class GetInfoCallback: RetrofitCallback<HashMap<String, Any>> {
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: $t")
        }

        override fun onSuccess(code: Int, responseData: HashMap<String, Any>) {

            val grade = responseData["grade"].let {
                val json = JsonParser().parse(it.toString()).asJsonObject
                Gson().fromJson<HashMap<String, Any>>(json.toString(), java.util.HashMap::class.java)
            }

            Glide.with(this@MyPageFragment)
                .load("${ApplicationClass.GRADE_IMGS_URL}${grade["img"]}")
                .error("R.drawable.${grade["img"]}")
                .into(binding.imageLevel)

            binding.textUserLevel.text = String.format(
                resources.getString(R.string.level),
                grade["title"].toString(),
                grade["step"].toString().toDouble().toInt()
            )

            val from = 10 - grade["to"].toString().toDouble().toInt()
            binding.proBarUserLevel.progress = from * 10
            binding.textUserNextLevel.text = String.format(resources.getString(R.string.next_level), from)
            binding.textLevelRest.text = String.format(
                resources.getString(R.string.level_rest),
                grade["to"].toString().toDouble().toInt()
            )
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: $code")
        }

    }
}