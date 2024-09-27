package com.lomolo.copod

import android.app.Application
import co.paystack.android.PaystackSdk
import com.lomolo.copod.container.ApplicationContainer
import com.lomolo.copod.container.IApplicationContainer

class CopodApp: Application() {
    lateinit var container: IApplicationContainer

    override fun onCreate() {
        super.onCreate()
        PaystackSdk.initialize(this)
        PaystackSdk.setPublicKey(BuildConfig.paystackpublic_key)
        container = ApplicationContainer(this)
    }
}