package com.ssafy.smartstore.database

import androidx.room.*

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    suspend fun getFavorites() : MutableList<FavoriteDto>

    @Query("SELECT * FROM favorite WHERE productId = (:id)")
    suspend fun getFavorite(id: Int) : FavoriteDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(dto : FavoriteDto)

    @Update
    suspend fun updateFavorite(dto : FavoriteDto)

    @Query("DELETE FROM favorite WHERE productId = (:id)")
    suspend fun deleteFavorite(id:Int)

}


