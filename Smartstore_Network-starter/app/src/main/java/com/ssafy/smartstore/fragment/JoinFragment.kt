package com.ssafy.smartstore.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.databinding.FragmentJoinBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.RetrofitCallback

// 회원 가입 화면
private const val TAG = "JoinFragment_싸피"
class JoinFragment : Fragment(){
    private var checkedId = true
    private var isChecked = false
    lateinit var binding: FragmentJoinBinding
    private lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener{
            if(binding.editTextJoinID.text.toString()==""){
                Toast.makeText(context,"ID를 입력해주십시오.", Toast.LENGTH_SHORT).show()

            }else{
                isChecked=true
                UserService().isUsedId(binding.editTextJoinID.text.toString(),isUsedIdCallback())
            }

        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            if(isChecked==false){
                Toast.makeText(context,"ID 중복확인을 해주십시오.", Toast.LENGTH_SHORT).show()
            }
            else if(checkedId==true){
                Toast.makeText(context,"같은 ID가 있습니다.", Toast.LENGTH_SHORT).show()

            }else{
                val user = User(binding.editTextJoinID.text.toString(),binding.editTextJoinName.text.toString(),binding.editTextJoinPW.text.toString())
                UserService().insert(user, JoinCallback())
            }








        }
    }
    inner class JoinCallback: RetrofitCallback<Boolean> {

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
                Toast.makeText(context,"회원가입 되었습니다.", Toast.LENGTH_SHORT).show()
                // 로그인 시 user정보 sp에 저장
                val intent = Intent(context,LoginActivity::class.java)
                startActivity(intent)
        }
    }
    inner class isUsedIdCallback: RetrofitCallback<Boolean> {

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
            if (responseData==false) {
                Toast.makeText(context,"가능한 ID입니다.", Toast.LENGTH_SHORT).show()
                checkedId=false

            }else{
                Toast.makeText(context,"같은 ID가 있습니다.", Toast.LENGTH_SHORT).show()
                checkedId=true
            }
        }
    }
}