package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.network.IGiggyRestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InputStream

class StoreViewModel(
    private val giggyRestApi: IGiggyRestApi,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): ViewModel() {
    private val _storeInput = MutableStateFlow(Store())
    val storeUiState: StateFlow<Store> = _storeInput.asStateFlow()

    var storeImageUploadState: StoreImageUploadState by mutableStateOf(StoreImageUploadState.Success)
        private set

    fun setName(name: String) {
        _storeInput.update {
            it.copy(name = name)
        }
    }

    fun setImage(image: String) {
        _storeInput.update {
            it.copy(image = image)
        }
    }

    fun uploadImage(stream: InputStream) {
        val request = stream.readBytes().toRequestBody()
        val file = MultipartBody.Part.createFormData(
            "file",
            "store_image_${System.currentTimeMillis()}.jpg",
            request,
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = giggyRestApi.storeImageUploader(file)
                _storeInput.update {
                    it.copy(image = res.imageUri)
                }
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveStore(cb: () -> Unit = {}) {
        if (_storeInput.value.name.isNotBlank() && _storeInput.value.image.isNotBlank()) {
            cb()
        }
    }
}

data class Store(
    val name: String = "",
    val image: String = "",
)

interface StoreImageUploadState {
    data object Loading: StoreImageUploadState
    data object Success: StoreImageUploadState
    data class Error(val msg: String?): StoreImageUploadState
}