package com.ssafy.smartstore.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ssafy.smartstore.database.FavoriteDto
import com.ssafy.smartstore.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.dto.Product
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.repository.FavoriteRepository
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.service.CommentService
import com.ssafy.smartstore.service.ProductService
import com.ssafy.smartstore.util.CommonUtils
import com.ssafy.smartstore.util.RetrofitCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.round

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment : Fragment(){
    private lateinit var mainActivity: MainActivity
    private var commentAdapter = CommentAdapter(emptyList())
    private lateinit var product: Product
    private var productId = -1
    private lateinit var favoriteDto: FavoriteDto


    private lateinit var binding:FragmentMenuDetailBinding
    private lateinit var favoriteRepository: FavoriteRepository


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
        setfavorite()
    }
    private fun setfavorite(){
        favoriteRepository= FavoriteRepository.get()
        CoroutineScope(Dispatchers.IO).launch {
            if(favoriteRepository.getFavorite(productId)==null){
                Log.d(TAG, "setfavorite: ")
              binding.favorite.visibility = View.GONE
                binding.noFavorite.visibility = View.VISIBLE

            }else{
                binding.favorite.visibility = View.VISIBLE
                binding.noFavorite.visibility = View.GONE
            }
        }
    }

    private fun initData(){
        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
        binding.recyclerViewMenuDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    // 초기 화면 설정
    private fun setScreen(menu: MenuDetailWithCommentResponse){

        Glide.with(this)
            .load("${ApplicationClass.MENU_IMGS_URL}${menu.productImg}")
            .into(binding.menuImage)

        binding.txtMenuName.text = menu.productName
        binding.txtMenuPrice.text = "${CommonUtils.makeComma(menu.productPrice)}"
        binding.txtRating.text = "${(round(menu.productRatingAvg*10) /10)}점"
        binding.ratingBar.rating = menu.productRatingAvg.toFloat()

        commentAdapter.notifyDataSetChanged()
    }

    private fun initListener(){
        binding.btnAddList.setOnClickListener {
            val menuCnt = binding.textMenuCount.text.toString().toInt()
            val totalPrice = menuCnt * product.price

            val shoppingCart = ShoppingCart(
                product.id,
                product.img,
                product.name,
                menuCnt,
                product.price,
                totalPrice,
//                product.type
            )

            mainActivity.shppingListViewModel.addItem(shoppingCart)
            Toast.makeText(context, "상품이 장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.btnCreateComment.setOnClickListener {
            if (binding.inputComment.text.toString() != "") {
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
                val txtComment = binding.inputComment.text.toString()

                val comment = Comment(0, userId, productId, -1f, txtComment)
                showDialogRatingStar(comment)
            }
        }

        binding.btnAddCount.setOnClickListener {
            var count = (binding.textMenuCount.text.toString()).toInt()
            count++

            binding.textMenuCount.text = count.toString()
        }

        binding.btnMinusCount.setOnClickListener {
            var count = (binding.textMenuCount.text.toString()).toInt()
            if (count > 1) {
                count--
            }

            binding.textMenuCount.text = count.toString()
        }

        binding.noFavorite.setOnClickListener {
            favoriteDto=FavoriteDto(product)
            CoroutineScope(Dispatchers.IO).launch {
                favoriteRepository.insertFavorite(favoriteDto)
            }
            binding.noFavorite.visibility=View.GONE
            binding.favorite.visibility=View.VISIBLE

        }
        binding.favorite.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                favoriteRepository.deleteFavorite(productId)
            }
            binding.noFavorite.visibility=View.VISIBLE
            binding.favorite.visibility=View.GONE
        }

        commentAdapter.setBtnClickListener(object : CommentAdapter.CommentClickListener {
            override fun onClick(holder: CommentAdapter.CommentHolder, position: Int, button: Int) {
                when(button) {
                    ApplicationClass.MODIFY -> {
                        holder.etComment.apply {
                            visibility = View.VISIBLE
                            setText(holder.tvComment.text.toString())
                        }
                        holder.ivModifyAccept.visibility = View.VISIBLE
                        holder.ivModifyCancel.visibility = View.VISIBLE
                        holder.tvComment.visibility = View.GONE
                        holder.ivModify.visibility = View.GONE
                        holder.ivDelete.visibility = View.GONE
                    }
                    ApplicationClass.MODIFY_ACCCEPT -> {
                        if (holder.etComment.text.toString() == "")
                            return

                        val oldComment = commentAdapter.list[position]
                        val newComment = Comment(
                            oldComment.commentId,
                            oldComment.userId!!,
                            productId, oldComment.productRating.toFloat(),
                            holder.etComment.text.toString()
                        )
                        showDialogRatingStar(newComment)
                    }
                    ApplicationClass.MODIFY_CANCEL -> {
                        holder.ivModifyAccept.visibility = View.GONE
                        holder.ivModifyCancel.visibility = View.GONE
                        holder.etComment.visibility = View.GONE
                        holder.ivModify.visibility = View.VISIBLE
                        holder.ivDelete.visibility = View.VISIBLE
                        holder.tvComment.visibility = View.VISIBLE
                    }
                    ApplicationClass.DELETE -> {
                        val id = commentAdapter.list[position].commentId
                        CommentService().removeComment(id, CommentCallback("delete"))
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogRatingStar(comment: Comment) {
        val rating = comment.rating
        val view = layoutInflater.inflate(R.layout.dialog_menu_comment, null).apply {
            findViewById<RatingBar>(R.id.ratingBarMenuDialogComment).rating = if (rating > 0) rating else 0f
        }
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when(which) {
                Dialog.BUTTON_POSITIVE -> {
                    val newComment = Comment(
                        comment.id,
                        comment.userId,
                        comment.productId,
                        view.findViewById<RatingBar>(R.id.ratingBarMenuDialogComment).rating,
                        comment.comment
                    )

                    if (comment.rating >= 0) {
                        CommentService().updateComment(newComment, CommentCallback("update"))
                    } else {
                        CommentService().addComment(newComment, CommentCallback("add"))
                    }
                }
            }
        }

        AlertDialog.Builder(mainActivity).apply {
            setView(view)
            setPositiveButton("확인", listener)
            setNegativeButton("취소", null)
            show()
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


    inner class ProductWithCommentInsertCallback: RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onSuccess(
            code: Int,
            responseData: List<MenuDetailWithCommentResponse>
        ) {
            if(responseData.isNotEmpty()) {

                Log.d(TAG, "initData: $responseData")

                // comment 가 없을 경우 -> 들어온 response가 1개이고 해당 userId 가 null일 경우 빈 배열 Adapter 연결
                if (responseData.size == 1 && responseData[0].userId == null) {
                    commentAdapter.list = emptyList()
                } else {
                    commentAdapter.list = responseData
                }

                // 화면 정보 갱신
                setScreen(responseData[0])

                product = Product(productId, responseData[0].productName,"",responseData[0].productPrice,responseData[0].productImg)
            }

        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    inner class CommentCallback(private val state: String) : RetrofitCallback<Boolean> {
        override fun onSuccess(code: Int, responseData: Boolean) {
            Log.d(TAG, "onSuccess: $state")
            if (state == "add") binding.inputComment.setText("")
            initData()
        }

        override fun onError(t: Throwable) {
            when(state) {
                "add"    -> Log.d(TAG, t.message ?: "평가 등록 중 통신오류")
                "update" -> Log.d(TAG, t.message ?: "평가 수정 중 통신오류")
                "delete" -> Log.d(TAG, t.message ?: "평가 삭제 중 통신오류")
            }
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }
}