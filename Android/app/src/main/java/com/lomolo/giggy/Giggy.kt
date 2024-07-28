package com.lomolo.giggy

import android.app.Application
import com.lomolo.giggy.container.ApplicationContainer
import com.lomolo.giggy.container.IApplicationContainer

class GiggyApp: Application() {
    lateinit var container: IApplicationContainer

    override fun onCreate() {
        super.onCreate()
        container = ApplicationContainer(this)
    }
}