package com.example.basic.ui.pdf

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

class PDFViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ImageRepository
    private var _imageDetails = MutableStateFlow(FilesEntity("", "", "", 0, ""))
    var imageDetails = _imageDetails.asStateFlow()

    init {
        val dao = ImageDatabase.getDatabase(application).getImageDao()
        repository = ImageRepository(dao)
    }

    fun getSpecificImageDetails(imageId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.getSpecificImageDetails(imageId).collectLatest {
            _imageDetails.value = it
        }
    }
}