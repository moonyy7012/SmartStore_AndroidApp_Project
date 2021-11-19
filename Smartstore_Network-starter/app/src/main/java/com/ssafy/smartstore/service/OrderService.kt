package com.ssafy.smartstore.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.config.ApplicationClass.Companion.latestMode
import com.ssafy.smartstore.config.ApplicationClass.Companion.latestOrderId
import com.ssafy.smartstore.config.ApplicationClass.Companion.shoppingList
import com.ssafy.smartstore.dto.*
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "OrderService_싸피"
class OrderService{

    // 주문 상세 내역 가져오는 API
    fun getOrderDetails(orderId: Int): LiveData<List<OrderDetailResponse>> {
        val responseLiveData: MutableLiveData<List<OrderDetailResponse>> = MutableLiveData()
        val orderDetailRequest: Call<List<OrderDetailResponse>> = RetrofitUtil.orderService.getOrderDetail(orderId)

        orderDetailRequest.enqueue(object : Callback<List<OrderDetailResponse>> {
            override fun onResponse(call: Call<List<OrderDetailResponse>>, response: Response<List<OrderDetailResponse>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        responseLiveData.value = res

                        if (latestMode == 1) {
                            shoppingList = mutableListOf<ShoppingCart>()

                            res.forEach {
                                val productId = getProductId(it.productName)
                                val productImg = it.img
                                val productName = it.productName
                                val productCnt = it.quantity
                                val productPrice = it.unitPrice
                                val totalPrice = it.totalPrice
                                val type = it.productType ?: "coffee"

//                                Log.d(TAG, "${productId}")
//                                Log.d(TAG, "${productImg}")
//                                Log.d(TAG, "${productName}")
//                                Log.d(TAG, "${productCnt}")
//                                Log.d(TAG, "${productPrice}")
//                                Log.d(TAG, "${totalPrice}")
//                                Log.d(TAG, "${type}")

                                shoppingList.add(ShoppingCart(productId, productImg, productName, productCnt, productPrice, totalPrice, type))

                                Log.d(TAG, "onResponseLatestMode: ${it}")
                            }
                            latestMode = 0
                        }

                    }
                    Log.d(TAG, "onResponse: $res")
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<OrderDetailResponse>>, t: Throwable) {
                Log.d(TAG, t.message ?: "주문 상세 내역 받아오는 중 통신오류")
            }
        })

        return responseLiveData
    }

    // productId 찾기
    fun getProductId(name: String) : Int {
        if (name.equals("coffee1")) {
            return 1
        } else if (name.equals("coffee2")) {
            return 2
        } else if (name.equals("coffee3")) {
            return 3
        } else if (name.equals("coffee4")) {
            return 4
        } else if (name.equals("coffee5")) {
            return 5
        } else if (name.equals("coffee6")) {
            return 6
        } else if (name.equals("coffee7")) {
            return 7
        } else if (name.equals("coffee8")) {
            return 8
        } else if (name.equals("coffee9")) {
            return 9
        } else if (name.equals("coffee10")) {
            return 10
        } else if (name.equals("tea1")) {
            return 11
        } else if (name.equals("cookie")) {
            return 12
        }

        return 1
    }


    // 최근 한달간 주문내역 가져오는 API
    fun getLastMonthOrder(userId: String): LiveData<List<LatestOrderResponse>> {
        val responseLiveData: MutableLiveData<List<LatestOrderResponse>> = MutableLiveData()
        val latestOrderRequest: Call<List<LatestOrderResponse>> = RetrofitUtil.orderService.getLastMonthOrder(userId)

        latestOrderRequest.enqueue(object : Callback<List<LatestOrderResponse>> {
            override fun onResponse(call: Call<List<LatestOrderResponse>>, response: Response<List<LatestOrderResponse>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        // 가공 필요 orderDate 를 기준으로 정렬, o_img 하나로 축약 필요
                        //orderId를 기준으로 새로운 리스트 만들어서 넘기기
                        responseLiveData.value = makeLatestOrderList(res)
                    }
                    Log.d(TAG, "onResponse: $res")
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LatestOrderResponse>>, t: Throwable) {
                Log.d(TAG, t.message ?: "최근 주문 내역 받아오는 중 통신오류")
            }
        })

        return responseLiveData
    }

    // 최근 주문 목록에서 총가격, 주문 개수 구하여 List로 반환한다.
    // 반환되는 List의 경우 화면에서 보여주는 최근 주문 목록 List이다.
    private fun makeLatestOrderList(latestOrderList: List<LatestOrderResponse>): List<LatestOrderResponse>{
        val hm = HashMap<Int, LatestOrderResponse>()
        latestOrderList.forEach { order ->
            if(hm.containsKey(order.orderId)){
                val tmp = hm[order.orderId]!!
                tmp.orderCnt += order.orderCnt
                tmp.totalPrice  += order.productPrice * order.orderCnt
                hm[order.orderId] = tmp
            } else {
                order.totalPrice = order.productPrice * order.orderCnt
                hm[order.orderId] = order
            }
        }
        val list = ArrayList<LatestOrderResponse>(hm.values)
        list.sortWith { o1, o2 -> o2.orderDate.compareTo(o1.orderDate) }
        if(!list.isEmpty()) latestOrderId=list[0].orderId
        return list
    }
    fun insert(order:Order, callback: RetrofitCallback<Int>)  {
        RetrofitUtil.orderService.makeOrder(order).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

}