package com.example.basic.ui.files

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.basic.db.FilesEntity
import com.example.basic.db.ImageDatabase
import com.example.basic.db.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FilesViewModel(application: Application) : AndroidViewModel(application) {
    private var _allFiles = MutableStateFlow<List<FilesEntity>>(listOf())

    val allFiles: StateFlow<List<FilesEntity>>
        get() = _allFiles

    private val repository: ImageRepository
    init {
        val dao = ImageDatabase.getDatabase(application).getImageDao()
        repository = ImageRepository(dao)
        readAllImages()
    }

    private fun readAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.allImages.collectLatest {
                _allFiles.value = it
            }
        }
    }

    fun insertImageList(filesEntityList: List<FilesEntity>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertListOfImages(filesEntityList)
    }

    fun insertSingleImage(singleFilesEntity: FilesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSingleImage(singleFilesEntity)
    }

    fun updateImageEntity(filesEntity: FilesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateImageEntity(filesEntity)
    }

    fun deleteImageEntity(filesEntity: FilesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteImageEntity(filesEntity)
    }
}