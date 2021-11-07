package com.lyvetech.lyve.application

import android.app.Application
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User

class LyveApplication : Application() {

    companion object {
        var mInstance: LyveApplication = LyveApplication()
    }

    var currentUser: User? = null
    var activity: Activity? = null
    var allActivities: List<Activity?>? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}