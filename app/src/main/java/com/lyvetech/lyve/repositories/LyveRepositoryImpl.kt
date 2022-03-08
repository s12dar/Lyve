package com.lyvetech.lyve.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Constants.COLLECTION_ACTIVITIES
import com.lyvetech.lyve.utils.Constants.COLLECTION_USER
import javax.inject.Inject

class LyveRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firebaseUser: FirebaseUser
) : LyveRepository {

    private val TAG = LyveRepositoryImpl::class.qualifiedName

    companion object {
        const val AUTHENTICATION = "Authentication"
        const val INVALID_USER = "Invalid User"
    }

    override suspend fun createUser(user: User) {
        firebaseUser.let {
            val userBatch: WriteBatch = FirebaseFirestore.getInstance().batch()
            val userDocRef: DocumentReference =
                FirebaseFirestore.getInstance().collection(COLLECTION_USER).document(user.uid)

            userBatch.set(userDocRef, user.toMap())
            userBatch.commit().addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        Log.d(TAG, "User created successfully")
                    } else {
                        Log.e(TAG, "User couldn't be created ${task.exception}")
                    }
                }
            }
        }
    }

    override fun getCurrentUser(): LiveData<User> {
        val liveDataUser = MutableLiveData<User>()

        firebaseUser.let {
            val userDocRef: DocumentReference =
                FirebaseFirestore.getInstance().collection(COLLECTION_USER)
                    .document(it.uid)

            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val userDoc: DocumentSnapshot = transaction.get(userDocRef)

                val currentUser: User? = userDoc.toObject(User::class.java)
                currentUser?.let { user ->

                    if (user.email != it.email) {
                        user.email = it.email.toString()
                    }
                    if (user.email.isEmpty()) {
                        user.email = it.email.toString()
                    }

                    transaction.set(userDocRef, user.toMap(), SetOptions.merge())
                    liveDataUser.postValue(user)
                } ?: run {
                    null
                }
            }.addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        Log.d(TAG, "User data retrieved successfully")
                    } else {
                        Log.e(TAG, "User data couldn't be retrieved: ${task.exception}")
                    }
                }
            }
        }
        return liveDataUser
    }

    override suspend fun createActivity(
        activity: Activity,
        user: User
    ) {
        firebaseUser.let {
            val activityBatch: WriteBatch = FirebaseFirestore.getInstance().batch()
            val activityDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(
                COLLECTION_ACTIVITIES
            ).document(activity.aid)
            val subActivityDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(
                COLLECTION_USER
            ).document(user.uid).collection(COLLECTION_ACTIVITIES).document(activity.aid)

            activityBatch.set(activityDocRef, activity.toMap())
            activityBatch.set(subActivityDocRef, activity.toUserActivityMap())

            activityBatch.commit().addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        Log.d(TAG, "Activity created successfully")
                    } else {
                        Log.e(TAG, "Activity couldn't be created: ${task.exception}")
                    }
                }
            }
        }
    }

    override suspend fun updateUser(user: User) {
        firebaseUser.let {
            val userBatch = FirebaseFirestore.getInstance().batch()
            val userDocRef = FirebaseFirestore.getInstance().collection(
                COLLECTION_USER
            ).document(user.uid)

            userBatch.update(userDocRef, user.toMap())
            userBatch.commit().addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        Log.d(TAG, "User updated successfully")
                    } else {
                        Log.e(TAG, "User couldn't be updated: ${task.exception}")
                    }
                }
            }
        }
    }

    override suspend fun updateActivity(
        activity: Activity,
        user: User,
    ) {
        firebaseUser.let {
            val activityBatch = FirebaseFirestore.getInstance().batch()
            val activityDocRef = FirebaseFirestore.getInstance().collection(
                COLLECTION_ACTIVITIES
            ).document(activity.aid)

            activityBatch.update(activityDocRef, activity.toMap())
            activityBatch.commit().addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        Log.d(TAG, "Activity updated successfully")
                    } else {
                        Log.e(TAG, "Activity couldn't be updated: ${task.exception}")
                    }
                }
            }
        }
    }

    override fun getUsers(): LiveData<List<User>> {
        val liveDataUsers = MutableLiveData<List<User>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_USER)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Users are null, retrying...")
                            return@addOnCompleteListener
                        }

                        val usersList = mutableListOf<User>()
                        for (document in querySnapshot) {
                            usersList.add(document.toObject(User::class.java))
                        }
                        liveDataUsers.value = usersList

                        Log.d(TAG, "Users retrieved successfully")
                    } else {
                        Log.e(TAG, "Users couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return liveDataUsers
    }

    override fun getActivities(): LiveData<List<Activity>> {
        val liveDataActivities = MutableLiveData<List<Activity>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Activities are null, retrying...")
                            return@addOnCompleteListener
                        }

                        val activitiesList = mutableListOf<Activity>()
                        for (document in querySnapshot) {
                            if ((document.toObject(Activity::class.java).acCreatedByID !=
                                        firebaseUser.uid)
                            ) {
                                activitiesList.add(document.toObject(Activity::class.java))
                            }
                        }
                        liveDataActivities.value = activitiesList

                        Log.d(TAG, "Activities retrieved successfully")
                    } else {
                        Log.e(TAG, "Activities couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return liveDataActivities
    }

    override fun getSearchedActivities(searchQuery: String): LiveData<List<Activity>> {
        val liveDataActivities = MutableLiveData<List<Activity>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Activities are null, retrying...")
                            return@addOnCompleteListener
                        }
                        val activitiesList = mutableListOf<Activity>()
                        for (document in querySnapshot) {
                            if (searchQuery.isNotEmpty() && document.toObject(Activity::class.java)
                                    .acTitle.lowercase().contains(searchQuery.lowercase())
                            ) {
                                activitiesList.add(document.toObject(Activity::class.java))
                            }
                        }
                        liveDataActivities.value = activitiesList

                        Log.d(TAG, "Activities retrieved successfully")
                    } else {
                        Log.e(TAG, "Activities couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return liveDataActivities
    }

    override fun getFollowers(user: User): LiveData<List<User>> {
        val followers = MutableLiveData<List<User>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_USER)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Users are null, retrying...")
                            return@addOnCompleteListener
                        }

                        val usersList = mutableListOf<User>()
                        for (document in querySnapshot) {
                            if (document.toObject(User::class.java).uid in user.followers) {
                                usersList.add(document.toObject(User::class.java))
                            }
                        }
                        followers.value = usersList

                        Log.d(TAG, "Users retrieved successfully")
                    } else {
                        Log.e(TAG, "Users couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return followers
    }

    override fun getFollowings(user: User): LiveData<List<User>> {
        val followings = MutableLiveData<List<User>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_USER)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Users are null, retrying...")
                            return@addOnCompleteListener
                        }

                        val usersList = mutableListOf<User>()
                        for (document in querySnapshot) {
                            if (document.toObject(User::class.java).uid in user.followings) {
                                usersList.add(document.toObject(User::class.java))
                            }
                        }
                        followings.value = usersList

                        Log.d(TAG, "Followings retrieved successfully")
                    } else {
                        Log.e(TAG, "Followings couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return followings
    }

    override fun getFollowingActivities(user: User): LiveData<List<Activity>> {
        val liveDataActivities = MutableLiveData<List<Activity>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Activities are null, retrying...")
                            return@addOnCompleteListener
                        }

                        val activitiesList = mutableListOf<Activity>()
                        for (document in querySnapshot) {
                            if ((document.toObject(Activity::class.java).acCreatedByID in
                                        user.followings)
                            ) {
                                activitiesList.add(document.toObject(Activity::class.java))
                            }
                        }
                        liveDataActivities.value = activitiesList

                        Log.d(TAG, "Activities retrieved successfully")
                    } else {
                        Log.e(TAG, "Activities couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return liveDataActivities
    }

    override fun getSearchedUsers(searchQuery: String): LiveData<List<User>> {
        val liveDataUsers = MutableLiveData<List<User>>()

        firebaseUser.let {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_USER)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            Log.e(TAG, "Users are null, retrying...")
                            return@addOnCompleteListener
                        }

                        val usersList = mutableListOf<User>()
                        for (document in querySnapshot) {
                            if (searchQuery.isNotEmpty() && (document.toObject(User::class.java))
                                    .name.lowercase().contains(searchQuery.lowercase())
                            ) {
                                usersList.add(document.toObject(User::class.java))
                            }
                        }
                        liveDataUsers.value = usersList

                        Log.d(TAG, "Users retrieved successfully")
                    } else {
                        Log.e(TAG, "Users couldn't be retrieved: ${task.exception}")
                    }
                }
        }
        return liveDataUsers
    }
}