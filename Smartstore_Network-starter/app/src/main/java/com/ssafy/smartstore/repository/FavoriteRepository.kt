package com.ssafy.smartstore.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.withTransaction
import com.ssafy.smartstore.database.FavoriteDao
import com.ssafy.smartstore.database.FavoriteDatabase
import com.ssafy.smartstore.database.FavoriteDto


private const val DATABASE_NAME = "favorite-database.db"
class FavoriteRepository private constructor(context: Context){

    private val database : FavoriteDatabase = Room.databaseBuilder(
        context.applicationContext,
        FavoriteDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()

    private val favoriteDao = database.favoriteDao()

    suspend fun getFavorites() : MutableList<FavoriteDto> = database.withTransaction {
        favoriteDao.getFavorites()
    }

    suspend fun getFavorite(id : Int) : FavoriteDto = database.withTransaction {
        favoriteDao.getFavorite(id)
    }
    suspend fun insertFavorite(dto: FavoriteDto)= database.withTransaction {
        favoriteDao.insertFavorite(dto)
    }
    suspend fun updateFavorite(dto: FavoriteDto)= database.withTransaction {
        favoriteDao.updateFavorite(dto)
    }
    suspend fun deleteFavorite(id: Int) = database.withTransaction {
        favoriteDao.deleteFavorite(id)
        Log.d("d", "deleteFavorite: ")
    }

    companion object{
        private var INSTANCE : FavoriteRepository? =null

        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = FavoriteRepository(context)
            }
        }

        fun get() : FavoriteRepository{
            return INSTANCE ?:
            throw IllegalStateException("FavoriteRepository must be initialized")
        }
    }

}


