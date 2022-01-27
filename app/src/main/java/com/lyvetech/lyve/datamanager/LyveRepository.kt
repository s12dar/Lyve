package com.lyvetech.lyve.datamanager

import androidx.lifecycle.LiveData
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User

interface LyveRepository {
    suspend fun createUser(user: User)
    suspend fun createActivity(activity: Activity, user: User)
    suspend fun updateUser(user: User)
    suspend fun updateActivity(activity: Activity, user: User)
    fun getCurrentUser(): LiveData<User>
    fun getActivities(): LiveData<List<Activity>>
    fun getUsers(): LiveData<List<User>>
    fun getSearchedUsers(searchQuery: String): LiveData<List<User>>
    fun getSearchedActivities(searchQuery: String): LiveData<List<Activity>>
    fun getFollowers(user: User): LiveData<List<User>>
    fun getFollowings(user: User): LiveData<List<User>>
    fun getFollowingActivities(user: User): LiveData<List<Activity>>
}