package com.lomolo.giggy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.network.IGiggyRestApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class MainViewModel(
    private val giggyRestApiService: IGiggyRestApi
): ViewModel() {
    private val _deviceDetails: MutableStateFlow<DeviceDetails> = MutableStateFlow(DeviceDetails())
    val deviceDetailsState = _deviceDetails.asStateFlow()
    var settingDeviceDetailsState: SettingDeviceDetails by mutableStateOf(SettingDeviceDetails.Success)
        private set

    fun getDeviceDetails() {
        viewModelScope.launch {
            settingDeviceDetailsState = SettingDeviceDetails.Loading
            try {
                val res = giggyRestApiService.ip()
                _deviceDetails.update {
                    it.copy(
                        countryCode = res.countryCode,
                        countryFlag = res.countryFlag,
                        currency = res.currency,
                        callingCode = res.callingCode,
                        ipGps = res.ipGps,
                    )
                }
                settingDeviceDetailsState = SettingDeviceDetails.Success
            } catch(e: IOException) {
                settingDeviceDetailsState = SettingDeviceDetails.Error(e.localizedMessage)
                e.printStackTrace()
            }
        }
    }

    fun setDeviceGps(gps: LatLng) {
        if (gps.longitude != 0.0 && gps.latitude != 0.0) {
            _deviceDetails.update {
                it.copy(deviceGps = gps)
            }
        }
    }

    init {
        getDeviceDetails()
    }
}

interface SettingDeviceDetails {
    object Loading: SettingDeviceDetails
    data class Error(val msg: String?): SettingDeviceDetails
    object Success: SettingDeviceDetails
}