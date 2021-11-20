package com.ssafy.smartstore.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.adapter.FavoriteAdapter
import com.ssafy.smartstore.database.FavoriteDto
import com.ssafy.smartstore.databinding.FragmentFavoriteBinding
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

    }

    private fun initAdapter(){
        favoriteAdapter = FavoriteAdapter(listOf())
        CoroutineScope(Dispatchers.Main).launch{
            favoriteAdapter.favoriteList = getData()
            refreshAdapter()
        }


        binding.recyclerViewMenu.apply{
            adapter = favoriteAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
        favoriteAdapter.setItemClickListener(object : FavoriteAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, productId:Int) {
                mainActivity.openFragment(3, "productId", productId)
            }
        })
    }
    suspend private fun refreshAdapter(){
        favoriteAdapter.favoriteList = getData()
        favoriteAdapter.notifyDataSetChanged()
    }

    //전체 데이터 조회해서 리턴
    suspend private fun getData(): MutableList<FavoriteDto> {
        return favoriteRepository.getFavorites()
    }


}