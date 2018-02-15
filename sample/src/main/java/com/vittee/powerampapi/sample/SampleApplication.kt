package com.vittee.powerampapi.sample

import android.app.Application
import android.os.StrictMode

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}