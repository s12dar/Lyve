package com.lyvetech.lyve.repositories

import android.net.Uri
import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Resource
import com.lyvetech.lyve.utils.SimpleResource

interface LyveRepository {

    suspend fun createUser(user: User): SimpleResource
    suspend fun loginUser(email: String, pass: String): SimpleResource
    suspend fun createActivity(event: Event, user: User): SimpleResource
    suspend fun updateUser(user: User): SimpleResource
    suspend fun updateActivity(event: Event, user: User): SimpleResource
    suspend fun getCurrentUser(): Resource<User?>
    suspend fun getActivities(): Resource<List<Event>?>
    suspend fun getUsers(): Resource<List<User>?>
    suspend fun getSearchedUsers(searchQuery: String): Resource<List<User>?>
    suspend fun getSearchedActivities(searchQuery: String): Resource<List<Event>?>
    suspend fun getFollowings(user: User): Resource<List<User>?>
    suspend fun getFollowers(user: User): Resource<List<User>?>
    suspend fun getFollowingActivities(user: User): Resource<List<Event>?>
    suspend fun getUploadImgUriFromFirebaseStorage(imgUri: Uri): Resource<Uri>
    suspend fun getEventBelongingToCurrentUser(user: User): Resource<List<Event>>
}