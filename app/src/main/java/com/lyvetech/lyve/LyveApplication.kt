package com.lyvetech.lyve

import android.app.Application
import com.lyvetech.lyve.datamodels.User

class LyveApplication : Application() {

    companion object {
        lateinit var mInstance: LyveApplication
    }

    var currentUser: User? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}