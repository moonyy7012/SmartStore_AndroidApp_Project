package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.FavoriteAdapter
import com.ssafy.smartstore.database.FavoriteDto
import com.ssafy.smartstore.databinding.FragmentFavoriteBinding
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.repository.FavoriteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 하단 주문 탭
private const val TAG = "FavoriteFragment_싸피"
class FavoriteFragment : Fragment(){
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var binding:FragmentFavoriteBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteRepository = FavoriteRepository.get()

        initAdapter()
        initEvent()
    }

    private fun initAdapter(){
        favoriteAdapter = FavoriteAdapter(listOf())
        CoroutineScope(Dispatchers.Main).launch{
            refreshAdapter()
        }


        binding.recyclerViewMenu.apply{
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun initEvent() {
        favoriteAdapter.setItemClickListener(object : FavoriteAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, productId:Int) {
                mainActivity.openFragment(3, "productId", productId)
            }

            override fun onSelect(view: View, position: Int, productId: Int) {
                CoroutineScope(Dispatchers.Main).launch {
                    favoriteRepository.deleteFavorite(productId)
                    refreshAdapter()
                }
                Toast.makeText(requireContext(), "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun goOrder(view: View, position: Int, fProduct: FavoriteDto) {
                val shoppingCart = ShoppingCart(
                    fProduct.productId,
                    fProduct.img,
                    fProduct.name,
                    1,
                    fProduct.price,
                    fProduct.price,
                    fProduct.type
                )

                mainActivity.shoppingListViewModel.addItem(shoppingCart)
                Toast.makeText(context, "상품이 장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private suspend fun refreshAdapter(){
        favoriteAdapter.favoriteList = getData()
        favoriteAdapter.notifyDataSetChanged()
    }

    //전체 데이터 조회해서 리턴
    private suspend fun getData(): MutableList<FavoriteDto> {
        return favoriteRepository.getFavorites()
    }
}