package com.lomolo.giggy.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.model.DeviceDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostingViewModel(
    private val mainViewModel: MainViewModel,
): ViewModel() {
    private val _postInput = MutableStateFlow(Posting())
    val postingUiState: StateFlow<Posting> = _postInput.asStateFlow()

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
        _postInput.update {
            it.copy(location = postGps(mainViewModel.deviceDetailsState.value))
        }
        if (_postInput.value.text.isNotBlank()) {
            cb()
        }
    }

    fun discardPosting() {
        _postInput.value = Posting()
    }
}

data class Posting(
    val text: String = "",
    val location: LatLng = LatLng(0.0, 0.0),
)