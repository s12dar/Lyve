package com.lyvetech.lyve

import android.app.Application
import com.lyvetech.lyve.datamodels.User

class LyveApplication : Application() {

    companion object {
        var mInstance: LyveApplication = LyveApplication()
    }

    var currentUser: User? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}