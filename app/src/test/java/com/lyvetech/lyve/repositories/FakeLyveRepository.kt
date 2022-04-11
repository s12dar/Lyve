package com.lyvetech.lyve.repositories

//class FakeLyveRepository : LyveRepository {
//
//    private val users = mutableListOf<User>()
//
//    private val activities = mutableListOf<Activity>()
//
//    private val currentUser = MutableLiveData<User>()
//
//    private val allUsers = MutableLiveData<List<User>>(users)
//
//    private val followings = MutableLiveData<List<User>>(users)
//
//    private val followers = MutableLiveData<List<User>>(users)
//
//    private val searchedUsers = MutableLiveData<List<User>>(users)
//
//    private val allActivities = MutableLiveData<List<Activity>>(activities)
//
//    private val followingActivities = MutableLiveData<List<Activity>>(activities)
//
//    private val searchedActivities = MutableLiveData<List<Activity>>(activities)
//    override suspend fun createUser(user: User): SimpleResource {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun createActivity(activity: Activity, user: User): SimpleResource {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun updateUser(user: User): SimpleResource {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun updateActivity(activity: Activity, user: User): SimpleResource {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getCurrentUser(): Resource<User> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getActivities(): Resource<List<Activity>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getUsers(): Resource<List<User>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getSearchedUsers(searchQuery: String): Resource<List<User>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getSearchedActivities(searchQuery: String): Resource<List<Activity>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getFollowings(user: User): Resource<List<User>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getFollowers(user: User): Resource<List<User>> {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getFollowingActivities(user: User): Resource<List<Activity>> {
//        TODO("Not yet implemented")
//    }
//}