package com.lomolo.giggy.compose.screens

import androidx.lifecycle.ViewModel
import com.lomolo.giggy.MainViewModel
import com.lomolo.giggy.repository.IPosters

class DashboardViewModel(
    private val postersRepository: IPosters,
    mainViewModel: MainViewModel,
): ViewModel() {
}