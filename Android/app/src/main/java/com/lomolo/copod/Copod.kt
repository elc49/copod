package com.lomolo.copod

import android.app.Application
import com.lomolo.copod.container.ApplicationContainer
import com.lomolo.copod.container.IApplicationContainer

class CopodApp: Application() {
    lateinit var container: IApplicationContainer

    override fun onCreate() {
        super.onCreate()
        container = ApplicationContainer(this)
    }
}