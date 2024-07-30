package com.lomolo.vuno

import android.app.Application
import com.lomolo.vuno.container.ApplicationContainer
import com.lomolo.vuno.container.IApplicationContainer

class VunoApp: Application() {
    lateinit var container: IApplicationContainer

    override fun onCreate() {
        super.onCreate()
        container = ApplicationContainer(this)
    }
}