package com.lyvetech.lyve.repositories

import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Resource
import com.lyvetech.lyve.utils.SimpleResource

interface LyveRepository {

    suspend fun createUser(user: User): SimpleResource
    suspend fun createActivity(activity: Activity, user: User): SimpleResource
    suspend fun updateUser(user: User): SimpleResource
    suspend fun updateActivity(activity: Activity, user: User): SimpleResource
    suspend fun getCurrentUser(): Resource<User?>
    suspend fun getActivities(): Resource<List<Activity>?>
    suspend fun getUsers(): Resource<List<User>?>
    suspend fun getSearchedUsers(searchQuery: String): Resource<List<User>?>
    suspend fun getSearchedActivities(searchQuery: String): Resource<List<Activity>?>
    suspend fun getFollowings(user: User): Resource<List<User>?>
    suspend fun getFollowers(user: User): Resource<List<User>?>
    suspend fun getFollowingActivities(user: User): Resource<List<Activity>?>
}