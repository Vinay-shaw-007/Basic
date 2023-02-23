package com.example.basic.db

import kotlinx.coroutines.flow.Flow

class ImageRepository(private val imageDao: ImageDao) {
    val allImages: Flow<List<FilesEntity>> = imageDao.getAllFiles()

    val allStarredFiles: Flow<List<FilesEntity>> = imageDao.getAllStarredFiles()

    suspend fun insertListOfImages(filesEntityList: List<FilesEntity>) {
        imageDao.insertListOfImages(filesEntityList)
    }

    suspend fun insertSingleImage(singleFilesEntity: FilesEntity) {
        imageDao.insertSingleImage(singleFilesEntity)
    }

    suspend fun updateImageEntity(filesEntity: FilesEntity){
        imageDao.updateImageEntity(filesEntity)
    }

    suspend fun deleteImageEntity(filesEntity: FilesEntity) {
        imageDao.deleteImageEntity(filesEntity)
    }

    fun getSpecificFileDetails(imageId: Int): Flow<FilesEntity> {
        return imageDao.getSpecificFileDetails(imageId)
    }
}