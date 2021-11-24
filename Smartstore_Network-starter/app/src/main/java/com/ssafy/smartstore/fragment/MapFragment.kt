package com.ssafy.smartstore.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.R
import com.ssafy.smartstore.fragment.OrderFragment.Companion.DEFAULT_LOCATION
import java.util.*

// Order 탭 - 지도 화면
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mainActivity: MainActivity
    private val TAG = "MainFragment_ssafy"
    private val UPDATE_INTERVAL = 1000 // 1 초
    private val FASTEST_UPDATE_INTERVAL = 500 // 0.5 초
    private var mMap: GoogleMap? = null
    private var storeMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var mCurrentLocation: Location

    // 사진 테스트 위한 임의 위치
    private lateinit var currentPosition: LatLng

    private lateinit var mapView: MapView
    private var needRequest = false


    // 위치 서비스 실행 관련한 필요한 퍼미션을 정의
    private val requiredMapPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // Intent 사용 requestForActivity 선언
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) {
        startLocationUpdates()
    }

    private val mapPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (mainActivity.checkLocationServicesStatus()) {
            needRequest = true
            startLocationUpdates()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, null)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            smallestDisplacement = 10.0f
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        return view
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        //권한 요청 대화상자 (권한이 없을때) & 실행 시 초기 위치를 서울 중심부로 이동
        setDefaultStoreLocation()
        // 1. 위치 권한을 가지고 있는지 확인
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            requiredMapPermission[0]
        )

        // 권한이 허용되어 있다면 위치 업데이트 시작
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 권한 허용 되어있음.
            // ( 안드로이드 6.0 이하 버전은 런타임 권한이 필요없기 때문에 이미 허용된 걸로 인식)
            startLocationUpdates()
        } else { //2. 권한이 없다면 권한 요청 진행
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는 다이얼로그를 이용한 권한 요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    requiredMapPermission[0]
                )
            ) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("위치 권한 허용")
                    .setMessage("위치 권한 허용이 필요합니다")
                    .setPositiveButton("확인") { _, _ ->
                        mapPermissionResult.launch(requiredMapPermission[0])
                    }
                val alertDialog = builder.create()
                alertDialog.show()

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 권한 요청
                mapPermissionResult.launch(requiredMapPermission[0])
            }
        }

        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        //Marker Event
        mMap!!.setOnMarkerClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_map_store, null)
            AlertDialog.Builder(mainActivity).apply {
                setView(view)
                setPositiveButton("길찾기") { _, _ ->
                    getAddress(mCurrentLocation.latitude,mCurrentLocation.longitude)
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "https://www.google.com/maps/dir/?api=1&origin=${getAddress(mCurrentLocation.latitude,mCurrentLocation.longitude)}&destination=${getAddress(
                                DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)}&mode=r&z=15"

                        )
                    )
                    intent.setPackage("com.google.android.apps.maps")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                setNegativeButton("전화걸기") { _, _ ->
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:010123456789")))
                }
                setNeutralButton("닫기") { dialog, _ ->
                    dialog.cancel()
                }
                show()
            }
            false
        }
    }

    fun getAddress(lat:Double, lng:Double): String{
        val geoCoder = Geocoder(mainActivity, Locale.KOREAN)
        val address = geoCoder.getFromLocation(lat, lng,1)
        Log.d(TAG, "getAddress: ${address.get(0).getAddressLine(0)}")
        return address.get(0).getAddressLine(0)
    }

    // 위치 정보 업데이트 시 호출되는 Callback 함수
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
//                MY_LOCATION = currentPosition

                //현재 위치에 마커 생성하고 이동

                setCurrentLocation(location)
                mCurrentLocation = location
            }
        }
    }

    // 권한 확인 및 위치 정보 업데이트
    private fun startLocationUpdates() {
            if (checkMapPermission()) {
                if (!mainActivity.checkLocationServicesStatus()) {
                    showDialogForLocationServiceSetting()
                }
                mFusedLocationClient?.requestLocationUpdates(  //
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()!!
                )
                if (mMap != null) mMap!!.isMyLocationEnabled = true
                if (mMap != null) mMap!!.uiSettings.isZoomControlsEnabled = true
            }

    }

    // 현재 위치 표시, 현재 위치로 카메라 이동
    fun setCurrentLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap!!.moveCamera(cameraUpdate)

    }

    // 초기 상가 위치 지정
    private fun setDefaultStoreLocation() {
        val location = Location("")
        location.latitude = DEFAULT_LOCATION.latitude
        location.longitude = DEFAULT_LOCATION.longitude
        setCurrentLocation(location)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap!!.moveCamera(cameraUpdate)
        val bitmap = (ResourcesCompat.getDrawable(
            resources,
            com.ssafy.smartstore.R.drawable.location_icon,
            null
        ) as BitmapDrawable).bitmap
        val resized = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        val markerOptions = MarkerOptions()
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resized))
        markerOptions.draggable(true)

        storeMarker = mMap!!.addMarker(markerOptions)


    }

    // 위치 정보 권한 허용 여부 체크
    private fun checkMapPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private val GPS_ENABLE_REQUEST_CODE = 2001

    private fun showDialogForLocationServiceSetting() {
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(context as MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            requestActivity.launch(callGPSSettingIntent)
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }


    ///////////////////////////////////////////////////////////////////////////////
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity.hideBottomNav(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMap?.setOnMarkerClickListener {

            showDialogStore()
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogStore() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setView(R.layout.dialog_map_store)
            setTitle("매장 상세")
            setCancelable(true)
            setPositiveButton("전화걸기") { dialog, _ ->
                dialog.cancel()
            }
            setNegativeButton("길찾기") { dialog, _ ->
                dialog.cancel()
            }
        }
        builder.create().show()
    }
}
