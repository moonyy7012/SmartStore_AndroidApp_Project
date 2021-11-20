package com.ssafy.smartstore.config

import android.Manifest
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.dto.Notification
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.intercepter.AddCookiesInterceptor
import com.ssafy.smartstore.intercepter.ReceivedCookiesInterceptor
import com.ssafy.smartstore.repository.FavoriteRepository
import com.ssafy.smartstore.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ApplicationClass_싸피"
class ApplicationClass : Application() {
    companion object{
        // ipconfig를 통해 ip확인하기
        // 핸드폰으로 접속은 같은 인터넷으로 연결 되어있어야함 (유,무선)
        const val SERVER_URL = "http://192.168.35.165:9999/"
//        const val SERVER_URL = "http://172.30.1.20:9999/"
        const val MENU_IMGS_URL = "${SERVER_URL}imgs/menu/"
        const val GRADE_IMGS_URL = "${SERVER_URL}imgs/grade/"
        const val IMGS_URL = "${SERVER_URL}imgs/"

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        var notiIdx = 0
        var notiList = ArrayList<Notification>()

        var shoppingList = mutableListOf<ShoppingCart>()

        // 최근주문 모드
        var latestMode = 0
        var latestOrderId = -1

        var liveCnt = MutableLiveData<Int>()
        var tableN = ""
        var isNear = false

        // comment button
        const val MODIFY = 1
        const val MODIFY_ACCCEPT = 2
        const val MODIFY_CANCEL = 3
        const val DELETE = 4

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        // 주문 준비 완료 확인 시간 1분
        const val ORDER_COMPLETED_TIME = 60*1000

    }


    override fun onCreate() {
        super.onCreate()

        FavoriteRepository.initialize(this)

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor())
            .addInterceptor(ReceivedCookiesInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS).build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

}