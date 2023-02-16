package com.example.basic.db

import kotlinx.coroutines.flow.Flow

class ImageRepository(private val imageDao: ImageDao) {
    val allImages: Flow<List<ImageEntity>> = imageDao.getAllMessages()

    suspend fun insertListOfImages(imageEntityList: List<ImageEntity>) {
        imageDao.insertListOfImages(imageEntityList)
    }

    suspend fun insertSingleImage(singleImageEntity: ImageEntity) {
        imageDao.insertSingleImage(singleImageEntity)
    }

    fun getSpecificImageDetails(imageId: Int): Flow<ImageEntity> {
        return imageDao.getSpecificImageDetails(imageId)
    }
}