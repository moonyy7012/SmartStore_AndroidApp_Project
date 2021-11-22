package com.ssafy.smartstore.activity

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssafy.smartstore.*
import com.ssafy.smartstore.R
import com.ssafy.smartstore.adapter.OrderDetailListAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.config.ShoppingListViewModel
import com.ssafy.smartstore.dto.Order
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.fragment.*
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.util.CommonUtils
import com.ssafy.smartstore.util.RetrofitCallback
import org.altbeacon.beacon.*
import java.util.*

private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity(), BeaconConsumer {
    private lateinit var bottomNavigation : BottomNavigationView
    val shppingListViewModel: ShoppingListViewModel by lazy {
        ViewModelProvider(this)[ShoppingListViewModel::class.java]
    }
    var tableN = ""
    var orderId = -1
    var readable = false
    var isNear = false

    // beacon
    private lateinit var beaconManager: BeaconManager
    private val BEACON_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825"
    private val BEACON_MAJOR = "10004"
    private val BEACON_MINOR = "54480"
    private val STORE_DISTANCE = 1 // 스토어 거리 1m

    private val region = Region("altbeacon"
        , Identifier.parse(BEACON_UUID)
        , Identifier.parse(BEACON_MAJOR)
        , Identifier.parse(BEACON_MINOR)
    )

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var needBLERequest = true

    private val PERMISSIONS_CODE = 100

    // nfc
    var nfcAdapter: NfcAdapter? = null
    var pIntent:PendingIntent? = null
    lateinit var filters: Array<IntentFilter>
    lateinit var i:Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 가장 첫 화면은 홈 화면의 Fragment로 지정

        setNdef()

        setBeacon()

        createNotificationChannel("ssafy_channel", "ssafy")

        checkPermissions()

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        bottomNavigation = findViewById(R.id.tab_layout_bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, FavoriteFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_4 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, MyPageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            // 재선택시 다시 랜더링 하지 않기 위해 수정
            if(bottomNavigation.selectedItemId != item.itemId){
                bottomNavigation.selectedItemId = item.itemId
            }
        }
    }

    fun openFragment(index:Int, key:String, value:Int){
        moveFragment(index, key, value)
    }

    fun openFragment(index: Int) {
        moveFragment(index, "", 0)
    }

    private fun moveFragment(index:Int, key:String, value:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(index){
            //장바구니
            1 -> {
                if (value != 0) {
                    transaction.replace(R.id.frame_layout_main, ShoppingListFragment.newInstance(key, value))
                        .addToBackStack(null)
                } else {
                    transaction.replace(R.id.frame_layout_main, ShoppingListFragment())
                        .addToBackStack(null)
                }
            }
            //주문 상세 보기
            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            //메뉴 상세 보기
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            //map으로 가기
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
            //logout
            5 -> {
                logout()
            }
            //coupon
            6 -> transaction.replace(R.id.frame_layout_main, CouponFragment())
                .addToBackStack(null)
        }
        transaction.commit()
    }

    fun logout(){
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()

        //화면이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    fun hideBottomNav(state : Boolean){
        if(state) bottomNavigation.visibility =  View.GONE
        else bottomNavigation.visibility = View.VISIBLE
    }

    private fun setNdef(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            finish()
        }
        i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pIntent = PendingIntent.getActivity(this, 0, i, 0)

        val tagFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        filters = arrayOf(tagFilter)
    }

    private fun setBeacon(){
        checkPermissions()

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        startScan()
    }

    // NotificationChannel 설정
    private fun createNotificationChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)

            val notificationManager: NotificationManager
                    = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                ApplicationClass.requiredPermissions,
                PERMISSIONS_CODE
            )
        }
    }

    // Beacon Scan 시작
    private fun startScan() {
        // 블루투스 Enable 확인
        if(!isEnableBLEService()){
            requestEnableBLE()
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }

        // 위치 정보 권한 허용 및 GPS Enable 여부 확인
        checkPermissions()
        Log.d(TAG, "startScan: beacon Scan start")

        // Beacon Service bind
        beaconManager.bind(this)
    }

    // 블루투스 켰는지 확인
    private fun isEnableBLEService(): Boolean{
        if(!bluetoothAdapter!!.isEnabled){
            return false
        }
        return true
    }

    // 블루투스 ON/OFF 여부 확인 및 키도록 하는 함수
    private fun requestEnableBLE(){
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        // 사용자의 블루투스 사용이 가능한지 확인
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // 위치 정보 권한 요청 결과 콜백 함수
    // ActivityCompat.requestPermissions 실행 이후 실행
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i(TAG, "$permission 권한 획득에 실패하였습니다.")
                            finish()
                        }
                    }
                }
            }
        }
    }
    override fun onBeaconServiceConnect() {

        beaconManager.addMonitorNotifier(object : MonitorNotifier {

            override fun didEnterRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 발견하였습니다.------------${region.toString()}")
                    beaconManager.startRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didExitRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 찾을 수 없습니다.")
                    beaconManager.stopRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didDetermineStateForRegion(i: Int, region: Region?) {}
        })

        beaconManager.addRangeNotifier { beacons, region ->
            for (beacon in beacons) {
                // Major, Minor로 Beacon 구별, 1미터 이내에 들어오면 다이얼로그 출력
                if(isStoreBeacon(beacon)){
                    if (beacon.distance <= STORE_DISTANCE) {
                        // 주문한건지 확인
                        showPopDialog()
                        beaconManager.stopMonitoringBeaconsInRegion(region)
                        beaconManager.stopRangingBeaconsInRegion(region)
                    }

                    if (beacon.distance <= 200) {
                        isNear = true
                    }
                }
            }
        }

        try {
            beaconManager.startMonitoringBeaconsInRegion(region)
        } catch (e: RemoteException){
            e.printStackTrace()
        }
    }

    // 찾고자 하는 Beacon이 맞는지, 정해둔 거리 내부인지 확인
    private fun isStoreBeacon(beacon: Beacon): Boolean {
        return beacon.id2.toString() == BEACON_MAJOR && beacon.id3.toString() == BEACON_MINOR && beacon.distance <= STORE_DISTANCE

    }

    private fun showPopDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_popup, null)

        if (orderId > 0) {
            val orderList = OrderService().getOrderDetails(orderId)
            orderId = -1    // 나중에 주문완료, pickUp 완성되면 그쪽으로 이동

            runOnUiThread {
                orderList.observe(this, { orderDetails ->
                    val orderListAdapter = OrderDetailListAdapter(this, orderDetails)
                    view.apply {
                        findViewById<TextView>(R.id.tvVoid).visibility = View.GONE
                        findViewById<RecyclerView>(R.id.dialogRecycler).apply {
                            val linearLayoutManager = LinearLayoutManager(context)
                            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                            layoutManager = linearLayoutManager
                            adapter = orderListAdapter
                        }
                    }
                    AlertDialog.Builder(this).apply {
                        setView(view)
                        setPositiveButton("확인", null)
                        show()
                    }
                    Log.d(TAG, "showDialog: success")
                })
            }
        } else {
            runOnUiThread {
                view.apply {
                    findViewById<TextView>(R.id.tvVoid).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.dialogRecycler).visibility = View.GONE
                }
                AlertDialog.Builder(this).apply {
                    setView(view)
                    setPositiveButton("확인", null)
                    show()
                }
            }
            Log.d(TAG, "showDialog: success")
        }
    }

    //Tag 데이터를 추출하는 함수
    private fun getNFCData(intent: Intent){
        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

        if(rawMsgs!=null){
            val message = arrayOfNulls<NdefMessage>(rawMsgs.size)
            for(i in rawMsgs.indices){
                message[i]=rawMsgs[i] as NdefMessage
            }
            //실제 저장되어 있는 데이터를 추출
            val record_data = message[0]!!.records[0]
            val record_type = record_data.type
            val type = String(record_type)
            if(type.equals("T")){
                val data = record_data.payload
                //가져온 데이터를 TextView에 반영
                tableN = String(data).substring(3)
                completedOrder()
                Log.d("tableN", "getNFCData: $tableN")
            }
        }
    }

    fun completedOrder() {
        shppingListViewModel.shoppingList.observe(this, { list ->
            Log.d(TAG, "completedOrder: $list")

            val order = Order().apply {
                userId = ApplicationClass.sharedPreferencesUtil.getUser().id
                orderTable = tableN
            }

            for (item in list) {
                order.details.add(OrderDetail(item.menuId, item.menuCnt))
            }

            OrderService().makeOrder(order, OrderCallback())
        })
    }

    inner class OrderCallback: RetrofitCallback<Int> {
        override fun onSuccess(code: Int, responseData: Int) {
            orderId = responseData
            Toast.makeText(this@MainActivity, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            readable = false
            shppingListViewModel.clearCart()
            supportFragmentManager.apply {
                beginTransaction().remove(ShoppingListFragment()).commit()
                popBackStack()
                beginTransaction().replace(R.id.frame_layout_main, MyPageFragment()).commit()
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "주문정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    override fun onDestroy() {
        beaconManager.stopMonitoringBeaconsInRegion(region)
        beaconManager.stopRangingBeaconsInRegion(region)
        beaconManager.unbind(this)
        super.onDestroy()

    }

    override fun onResume() {
        super.onResume()
        nfcAdapter!!.enableForegroundDispatch(this, pIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter!!.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("INFO", "onNewIntent called...")
        Log.e(TAG, "${intent!!.action}", )

        if(readable == true && intent.action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
            getNFCData(intent)
        }
    }
}