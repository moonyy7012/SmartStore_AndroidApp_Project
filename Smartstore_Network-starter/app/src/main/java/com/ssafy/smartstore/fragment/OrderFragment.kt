package com.ssafy.smartstore.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.MenuAdapter
import com.ssafy.smartstore.databinding.FragmentOrderBinding
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.service.ProductService
import com.ssafy.smartstore.util.RetrofitCallback
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.ssafy.smartstore.util.CommonUtils


// 하단 주문 탭
private const val TAG = "OrderFragment_싸피"
class OrderFragment : Fragment(){
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var prodList:List<Product>
    private lateinit var filteredList:MutableList<Product>
    private lateinit var binding:FragmentOrderBinding

    private val UPDATE_INTERVAL = 60000 // 1분
    private val FASTEST_UPDATE_INTERVAL = 30000 // 30초

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var mCurrentLocation: Location? = null

    companion object {
        val DEFAULT_LOCATION = Location("").apply {
            latitude = 37.62064003133028
            longitude = 126.9161908472352
        }

        val DEFAULT_LAT_LNG = LatLng(37.62064003133028, 126.9161908472352)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }

        // 위치값 가져오기
        LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }

        // 마지막으로 확인된 위치 정보 가져오기
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        startLocationUpdates()

        initEvent()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun initData(){
        ProductService().getProductList(ProductCallback())
    }

    private fun initEvent() {
        binding.floatingBtn.setOnClickListener{
            //장바구니 이동
            mainActivity.openFragment(1)
        }

        binding.btnMap.setOnClickListener{
            mainActivity.openFragment(4)
        }

        binding.searchMenu.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var searchText = binding.searchMenu.text.toString()
                searchFilter(searchText)
            }

        })
    }

    private fun searchFilter(searchText:String?){
        filteredList= mutableListOf()

        for (i in prodList.indices) {
            if (prodList.get(i).name.toLowerCase()
                    .contains(searchText!!.toLowerCase())
            ) {
                filteredList.add(prodList.get(i))
            }
        }

        menuAdapter.filterList(filteredList)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && mainActivity.checkLocationServicesStatus()) {

            mFusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                mCurrentLocation = locationList[locationList.size - 1]
                val distance = mCurrentLocation!!.distanceTo(DEFAULT_LOCATION)
                Log.d(TAG, "setDistanceToStore: $distance")
                binding.distanceInfo.text = "매장과의 거리는 ${CommonUtils.makeCommaM(distance.toInt())} 입니다."
            }
        }
    }


    inner class ProductCallback: RetrofitCallback<List<Product>> {
        override fun onSuccess( code: Int, productList: List<Product>) {
            productList.let {
                prodList=productList
                menuAdapter = MenuAdapter(productList)
                menuAdapter.setItemClickListener(object : MenuAdapter.ItemClickListener{
                    override fun onClick(view: View, position: Int, productId:Int) {
                        mainActivity.openFragment(3, "productId", productId)
                    }
                })
            }

            binding.recyclerViewMenu.apply {
                layoutManager = GridLayoutManager(context,3)
                adapter = menuAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            Log.d(TAG, "ProductCallback: $productList")
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }
}