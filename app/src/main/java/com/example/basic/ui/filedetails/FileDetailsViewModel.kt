package com.example.basic.ui.filedetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basic.db.FilesEntity
import com.example.basic.db.ImageDatabase
import com.example.basic.db.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FileDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ImageRepository
    private var _fileDetails = MutableStateFlow(FilesEntity("", "", "", 0, ""))
    var fileDetails = _fileDetails.asStateFlow()

    init {
        val dao = ImageDatabase.getDatabase(application).getImageDao()
        repository = ImageRepository(dao)
    }

    fun getSpecificFileDetails(imageId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.getSpecificFileDetails(imageId).collectLatest {
            _fileDetails.value = it
        }
    }
}