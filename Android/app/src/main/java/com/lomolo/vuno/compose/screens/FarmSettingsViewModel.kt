package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.vuno.GetFarmByIdQuery
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.network.IVunoRestApi
import com.lomolo.vuno.type.UpdateFarmDetailsInput
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

class FarmSettingsViewModel(
    private val vunoGraphqlApi: IVunoGraphqlApi,
    private val vunoRestApi: IVunoRestApi,
    savedStateHandle: SavedStateHandle,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    private val farmId: String =
        checkNotNull(savedStateHandle[FarmSettingsScreenDestination.farmIdArg])

    var gettingFarmDetails: GettingFarmDetails by mutableStateOf(GettingFarmDetails.Success)
        private set
    private val _farm: MutableStateFlow<GetFarmByIdQuery.GetFarmById> = MutableStateFlow(
        GetFarmByIdQuery.GetFarmById(
            "",
            "",
            "",
            "",
            "",
        )
    )
    val farmDetails: StateFlow<GetFarmByIdQuery.GetFarmById> = _farm.asStateFlow()

    // Update about optimistically(in cache)
    fun setAbout(about: String) {
        _farm.update { it.copy(about = about) }
    }

    // Update farm thumbnail optimistically(in cache)
    private fun setThumbnail(thumbnail: String) {
        _farm.update { it.copy(thumbnail = thumbnail) }
    }

    var farmImageUploadState: FarmImageUploadState by mutableStateOf(FarmImageUploadState.Success)
        private set

    fun uploadImage(stream: InputStream) {
        if (farmImageUploadState !is FarmImageUploadState.Loading) {
            _farm.update { it.copy(thumbnail = "") }
            farmImageUploadState = FarmImageUploadState.Loading
            val request = stream.readBytes().toRequestBody()
            val file = MultipartBody.Part.createFormData(
                "file",
                "farm_image_${System.currentTimeMillis()}.jpg",
                request,
            )
            viewModelScope.launch(Dispatchers.IO) {
                farmImageUploadState = try {
                    val res = vunoRestApi.imageUpload(file)
                    setThumbnail(res.imageUri)
                    FarmImageUploadState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    FarmImageUploadState.Error(e.localizedMessage)
                }
            }
        }
    }

    var savingFarmDetails: SaveFarmDetailsState by mutableStateOf(SaveFarmDetailsState.Success)
        private set

    fun saveFarmDetails() {
        if (savingFarmDetails !is SaveFarmDetailsState.Loading) {
            savingFarmDetails = SaveFarmDetailsState.Loading
            viewModelScope.launch {
                savingFarmDetails = try {
                    vunoGraphqlApi.updateFarmDetails(
                        UpdateFarmDetailsInput(
                            farmId,
                            _farm.value.about,
                            _farm.value.thumbnail,
                        )
                    )
                    SaveFarmDetailsState.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    SaveFarmDetailsState.Error(e.localizedMessage)
                }
            }
        }
    }

    private fun getFarmDetails() {
        if (gettingFarmDetails !is GettingFarmDetails.Loading) {
            gettingFarmDetails = GettingFarmDetails.Loading
            viewModelScope.launch {
                gettingFarmDetails = try {
                    val c = apolloStore.readOperation(GetFarmByIdQuery(farmId)).getFarmById
                    _farm.value = c
                    GettingFarmDetails.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingFarmDetails.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getFarmDetails()
    }

}

interface GettingFarmDetails {
    data object Loading : GettingFarmDetails
    data object Success : GettingFarmDetails
    data class Error(val msg: String?) : GettingFarmDetails
}

interface SaveFarmDetailsState {
    data object Loading: SaveFarmDetailsState
    data object Success: SaveFarmDetailsState
    data class Error(val msg: String?): SaveFarmDetailsState
}