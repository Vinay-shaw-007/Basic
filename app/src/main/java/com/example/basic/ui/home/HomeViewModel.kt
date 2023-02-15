package com.example.basic.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.basic.db.ImageEntity
import com.example.basic.db.ImageDatabase
import com.example.basic.db.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var _allImages = MutableStateFlow<List<ImageEntity>>(listOf())
    val allImages: StateFlow<List<ImageEntity>>
        get() = _allImages

    private val repository: ImageRepository
    init {
        val dao = ImageDatabase.getDatabase(application).getImageDao()
        repository = ImageRepository(dao)
        readAllImages()
    }

    private fun readAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.allImages.collectLatest {
                _allImages.value = it
            }
        }
    }
    fun insertImageList(imageEntityList: List<ImageEntity>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertListOfImages(imageEntityList)
    }

    fun insertSingleImage(singleImageEntity: ImageEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSingleImage(singleImageEntity)
    }
}