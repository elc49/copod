package com.lomolo.giggy.container

import android.content.Context

interface IApplicationContainer{}

class ApplicationContainer(
    private val context: Context
): IApplicationContainer {
}