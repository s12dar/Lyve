package com.lyvetech.lyve.datamanager

import com.lyvetech.lyve.datamanager.DataListener
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User

interface DataManagerInterface {
    fun createUser(user: User, listener: DataListener<Boolean>)
    fun getCurrentUser(listener: DataListener<User>)
    fun getActivities(listener: DataListener<List<Activity?>>)
}