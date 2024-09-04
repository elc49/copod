package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.CacheMissException
import com.google.android.gms.maps.model.LatLng
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.MainViewModel
import com.lomolo.copod.data.Data
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.network.ICopodRestApi
import com.lomolo.copod.type.MarketType
import com.lomolo.copod.type.MetricUnit
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
    private val copodRestApi: ICopodRestApi,
    private val farmStoreViewModel: FarmStoreViewModel,
    private val copodGraphqlApi: ICopodGraphqlApi,
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
                    val res = copodRestApi.imageUpload(file)
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

    fun setMarketUnit(unit: MetricUnit) {
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

    fun setMarketType(type: MarketType) {
        _marketInput.update {
            it.copy(type = type)
        }
    }

    fun isMarketType(type: MarketType): Boolean {
        return type == _marketInput.value.type
    }

    fun isUnitType(unit: MetricUnit): Boolean {
        return unit == _marketInput.value.unit
    }

    private fun validMarketInput(uiState: Market): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank() && unit.toString().isNotBlank() && volume.isNotBlank() && pricePerUnit.isNotBlank() && storeId.isNotBlank() && tag.isNotBlank() && details.isNotBlank() && type.toString().isNotBlank()
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
                    val res = copodGraphqlApi.createFarmMarket(_marketInput.value).dataOrThrow()
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
                                    res.createFarmMarket.running_volume,
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
    val unit: MetricUnit? = null,
    val pricePerUnit: String = "",
    val volume: String = "",
    val location: LatLng = LatLng(0.0, 0.0),
    val storeId: String = "",
    val tag: String = "",
    val details: String = "",
    val type: MarketType? = null,
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