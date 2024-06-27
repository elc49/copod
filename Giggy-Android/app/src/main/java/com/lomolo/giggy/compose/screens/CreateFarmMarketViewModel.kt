package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.CacheMissException
import com.lomolo.giggy.GetFarmMarketsQuery
import com.lomolo.giggy.data.Data
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

class AddFarmMarketViewModel(
    private val giggyRestApi: IGiggyRestApi,
    private val farmMarketViewModel: FarmMarketViewModel,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
    private val apolloStore: ApolloStore,
): ViewModel() {
    private val _marketInput = MutableStateFlow(Market())
    val marketUiState: StateFlow<Market> = _marketInput.asStateFlow()

    var uploadingMarketImageState: UploadMarketImageState by mutableStateOf(UploadMarketImageState.Success)
        private set

    var addingFarmMarketState: AddFarmMarketState by mutableStateOf(AddFarmMarketState.Success)
        private set

    fun uploadImage(stream: InputStream) {
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
            } catch(e: java.io.IOException) {
                e.printStackTrace()
                UploadMarketImageState.Error(e.localizedMessage)
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

    private fun validMarketInput(uiState: Market): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank() && unit.isNotBlank() && volume.isNotBlank() && pricePerUnit.isNotBlank() && storeId.isNotBlank()
        }
    }

    fun addMarket(cb: () -> Unit = {}) {
        if (addingFarmMarketState !is AddFarmMarketState.Loading &&
            validMarketInput(_marketInput.value) &&
            uploadingMarketImageState is UploadMarketImageState.Success
        ) {
            addingFarmMarketState = AddFarmMarketState.Loading
            viewModelScope.launch {
                addingFarmMarketState = try {
                    val res = giggyGraphqlApi.createFarmMarket(_marketInput.value).dataOrThrow()
                    try {
                        val updatedCachedData = apolloStore.readOperation(
                            GetFarmMarketsQuery(_marketInput.value.storeId)
                        )
                            .getFarmMarkets
                            .toMutableList()
                            .apply {
                                add(
                                    GetFarmMarketsQuery.GetFarmMarket(
                                        res.createFarmMarket.id,
                                        res.createFarmMarket.name,
                                        res.createFarmMarket.image,
                                        res.createFarmMarket.volume,
                                        res.createFarmMarket.pricePerUnit,
                                    )
                                )
                            }
                            .toImmutableList()
                        apolloStore.writeOperation(
                            GetFarmMarketsQuery(_marketInput.value.storeId),
                            GetFarmMarketsQuery.Data(updatedCachedData),
                        )
                    } catch(e: CacheMissException) {
                        e.printStackTrace()
                    }
                    AddFarmMarketState.Success.also {
                        cb()
                    }
                } catch(e: IOException) {
                    e.printStackTrace()
                    AddFarmMarketState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun resetMarketState() {
        _marketInput.value = Market()
    }

    val tags = Data.tags

    private fun addTag(tag: String) {
        _marketInput.update {
            val existingTags = it.tags.toMutableList()
            existingTags.add(tag)
            it.copy(tags = existingTags.toList())
        }
    }

    private fun tagAlreadyExists(tag: String): Boolean {
        return _marketInput.value.tags.contains(tag)
    }

    private fun removeTag(tag: String) {
        _marketInput.update {
            val existingTags = it.tags.toMutableList()
            existingTags.remove(tag)
            it.copy(tags = existingTags.toList())
        }
    }

    fun addMarketTag(tag: String) {
        if (tagAlreadyExists(tag)) {
            removeTag(tag)
        } else if (!tagAlreadyExists(tag)) {
            addTag(tag)
        }
    }

    init {
        _marketInput.update {
            it.copy(storeId = farmMarketViewModel.getFarmId())
        }
    }
}

data class Market(
    val name: String = "",
    val image: String = "",
    val unit: String = "",
    val pricePerUnit: String = "",
    val volume: String = "",
    val storeId: String = "",
    val tags: List<String> = listOf(),
)

interface UploadMarketImageState {
    data object Loading: UploadMarketImageState
    data class Error(val msg: String?): UploadMarketImageState
    data object Success: UploadMarketImageState
}

interface AddFarmMarketState {
    data object Loading: AddFarmMarketState
    data object Success: AddFarmMarketState
    data class Error(val msg: String?): AddFarmMarketState
}