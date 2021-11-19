package com.ssafy.smartstore.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.R
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.CommentAdapter
import com.ssafy.smartstore.config.ApplicationClass
import com.ssafy.smartstore.config.ApplicationClass.Companion.shoppingList
import com.ssafy.smartstore.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.service.CommentService
import com.ssafy.smartstore.service.ProductService
import com.ssafy.smartstore.util.CommonUtils
import com.ssafy.smartstore.util.RetrofitCallback
import kotlin.math.round

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment : Fragment(){
    private lateinit var mainActivity: MainActivity
    private var commentAdapter = CommentAdapter(emptyList())
    private var productId = -1
    private var productImg =""
    private var productType = "coffee"
    private var productPrice = 0

    private var rating: Float = 0.0F
    val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

    // update 관련
    var prevComment: String = ""
    var currentComment: String = ""


    private lateinit var binding:FragmentMenuDetailBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

        arguments?.let {
            productId = it.getInt("productId", -1)
            Log.d(TAG, "onCreate: $productId")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuDetailBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initListener()
    }


    private fun initData(){
        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
        commentAdapter.setUserId(userId)
        binding.recyclerViewMenuDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        commentAdapter.onItemClickListener = object : CommentAdapter.OnItemClickListener {
            override fun onEditClick(view: View, position: Int, commentId: Int) {
                prevComment = commentAdapter.list.get(position).commentContent!!
                showDialogEditComment(position)
            }

            override fun onRemoveClick(view: View, position: Int, commentId: Int) {
                CommentService().delete(commentId)
                Toast.makeText(context, "별점 삭제 완료", Toast.LENGTH_SHORT).show()

                ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
            }

            override fun onSaveClick(view: View, position: Int, commentId: Int) {
                Log.d(TAG, "onSaveClick: ${commentAdapter.list[position]}")
                var cObj = commentAdapter.list[position]
                var comment = Comment(cObj.commentId, cObj.userId!!, productId, cObj.productRating.toFloat(), cObj.commentContent!!)
                CommentService().update(comment)
                Toast.makeText(context, "별점 수정 완료", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelClick(view: View, position: Int, commentId: Int) {
                Toast.makeText(context, "별점 수정 취소", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "prev : $prevComment, cur : $currentComment")
                commentAdapter.list[position].commentContent = prevComment
                commentAdapter.notifyDataSetChanged()
            }

        }
    }

    // 초기 화면 설정
    private fun setScreen(menu: MenuDetailWithCommentResponse){

        Glide.with(this)
            .load("${ApplicationClass.MENU_IMGS_URL}${menu.productImg}")
            .into(binding.menuImage)

        productImg=menu.productImg
//        Log.d(TAG, "setScreen: ${productImg}")
        productPrice=menu.productPrice
        binding.txtMenuName.text = menu.productName
        binding.txtMenuPrice.text = "${CommonUtils.makeComma(menu.productPrice)}"
        binding.txtRating.text = "${(round(menu.productRatingAvg*10) /10)}점"
        binding.ratingBar.rating = menu.productRatingAvg.toFloat()/2

//        Log.d(TAG, "setScreen: ${commentAdapter.list}")
        commentAdapter.notifyDataSetChanged()

    }

    private fun initListener(){
        binding.btnAddList.setOnClickListener {
            var menucnt = binding.textMenuCount.text.toString().toInt()
            var totalprice = menucnt*productPrice


            var shoppingCart= ShoppingCart(productId, productImg, binding.txtMenuName.text.toString(), menucnt, productPrice, totalprice, productType)

            shoppingList.add(shoppingCart)
            Toast.makeText(context,"상품이 장바구니에 담겼습니다.",Toast.LENGTH_SHORT).show()

        }
        binding.btnCreateComment.setOnClickListener {
            showDialogRatingStar()
            Toast.makeText(context,"버튼클릭",Toast.LENGTH_SHORT).show()
        }

        binding.btnAddCount.setOnClickListener {
            var count = (binding.textMenuCount.text.toString()).toInt()
            count++

            binding.textMenuCount.text = count.toString()
        }

        binding.btnMinusCount.setOnClickListener {
            var count = (binding.textMenuCount.text.toString()).toInt()
            count--

            binding.textMenuCount.text = count.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogRatingStar() {
        val builder = AlertDialog.Builder(requireContext())
        val v1 = layoutInflater.inflate(R.layout.dialog_menu_comment, null)
        builder.setView(v1)

        val listener = DialogInterface.OnClickListener { dialog, which ->
            val alert = dialog as AlertDialog
            val rBar: RatingBar = alert.findViewById(R.id.ratingBarMenuDialogComment)

            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {

                    val comment: String = binding.etComment.text.toString()
                    rating = rBar.rating

                    val commentModel = Comment(-1, userId, productId, rating, comment)

                    CommentService().insert(commentModel, commentCallback())

                }
            }
        }

        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", listener)
        builder.show()
    }

    private fun showDialogEditComment(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val v1 = layoutInflater.inflate(R.layout.dialog_menu_editcomment,null)
        builder.setView(v1)
        v1.findViewById<EditText>(R.id.dialEditText).setText(commentAdapter.list[position].commentContent)
        val listener = DialogInterface.OnClickListener { dialog, which ->
            val alert = dialog as AlertDialog

            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    currentComment = alert.findViewById<EditText>(R.id.dialEditText).text.toString()

                    commentAdapter.list[position].commentContent = currentComment
                    commentAdapter.notifyDataSetChanged()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    commentAdapter.update = false
                    commentAdapter.notifyDataSetChanged()
                }
            }
        }
        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", listener)
        builder.show()
    }


    inner class commentCallback: RetrofitCallback<Comment> {
        override fun onError(t: Throwable) {
            Toast.makeText(requireContext(), "별점 등록 실패 Error", Toast.LENGTH_SHORT).show()
        }

        override fun onSuccess(code: Int, responseData: Comment) {
            Toast.makeText(requireContext(), "별점이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            binding.etComment.setText("")
            initData()
        }

        override fun onFailure(code: Int) {
            Toast.makeText(requireContext(), "별점 등록 실패 Failure", Toast.LENGTH_SHORT).show()
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(key:String, value:Int) =
            MenuDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }

    fun notifyChanged() {

        binding.recyclerViewMenuDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    inner class ProductWithCommentInsertCallback: RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onSuccess(
            code: Int,
            responseData: List<MenuDetailWithCommentResponse>
        ) {
            if(responseData.isNotEmpty()) {

                Log.d(TAG, "initData: ${responseData}")

                // comment 가 없을 경우 -> 들어온 response가 1개이고 해당 userId 가 null일 경우 빈 배열 Adapter 연결
                if (responseData.size ==1 && responseData[0].userId == null) {
                    commentAdapter.setItems(emptyList())

                } else {
                    commentAdapter.setItems(responseData)
                }

                // 화면 정보 갱신
                setScreen(responseData[0])
            }

        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }



}