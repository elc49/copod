package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.lomolo.copod.GetFarmsBelongingToUserQuery
import com.lomolo.copod.MainViewModel
import com.lomolo.copod.network.ICopodRestApi
import com.lomolo.copod.repository.IFarm
import com.lomolo.copod.type.GpsInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.toImmutableList
import java.io.IOException
import java.io.InputStream

class CreateFarmViewModel(
    private val apolloStore: ApolloStore,
    private val giggyRestApi: ICopodRestApi,
    private val farmRepository: IFarm,
    mainViewModel: MainViewModel,
): ViewModel() {
    private val validGps = mainViewModel.getValidDeviceGps()
    private val _farmInput = MutableStateFlow(Farm())
    val farmUiState: StateFlow<Farm> = _farmInput.asStateFlow()

    var farmImageUploadState: FarmImageUploadState by mutableStateOf(FarmImageUploadState.Success)
        private set

    var createFarmState: CreateFarmState by mutableStateOf(CreateFarmState.Success)
        private set

    fun setName(name: String) {
        _farmInput.update {
            it.copy(name = name)
        }
    }

    fun setDateStarted(date: String) {
        _farmInput.update {
            it.copy(dateStarted = date)
        }
    }

    fun setAbout(about: String) {
        _farmInput.update {
            it.copy(about = about)
        }
    }

    private fun validFarmInput(uiState: Farm): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank() && dateStarted.isNotBlank() && about.isNotBlank()
        }
    }

    fun discardFarmInput() {
        _farmInput.value = Farm()
    }

    fun saveFarm(cb: () -> Unit = {}) {
        if (validFarmInput(_farmInput.value) &&
            createFarmState !is CreateFarmState.Loading &&
            farmImageUploadState is FarmImageUploadState.Success
        ) {
            createFarmState = CreateFarmState.Loading
            viewModelScope.launch {
                createFarmState = try {
                    _farmInput.update {
                        it.copy(location = GpsInput(validGps.latitude, validGps.longitude))
                    }
                    val res = farmRepository.createFarm(_farmInput.value).dataOrThrow()
                    try {
                        val updatedCachedData = apolloStore.readOperation(
                            GetFarmsBelongingToUserQuery()
                        )
                            .getFarmsBelongingToUser
                            .toMutableList()
                            .apply {
                                add(
                                    GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser(
                                        res.createFarm.id,
                                        res.createFarm.name,
                                        res.createFarm.about,
                                        res.createFarm.dateStarted,
                                        res.createFarm.thumbnail,
                                    )
                                )
                            }
                            .toImmutableList()
                        apolloStore.writeOperation(
                            GetFarmsBelongingToUserQuery(),
                            GetFarmsBelongingToUserQuery.Data(updatedCachedData)
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    CreateFarmState.Success.also {
                        cb()
                    }
                } catch(e: IOException) {
                    e.printStackTrace()
                    CreateFarmState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun uploadImage(stream: InputStream) {
        if (farmImageUploadState !is FarmImageUploadState.Loading) {
            _farmInput.update { it.copy(image = "") }
            farmImageUploadState = FarmImageUploadState.Loading
            val request = stream.readBytes().toRequestBody()
            val file = MultipartBody.Part.createFormData(
                "file",
                "farm_image_${System.currentTimeMillis()}.jpg",
                request,
            )
            viewModelScope.launch(Dispatchers.IO) {
                farmImageUploadState = try {
                    val res = giggyRestApi.imageUpload(file)
                    _farmInput.update {
                        it.copy(image = res.imageUri)
                    }
                    FarmImageUploadState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    FarmImageUploadState.Error(e.localizedMessage)
                }
            }
        }
    }
}

data class Farm(
    val name: String = "",
    val image: String = "",
    val about: String = "",
    val location: GpsInput = GpsInput(0.0, 0.0),
    val dateStarted: String = ""
)

interface FarmImageUploadState {
    data object Loading: FarmImageUploadState
    data object Success: FarmImageUploadState
    data class Error(val msg: String?): FarmImageUploadState
}

interface CreateFarmState {
    data object Loading: CreateFarmState
    data object Success: CreateFarmState
    data class Error(val msg: String?): CreateFarmState
}