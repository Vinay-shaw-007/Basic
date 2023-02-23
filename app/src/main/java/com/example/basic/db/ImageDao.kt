package com.example.basic.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListOfImages(filesEntityList: List<FilesEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSingleImage(singleFilesEntity: FilesEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateImageEntity(filesEntity: FilesEntity)

    @Delete
    suspend fun deleteImageEntity(filesEntity: FilesEntity)

    @Transaction
    @Query("Select * From ImageList")
    fun getAllFiles(): Flow<List<FilesEntity>>

    @Query("Select * From ImageList where imageStarred = 1")
    fun getAllStarredFiles(): Flow<List<FilesEntity>>

    @Query("Select * from ImageList where id = :fileId")
    fun getSpecificFileDetails(fileId: Int) : Flow<FilesEntity>
}