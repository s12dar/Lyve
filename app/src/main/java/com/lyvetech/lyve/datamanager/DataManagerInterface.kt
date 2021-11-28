package com.lyvetech.lyve.datamanager

import com.google.firebase.auth.FirebaseUser
import com.lyvetech.lyve.listeners.DataListener
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User

interface DataManagerInterface {
    fun createUser(user: User, listener: DataListener<Boolean>)
    fun getCurrentUser(listener: DataListener<User>)
    fun getActivities(listener: DataListener<MutableList<Activity?>>)
    fun createActivity(activity: Activity, user: FirebaseUser, listener: DataListener<Boolean>)
    fun updateActivity(activity: Activity, user: FirebaseUser, listener: DataListener<Boolean>)
}