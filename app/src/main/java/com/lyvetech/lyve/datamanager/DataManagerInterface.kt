package com.lyvetech.lyve.datamanager

import androidx.lifecycle.LiveData
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User

interface DataManagerInterface {
    suspend fun createUser(user: User)
    suspend fun createActivity(activity: Activity, user: User)
    suspend fun updateActivity(activity: Activity, user: User)
    fun getCurrentUser(): LiveData<User>
    fun getActivities(): LiveData<List<Activity?>?>
}