package com.example.basic.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basic.db.FilesEntity
import com.example.basic.db.ImageDatabase
import com.example.basic.db.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private var _allStarredFiles = MutableStateFlow<List<FilesEntity>>(listOf())

    val allStarredFiles: StateFlow<List<FilesEntity>>
        get() = _allStarredFiles

    private val repository: ImageRepository
    init {
        val dao = ImageDatabase.getDatabase(application).getImageDao()
        repository = ImageRepository(dao)
        readAllStarredFiles()
    }

    private fun readAllStarredFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.allStarredFiles.collectLatest {
                _allStarredFiles.value = it
            }
        }
    }

    fun updateImageEntity(filesEntity: FilesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateImageEntity(filesEntity)
    }

    fun deleteImageEntity(filesEntity: FilesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteImageEntity(filesEntity)
    }
}