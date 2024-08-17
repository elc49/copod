package com.lomolo.vuno.compose.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class FarmSettingsViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val farmId: String =
        checkNotNull(savedStateHandle[FarmSettingsScreenDestination.farmIdArg])
}