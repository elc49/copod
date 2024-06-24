package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.network.IGiggyRestApi
import com.lomolo.giggy.repository.IFarm
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

class FarmViewModel(
    private val giggyRestApi: IGiggyRestApi,
    private val farmRepository: IFarm,
): ViewModel() {
    private val _farmInput = MutableStateFlow(Farm())
    val farmUiState: StateFlow<Farm> = _farmInput.asStateFlow()

    var farmImageUploadState: FarmImageUploadState by mutableStateOf(FarmImageUploadState.Success)
        private set

    var getFarmsBelongingToUserState: GetFarmsBelongingToUserState by mutableStateOf(GetFarmsBelongingToUserState.Success(null))
        private set

    var createFarmState: CreateFarmState by mutableStateOf(CreateFarmState.Success)
        private set

    fun setName(name: String) {
        _farmInput.update {
            it.copy(name = name)
        }
    }

    fun uploadImage(stream: InputStream) {
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
            } catch(e: IOException) {
                e.printStackTrace()
                FarmImageUploadState.Error(e.localizedMessage)
            }
        }
    }

    private fun validFarmInput(uiState: Farm): Boolean {
        return with(uiState) {
            name.isNotBlank() && image.isNotBlank()
        }
    }

    fun saveFarm(cb: () -> Unit = {}) {
        if (validFarmInput(_farmInput.value) &&
            createFarmState !is CreateFarmState.Loading &&
            farmImageUploadState is FarmImageUploadState.Success) {
            createFarmState = CreateFarmState.Loading
            viewModelScope.launch {
                createFarmState = try {
                    farmRepository.createFarm(_farmInput.value)
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

    fun getFarmsBelongingToUser() = viewModelScope.launch {
        getFarmsBelongingToUserState = GetFarmsBelongingToUserState.Loading
        getFarmsBelongingToUserState = try {
            val res = farmRepository.getFarmsBelongingToUser().dataOrThrow()
            GetFarmsBelongingToUserState.Success(res.getFarmsBelongingToUser)
        } catch(e: IOException) {
            e.printStackTrace()
            GetFarmsBelongingToUserState.Error(e.localizedMessage)
        }
    }

    fun discardFarmInput() {
        _farmInput.value = Farm()
    }
}

data class Farm(
    val name: String = "",
    val image: String = "",
)

interface FarmImageUploadState {
    data object Loading: FarmImageUploadState
    data object Success: FarmImageUploadState
    data class Error(val msg: String?): FarmImageUploadState
}

interface GetFarmsBelongingToUserState {
    data object Loading: GetFarmsBelongingToUserState
    data class Error(val msg: String?): GetFarmsBelongingToUserState
    data class Success(val success: List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>?): GetFarmsBelongingToUserState
}

interface CreateFarmState {
    data object Loading: CreateFarmState
    data object Success: CreateFarmState
    data class Error(val msg: String?): CreateFarmState
}