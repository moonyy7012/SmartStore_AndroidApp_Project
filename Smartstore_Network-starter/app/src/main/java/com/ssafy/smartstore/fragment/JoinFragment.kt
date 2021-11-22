package com.ssafy.smartstore.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.databinding.FragmentJoinBinding
import com.ssafy.smartstore.dto.User
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.RetrofitCallback

// 회원 가입 화면
private const val TAG = "JoinFragment_싸피"
class JoinFragment : Fragment(){
    private var checkedId = false
    lateinit var binding: FragmentJoinBinding

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

        //id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener {
            val joinId = binding.editTextJoinID.text.toString()

            if (joinId != "") {
                UserService().isUsedId(joinId, object : RetrofitCallback<Boolean> {
                    override fun onSuccess(code: Int, responseData: Boolean) {
                        if (responseData == false) {
                            checkedId = true
                            Toast.makeText(context, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                            changeView(true, binding.editTextJoinID)
                        } else {
                            checkedId = false
                            Toast.makeText(context, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
                            changeView(false, binding.editTextJoinID)
                        }
                    }

                    override fun onFailure(code: Int) {
                        Log.d(TAG, "onResponse: Error Code $code")
                    }

                    override fun onError(t: Throwable) {
                        Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
                    }

                })
            }
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            Log.d(TAG, "btnJoin: ")
            val joinId = binding.editTextJoinID.text.toString()
            val joinPw = binding.editTextJoinPW.text.toString()
            val joinName = binding.editTextJoinName.text.toString()

            if (isNotVoid(joinId, joinPw, joinName)) {
                if (checkedId == false) {
                    Toast.makeText(context, "아이디 중복을 검사하세요", Toast.LENGTH_SHORT).show()
                } else {
                    join(joinId, joinPw, joinName)
                }
            } else {
                Toast.makeText(context, "모든 정보를 기입하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun join(joinId: String, joinPw: String, joinName: String) {
        val user = User(joinId, joinName, joinPw)

        UserService().join(user, object : RetrofitCallback<Boolean> {
            override fun onSuccess(code: Int, responseData: Boolean) {
                if (responseData == true) {
                    Toast.makeText(context, "회원 가입 성공!", Toast.LENGTH_SHORT).show()
                    (requireActivity() as LoginActivity).openFragment(3)
                } else {
                    Toast.makeText(context, "문제가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(code: Int) {
                Log.d(TAG, "onResponse: Error Code $code")
                Toast.makeText(context, "문제가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

            override fun onError(t: Throwable) {
                Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
                Toast.makeText(context, "문제가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun changeView(pass: Boolean, view: View) {
        if (pass)   view.setBackgroundResource(R.drawable.textview_regular)
        else        view.setBackgroundResource(R.drawable.textview_regular_red)
    }

    private fun isNotVoid(joinId: String, joinPw: String, joinName: String): Boolean {
        var result = true

        Log.d(TAG, "isNotVoid: $joinId $joinPw $joinName")

        if (joinId == "") {
            changeView(false, binding.editTextJoinID)
            result = false
        }
        else {
            changeView(true, binding.editTextJoinID)
        }
        if (joinPw == "") {
            changeView(false, binding.editTextJoinPW)
            result = false
        }
        else {
            changeView(true, binding.editTextJoinPW)
        }
        if (joinName == "") {
            changeView(false, binding.editTextJoinName)
            result = false
        }
        else {
            changeView(true, binding.editTextJoinName)
        }

        return result
    }
}