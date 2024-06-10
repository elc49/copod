package com.lomolo.giggy.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostingViewModel: ViewModel() {
    private val _postInput = MutableStateFlow(Posting())
    val postingUiState: StateFlow<Posting> = _postInput.asStateFlow()

    fun setPostText(text: String) {
        _postInput.update {
            it.copy(text = text)
        }
    }

    fun savePost(cb: () -> Unit = {}) {
        if (_postInput.value.text.isNotBlank()) {
            cb()
        }
    }

    fun discardPosting() {
        _postInput.value = Posting()
    }
}

data class Posting(
    val text: String = ""
)