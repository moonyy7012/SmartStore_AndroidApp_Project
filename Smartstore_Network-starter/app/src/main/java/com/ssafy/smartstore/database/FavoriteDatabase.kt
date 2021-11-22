package com.ssafy.smartstore.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssafy.smartstore.database.FavoriteDto
import com.ssafy.smartstore.database.FavoriteDao

@Database(entities = [FavoriteDto::class], version = 1)
abstract class FavoriteDatabase :RoomDatabase(){

    abstract fun favoriteDao() : FavoriteDao

}


