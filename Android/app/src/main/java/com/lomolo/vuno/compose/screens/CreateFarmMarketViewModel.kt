package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.CacheMissException
import com.google.android.gms.maps.model.LatLng
import com.lomolo.vuno.GetFarmMarketsQuery
import com.lomolo.vuno.MainViewModel
import com.lomolo.vuno.data.Data
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.network.IVunoRestApi
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

class AddFarmMarketViewModel(
    private val giggyRestApi: IVunoRestApi,
    private val farmStoreViewModel: FarmStoreViewModel,
    private val giggyGraphqlApi: IVunoGraphqlApi,
    private val apolloStore: ApolloStore,
    private val mainViewModel: MainViewModel,
) : ViewModel() {
    private val _marketInput = MutableStateFlow(Market())
    val marketUiState: StateFlow<Market> = _marketInput.asStateFlow()

    var uploadingMarketImageState: UploadMarketImageState by mutableStateOf(UploadMarketImageState.Success)
        private set

    var addingFarmMarketState: AddFarmMarketState by mutableStateOf(AddFarmMarketState.Success)
        private set

    fun uploadImage(stream: InputStream) {
        if (uploadingMarketImageState !is UploadMarketImageState.Loading) {
            _marketInput.update { it.copy(image = "") }
            uploadingMarketImageState = UploadMarketImageState.Loading
            val request = stream.readBytes().toRequestBody()
            val file = MultipartBody.Part.createFormData(
                "file",
                "market_image_${System.currentTimeMillis()}.jpg",
                request,
            )
            viewModelScope.launch(Dispatchers.IO) {
                uploadingMarketImageState = try {
                    val res = giggyRestApi.imageUpload(file)
                    _marketInput.update {
                        it.copy(image = res.imageUri)
                    }
                    UploadMarketImageState.Success
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                    UploadMarketImageState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun setMarketName(name: String) {
        _marketInput.update {
            it.copy(name = name)
        }
    }

    fun setMarketUnit(unit: String) {
        _marketInput.update {
            it.copy(unit = unit)
        }
    }

    fun setMarketPricePerUnit(price: String) {
        _marketInput.update {
            it.copy(pricePerUnit = price)
        }
    }

    fun setMarketVolume(volume: String) {
        _marketInput.update {
            it.copy(volume = volume)
        }
    }

    fun setMarketDetails(details: String) {
        _marketInput.update {
            it.copy(details = details)
        }
    }

    private fun validMarketInput(uiState: Market): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank() && unit.isNotBlank() && volume.isNotBlank() && pricePerUnit.isNotBlank() && storeId.isNotBlank() && tag.isNotBlank() && details.isNotBlank()
        }
    }

    fun addMarket(cb: () -> Unit = {}) {
        if (addingFarmMarketState !is AddFarmMarketState.Loading && validMarketInput(_marketInput.value) && uploadingMarketImageState is UploadMarketImageState.Success) {
            addingFarmMarketState = AddFarmMarketState.Loading
            viewModelScope.launch {
                addingFarmMarketState = try {
                    _marketInput.update {
                        it.copy(location = mainViewModel.getValidDeviceGps())
                    }
                    val res = giggyGraphqlApi.createFarmMarket(_marketInput.value).dataOrThrow()
                    try {
                        val updatedCachedData = apolloStore.readOperation(
                            GetFarmMarketsQuery(_marketInput.value.storeId)
                        ).getFarmMarkets.toMutableList().apply {
                            add(
                                GetFarmMarketsQuery.GetFarmMarket(
                                    res.createFarmMarket.id,
                                    res.createFarmMarket.name,
                                    res.createFarmMarket.image,
                                    res.createFarmMarket.farmId,
                                    res.createFarmMarket.status,
                                    res.createFarmMarket.unit,
                                    res.createFarmMarket.volume,
                                    res.createFarmMarket.pricePerUnit,
                                )
                            )
                        }.toImmutableList()
                        apolloStore.writeOperation(
                            GetFarmMarketsQuery(_marketInput.value.storeId),
                            GetFarmMarketsQuery.Data(updatedCachedData),
                        )
                    } catch (e: CacheMissException) {
                        e.printStackTrace()
                    }
                    AddFarmMarketState.Success.also {
                        cb()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    AddFarmMarketState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun resetMarketState() {
        _marketInput.value = Market()
    }

    val category = Data.marketTags

    fun tagAlreadyExists(tag: String): Boolean {
        return _marketInput.value.tag == tag
    }

    fun addMarketCategory(tag: String) {
        _marketInput.update {
            it.copy(tag = tag)
        }
    }

    init {
        _marketInput.update {
            it.copy(
                storeId = farmStoreViewModel.getFarmId(),
                tag = category[0],
            )
        }
    }
}

data class Market(
    val name: String = "",
    val image: String = "",
    val unit: String = "",
    val pricePerUnit: String = "",
    val volume: String = "",
    val location: LatLng = LatLng(0.0, 0.0),
    val storeId: String = "",
    val tag: String = "",
    val details: String = "",
)

interface UploadMarketImageState {
    data object Loading : UploadMarketImageState
    data class Error(val msg: String?) : UploadMarketImageState
    data object Success : UploadMarketImageState
}

interface AddFarmMarketState {
    data object Loading : AddFarmMarketState
    data object Success : AddFarmMarketState
    data class Error(val msg: String?) : AddFarmMarketState
}