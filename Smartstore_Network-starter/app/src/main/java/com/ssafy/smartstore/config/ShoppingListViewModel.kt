package com.ssafy.smartstore.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.smartstore.dto.ShoppingCart

class ShoppingListViewModel : ViewModel() {
    private val _shoppingList = MutableLiveData<MutableList<ShoppingCart>>().apply {
        value = mutableListOf()
    }

    val shppingList: LiveData<MutableList<ShoppingCart>>
        get() = _shoppingList

    fun addItem(item: ShoppingCart) {
        _shoppingList.value!!.add(item)
    }

    fun removeItem(position: Int) {
        _shoppingList.value!!.removeAt(position)
    }

    fun clearCart() {
        _shoppingList.value!!.clear()
    }
}