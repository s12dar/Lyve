package com.lyvetech.lyve

import android.app.Application
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LyveApplication : Application() {

    companion object {
        var mInstance: LyveApplication = LyveApplication()
    }

    var currentUser: User? = null
    var activity: Activity? = null
    var allActivities: MutableList<Activity?>? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}