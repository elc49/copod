package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.network.IGiggyRestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class AddFarmProductViewModel(
    private val giggyRestApi: IGiggyRestApi,
    private val farmStoreProductViewModel: FarmStoreProductViewModel,
): ViewModel() {
    private val _productInput = MutableStateFlow(Product())
    val productUiState: StateFlow<Product> = _productInput.asStateFlow()

    var uploadingProductImageState: UploadProductImageState by mutableStateOf(UploadProductImageState.Success)
        private set

    fun uploadImage(stream: InputStream) {
        _productInput.update { it.copy(image = "") }
        uploadingProductImageState = UploadProductImageState.Loading
        val request = stream.readBytes().toRequestBody()
        val file = MultipartBody.Part.createFormData(
            "file",
            "product_image_${System.currentTimeMillis()}.jpg",
            request,
        )
        viewModelScope.launch(Dispatchers.IO) {
            uploadingProductImageState = try {
                val res = giggyRestApi.imageUpload(file)
                _productInput.update {
                    it.copy(image = res.imageUri)
                }
                UploadProductImageState.Success
            } catch(e: java.io.IOException) {
                e.printStackTrace()
                UploadProductImageState.Error(e.localizedMessage)
            }
        }
    }

    fun addProduct() {
        println(farmStoreProductViewModel.getStoreId())
    }
}

data class Product(
    val name: String = "",
    val image: String = "",
    val unit: String = "",
    val pricePerUnit: String = "",
    val volume: String = "",
)

interface UploadProductImageState {
    data object Loading: UploadProductImageState
    data class Error(val msg: String?): UploadProductImageState
    data object Success: UploadProductImageState
}