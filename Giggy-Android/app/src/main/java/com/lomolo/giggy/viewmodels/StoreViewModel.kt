package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetStoresBelongingToUserQuery
import com.lomolo.giggy.network.IGiggyRestApi
import com.lomolo.giggy.repository.IStore
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
    private val storeRepository: IStore,
): ViewModel() {
    private val _storeInput = MutableStateFlow(Store())
    val storeUiState: StateFlow<Store> = _storeInput.asStateFlow()

    var storeImageUploadState: StoreImageUploadState by mutableStateOf(StoreImageUploadState.Success)
        private set

    var getStoresBelongingToUserState: GetStoresBelongingToUserState by mutableStateOf(GetStoresBelongingToUserState.Success(null))
        private set

    var createStoreState: CreateStoreState by mutableStateOf(CreateStoreState.Success)
        private set

    fun setName(name: String) {
        _storeInput.update {
            it.copy(name = name)
        }
    }

    fun uploadImage(stream: InputStream) {
        _storeInput.update { it.copy(image = "") }
        storeImageUploadState = StoreImageUploadState.Loading
        val request = stream.readBytes().toRequestBody()
        val file = MultipartBody.Part.createFormData(
            "file",
            "store_image_${System.currentTimeMillis()}.jpg",
            request,
        )
        viewModelScope.launch(Dispatchers.IO) {
            storeImageUploadState = try {
                val res = giggyRestApi.imageUpload(file)
                _storeInput.update {
                    it.copy(image = res.imageUri)
                }
                StoreImageUploadState.Success
            } catch(e: IOException) {
                e.printStackTrace()
                StoreImageUploadState.Error(e.localizedMessage)
            }
        }
    }

    fun saveStore(cb: () -> Unit = {}) {
        if (_storeInput.value.name.isNotBlank() && _storeInput.value.image.isNotBlank()) {
            createStoreState = CreateStoreState.Loading
            viewModelScope.launch {
                createStoreState = try {
                    storeRepository.createStore(_storeInput.value)
                    CreateStoreState.Success.also {
                        cb()
                    }
                } catch(e: IOException) {
                    e.printStackTrace()
                    CreateStoreState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun getStoresBelongingToUser() = viewModelScope.launch {
        getStoresBelongingToUserState = GetStoresBelongingToUserState.Loading
        getStoresBelongingToUserState = try {
            val res = storeRepository.getStoresBelongingToUser().dataOrThrow()
            GetStoresBelongingToUserState.Success(res.getStoresBelongingToUser)
        } catch(e: IOException) {
            e.printStackTrace()
            GetStoresBelongingToUserState.Error(e.localizedMessage)
        }
    }

    fun discardStoreInput() {
        _storeInput.value = Store()
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

interface GetStoresBelongingToUserState {
    data object Loading: GetStoresBelongingToUserState
    data class Error(val msg: String?): GetStoresBelongingToUserState
    data class Success(val success: List<GetStoresBelongingToUserQuery.GetStoresBelongingToUser>?): GetStoresBelongingToUserState
}

interface CreateStoreState {
    data object Loading: CreateStoreState
    data object Success: CreateStoreState
    data class Error(val msg: String?): CreateStoreState
}