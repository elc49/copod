package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.copod.GetLocalizedPostersQuery
import com.lomolo.copod.MainViewModel
import com.lomolo.copod.repository.IPosters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class ExploreViewModel(
    private val postersRepository: IPosters,
    mainViewModel: MainViewModel,
) : ViewModel() {
    private val _postersData: MutableStateFlow<List<GetLocalizedPostersQuery.GetLocalizedPoster>> = MutableStateFlow(
        listOf()
    )
    val posters: StateFlow<List<GetLocalizedPostersQuery.GetLocalizedPoster>> = _postersData.asStateFlow()

    var gettingPostersState: GettingPostersState by mutableStateOf(GettingPostersState.Success)
        private set

    private val localGps = mainViewModel.getValidDeviceGps()

    fun getLocalizedPosters() {
        if (gettingPostersState !is GettingPostersState.Loading) {
            gettingPostersState = GettingPostersState.Loading
            viewModelScope.launch {
                gettingPostersState = try {
                    val res = postersRepository.getLocalizedPosters(localGps).dataOrThrow()
                    _postersData.update { res.getLocalizedPosters }
                    GettingPostersState.Success
                } catch(e: IOException) {
                    e.printStackTrace()
                    GettingPostersState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getLocalizedPosters()
    }
}

interface GettingPostersState {
    data object Loading: GettingPostersState
    data object Success: GettingPostersState
    data class Error(val msg: String?): GettingPostersState
}