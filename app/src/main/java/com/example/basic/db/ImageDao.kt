package com.example.basic.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListOfImages(imageEntityList: List<ImageEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleImage(singleImageEntity: ImageEntity)

    @Transaction
    @Query("Select * From ImageList")
    fun getAllMessages(): Flow<List<ImageEntity>>
}