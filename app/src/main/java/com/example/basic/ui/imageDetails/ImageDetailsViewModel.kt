package com.example.basic.ui.imageDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basic.db.ImageDatabase
import com.example.basic.db.ImageEntity
import com.example.basic.db.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ImageDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ImageRepository
    private var _imageDetails = MutableStateFlow(ImageEntity("", "", ""))
    var imageDetails = _imageDetails.asStateFlow()
    init {
        val dao = ImageDatabase.getDatabase(application).getImageDao()
        repository = ImageRepository(dao)
    }

    fun getSpecificImageDetails(imageId: Int) = viewModelScope.launch(Dispatchers.Default) {
        repository.getSpecificImageDetails(imageId).collectLatest {
            _imageDetails.value = it
        }
    }
}