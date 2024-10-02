package com.lomolo.copod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.network.ICopodRestApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class MainViewModel(
    private val giggyRestApiService: ICopodRestApi,
): ViewModel() {
    private val _deviceDetails: MutableStateFlow<DeviceDetails> = MutableStateFlow(DeviceDetails())
    val deviceDetailsState = _deviceDetails.asStateFlow()
    var settingDeviceDetailsState: SettingDeviceDetails by mutableStateOf(SettingDeviceDetails.Success)
        private set

    fun getDeviceDetails() {
        if (settingDeviceDetailsState !is SettingDeviceDetails.Loading) {
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
                            posterRightsFee = res.posterRightsFee,
                            farmingRightsFee = res.farmingRightsFee,
                            languages = res.languages.split(",")[0],
                            // TODO look into loading this using currency exchange rates
                            farmingFeesByCurrency = mapOf("KES" to 2000, "USD" to 20),
                        )
                    }
                    settingDeviceDetailsState = SettingDeviceDetails.Success
                } catch (e: IOException) {
                    settingDeviceDetailsState = SettingDeviceDetails.Error(e.localizedMessage)
                    e.printStackTrace()
                }
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

    fun getValidDeviceGps(): LatLng {
        val deviceGps = _deviceDetails.value.deviceGps
        val ipGps = _deviceDetails.value.ipGps
        if (deviceGps.latitude == 0.0 && deviceGps.longitude == 0.0) {
            val ipLocation = ipGps.split(",")
            return LatLng(ipLocation[0].toDouble(), ipLocation[1].toDouble())
        }

        return deviceGps
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