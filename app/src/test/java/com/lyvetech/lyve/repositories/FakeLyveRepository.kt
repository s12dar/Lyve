package com.lyvetech.lyve.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User

class FakeLyveRepository : LyveRepository {

    private val users = mutableListOf<User>()

    private val activities = mutableListOf<Activity>()

    private val currentUser = MutableLiveData<User>()

    private val allUsers = MutableLiveData<List<User>>(users)

    private val followings = MutableLiveData<List<User>>(users)

    private val followers = MutableLiveData<List<User>>(users)

    private val searchedUsers = MutableLiveData<List<User>>(users)

    private val allActivities = MutableLiveData<List<Activity>>(activities)

    private val followingActivities = MutableLiveData<List<Activity>>(activities)

    private val searchedActivities = MutableLiveData<List<Activity>>(activities)

    private fun refreshAllUsersLiveData() {
        allUsers.postValue(users)
    }

    private fun refreshAllActivitiesLiveData() {
        allActivities.postValue(activities)
    }

    override suspend fun createUser(user: User) {
        users.add(user)
        refreshAllUsersLiveData()

    }

    override suspend fun createActivity(activity: Activity, user: User) {
        activities.add(activity)
        refreshAllActivitiesLiveData()
    }

    override suspend fun updateUser(user: User) {
//        user.isVerified = true;
//        val updatedUser =
    }

    override suspend fun updateActivity(activity: Activity, user: User) {

    }

    override fun getCurrentUser(): LiveData<User> {
        return currentUser
    }

    override fun getActivities(): LiveData<List<Activity>> {
        return allActivities
    }

    override fun getUsers(): LiveData<List<User>> {
        return allUsers
    }

    override fun getSearchedUsers(searchQuery: String): LiveData<List<User>> {
        return searchedUsers
    }

    override fun getSearchedActivities(searchQuery: String): LiveData<List<Activity>> {
        return searchedActivities
    }

    override fun getFollowers(user: User): LiveData<List<User>> {
        return followers
    }

    override fun getFollowings(user: User): LiveData<List<User>> {
        return followings
    }

    override fun getFollowingActivities(user: User): LiveData<List<Activity>> {
        return followingActivities
    }
}