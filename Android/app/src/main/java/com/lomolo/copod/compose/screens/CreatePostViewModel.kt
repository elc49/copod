package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.gms.maps.model.LatLng
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.network.ICopodRestApi
import com.lomolo.copod.type.GpsInput
import com.lomolo.copod.type.NewPostInput
import com.lomolo.copod.MainViewModel
import com.lomolo.copod.data.Data
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

class CreatePostViewModel(
    private val mainViewModel: MainViewModel,
    private val giggyRestApi: ICopodRestApi,
    private val giggyGraphqlApi: ICopodGraphqlApi,
): ViewModel() {
    private val _postInput = MutableStateFlow(Posting())
    val postingUiState: StateFlow<Posting> = _postInput.asStateFlow()

    var postImageUploadState: PostImageUploadState by mutableStateOf(PostImageUploadState.Success)
        private set
    var submittingPostState: SubmittingPost by mutableStateOf(SubmittingPost.Success)
        private set

    fun setPostText(text: String) {
        _postInput.update {
            it.copy(text = text)
        }
    }

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
        if (postImageUploadState !is PostImageUploadState.Loading) {
            _postInput.update { it.copy(image = "") }
            postImageUploadState = PostImageUploadState.Loading
            val request = stream.readBytes().toRequestBody()
            val filePart = MultipartBody.Part.createFormData(
                "file",
                "post_image_${System.currentTimeMillis()}.jpg",
                request,
            )
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val res = giggyRestApi.imageUpload(filePart)
                    _postInput.update {
                        it.copy(image = res.imageUri)
                    }
                    postImageUploadState = PostImageUploadState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    postImageUploadState = PostImageUploadState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun savePost(userId: String, cb: () -> Unit = {}) {
        if (_postInput.value.text.isNotBlank() &&
            postImageUploadState is PostImageUploadState.Success &&
            submittingPostState !is SubmittingPost.Loading) {
            submittingPostState = SubmittingPost.Loading
            // TODO save
            _postInput.update {
                it.copy(location = mainViewModel.getValidDeviceGps())
            }
            viewModelScope.launch {
                try {
                    giggyGraphqlApi.createPost(
                        NewPostInput(
                            _postInput.value.text,
                            _postInput.value.image,
                            _postInput.value.tags,
                            userId,
                            GpsInput(_postInput.value.location.latitude, _postInput.value.location.longitude),
                        )
                    ).dataOrThrow()
                    submittingPostState = SubmittingPost.Success.also {
                        cb()
                    }
                } catch(e: ApolloException) {
                    e.printStackTrace()
                    submittingPostState = SubmittingPost.Error(e.localizedMessage)
                }
            }
        }
    }

    fun discardPosting() {
        _postInput.value = Posting()
    }

    val tags = Data.marketTags
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

interface SubmittingPost {
    data object Loading: SubmittingPost
    data class Error(val msg: String?): SubmittingPost
    data object Success: SubmittingPost
}
