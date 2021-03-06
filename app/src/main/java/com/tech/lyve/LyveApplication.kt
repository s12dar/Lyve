package com.tech.lyve

import android.app.Application
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LyveApplication : Application() {

    companion object {
        var mInstance: LyveApplication = LyveApplication()
    }

    var currentUser: User? = null
    var event: Event? = null
    var allActivities = mutableListOf<User>()
    var allUsers = mutableListOf<User>()

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}