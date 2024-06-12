package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.model.DeviceDetails
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

class PostingViewModel(
    private val mainViewModel: MainViewModel,
    private val restApi: IGiggyRestApi,
): ViewModel() {
    private val _postInput = MutableStateFlow(Posting())
    val postingUiState: StateFlow<Posting> = _postInput.asStateFlow()

    var postImageUploadState: PostImageUploadState by mutableStateOf(PostImageUploadState.Success)
        private set

    fun setPostText(text: String) {
        _postInput.update {
            it.copy(text = text)
        }
    }

    // use ip gps if we don't have device gps permissions
    private fun postGps(deviceDetails: DeviceDetails): LatLng {
        if (deviceDetails.deviceGps.latitude == 0.0 && deviceDetails.deviceGps.longitude == 0.0) {
            // use ip gps
            val gps = deviceDetails.ipGps.split(",")
            return LatLng(gps[0].toDouble(), gps[1].toDouble())
        }
        return deviceDetails.deviceGps
    }

    fun savePost(cb: () -> Unit = {}) {
        if (_postInput.value.text.isNotBlank()) {
            // TODO save
            _postInput.update {
                it.copy(location = postGps(mainViewModel.deviceDetailsState.value))
            }
            cb()
        }
    }

    fun discardPosting() {
        _postInput.value = Posting()
    }

    val tags = listOf(
        "livestock",
        "animal feeds",
        "poultry",
        "farm inputs",
        "disease",
        "outbreak",
        "vaccine",
        "fruits",
        "trees",
        "seeds",
        "grain",
        "rabbit",
        "vegetables",
        "birds",
        "mushrooms",
        "nuts",
        "spices",
        "herbs",
        "coconut oil",
        "butter",
        "avocado oil",
    )

    private fun addTag(tag: String) {
        _postInput.update {
            val existingTags = it.tags.toMutableList()
            existingTags.add(tag)
            it.copy(tags = existingTags.toList())
        }
    }

    fun addPostTag(tag: String) {
        if (!tagAlreadySelected(tag)) {
            addTag(tag)
        } else if (tagAlreadySelected(tag)) {
            removePostTag(tag)
        }
    }

    fun tagAlreadySelected(tag: String): Boolean {
        return _postInput.value.tags.contains(tag)
    }

    private fun removePostTag(tag: String) {
        _postInput.update {
            val existTags = it.tags.toMutableList()
            existTags.remove(tag)
            it.copy(tags = existTags.toList())
        }
    }

    fun uploadImage(stream: InputStream) {
        postImageUploadState = PostImageUploadState.Loading
        val request = stream.readBytes().toRequestBody()
        val filePart = MultipartBody.Part.createFormData(
            "file",
            "post_image_${System.currentTimeMillis()}.jpg",
            request,
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = restApi.postUploader(filePart)
                _postInput.update {
                    it.copy(image = res.imageUri)
                }
                postImageUploadState = PostImageUploadState.Success
            } catch(e: IOException) {
                e.printStackTrace()
                postImageUploadState = PostImageUploadState.Error(e.localizedMessage)
            }
        }
    }
}

data class Posting(
    val text: String = "",
    val location: LatLng = LatLng(0.0, 0.0),
    val tags: List<String> = listOf(),
    val image: String = "",
)

interface PostImageUploadState {
    data object Loading: PostImageUploadState
    data class Error(val msg: String?): PostImageUploadState
    data object Success: PostImageUploadState
}