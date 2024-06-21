package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.CacheMissException
import com.lomolo.giggy.GetStoreProductsQuery
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
import okhttp3.internal.toImmutableList
import okio.IOException
import java.io.InputStream

class AddFarmProductViewModel(
    private val giggyRestApi: IGiggyRestApi,
    private val farmStoreProductViewModel: FarmStoreProductViewModel,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
    private val apolloStore: ApolloStore,
): ViewModel() {
    private val _productInput = MutableStateFlow(Product())
    val productUiState: StateFlow<Product> = _productInput.asStateFlow()

    var uploadingProductImageState: UploadProductImageState by mutableStateOf(UploadProductImageState.Success)
        private set

    var addingFarmProductState: AddFarmProductState by mutableStateOf(AddFarmProductState.Success)
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

    fun setProductName(name: String) {
        _productInput.update {
            it.copy(name = name)
        }
    }

    fun setProductUnit(unit: String) {
        _productInput.update {
            it.copy(unit = unit)
        }
    }

    fun setProductPricePerUnit(price: String) {
        _productInput.update {
            it.copy(pricePerUnit = price)
        }
    }

    fun setProductVolume(volume: String) {
        _productInput.update {
            it.copy(volume = volume)
        }
    }

    private fun validProductInput(uiState: Product): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank() && unit.isNotBlank() && volume.isNotBlank() && pricePerUnit.isNotBlank() && storeId.isNotBlank()
        }
    }

    fun addProduct(cb: () -> Unit = {}) {
        if (addingFarmProductState !is AddFarmProductState.Loading &&
            validProductInput(_productInput.value) &&
            uploadingProductImageState is UploadProductImageState.Success) {
            addingFarmProductState = AddFarmProductState.Loading
            viewModelScope.launch {
                addingFarmProductState = try {
                    val res = giggyGraphqlApi.createStoreProduct(_productInput.value).dataOrThrow()
                    try {
                        val updatedCachedData = apolloStore.readOperation(
                            GetStoreProductsQuery(_productInput.value.storeId)
                        )
                            .getStoreProducts
                            .toMutableList()
                            .apply {
                                add(
                                    GetStoreProductsQuery.GetStoreProduct(
                                        res.createStoreProduct.id,
                                        res.createStoreProduct.name,
                                        res.createStoreProduct.image,
                                        res.createStoreProduct.volume,
                                        res.createStoreProduct.pricePerUnit,
                                    )
                                )
                            }
                            .toImmutableList()
                        apolloStore.writeOperation(
                            GetStoreProductsQuery(_productInput.value.storeId),
                            GetStoreProductsQuery.Data(updatedCachedData),
                        )
                    } catch(e: CacheMissException) {
                        e.printStackTrace()
                    }
                    AddFarmProductState.Success.also {
                        cb()
                    }
                } catch(e: IOException) {
                    e.printStackTrace()
                    AddFarmProductState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun resetProductState() {
        _productInput.value = Product()
    }

    init {
        _productInput.update {
            it.copy(storeId = farmStoreProductViewModel.getStoreId())
        }
    }
}

data class Product(
    val name: String = "",
    val image: String = "",
    val unit: String = "",
    val pricePerUnit: String = "",
    val volume: String = "",
    val storeId: String = "",
)

interface UploadProductImageState {
    data object Loading: UploadProductImageState
    data class Error(val msg: String?): UploadProductImageState
    data object Success: UploadProductImageState
}

interface AddFarmProductState {
    data object Loading: AddFarmProductState
    data object Success: AddFarmProductState
    data class Error(val msg: String?): AddFarmProductState
}