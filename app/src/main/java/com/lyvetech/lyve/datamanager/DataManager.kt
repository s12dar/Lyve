package com.lyvetech.lyve.datamanager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_ACTIVITIES
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_USER
import javax.inject.Inject

class DataManager @Inject constructor() : DataManagerInterface {

    private val TAG = DataManager::class.qualifiedName

    companion object {
        const val AUTHENTICATION = "Authentication"
        const val INVALID_USER = "Invalid User"
        var mInstance: DataManager = DataManager()
    }

    override suspend fun createUser(user: User) {
        val firebaseUser: FirebaseUser? = Firebase.auth.currentUser

        firebaseUser?.let {
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
        } ?: run {
            Log.e(TAG, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER).toString())
        }
    }

    override fun getCurrentUser(): LiveData<User> {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val liveDataUser = MutableLiveData<User>()

        firebaseUser?.let {
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
        } ?: run {
            Log.e(TAG, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER).toString())
        }

        return liveDataUser
    }

    override suspend fun createActivity(
        activity: Activity,
        user: User
    ) {
        val firebaseUser: FirebaseUser? = Firebase.auth.currentUser

        firebaseUser?.let {
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
        } ?: run {
            Log.e(TAG, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER).toString())
        }
    }

    override suspend fun updateActivity(
        activity: Activity,
        user: User,
    ) {
        val firebaseUser: FirebaseUser? = Firebase.auth.currentUser

        firebaseUser?.let {
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
        } ?: run {
            Log.e(TAG, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER).toString())
        }
    }

    override fun getActivities(): LiveData<List<Activity?>?> {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val liveDataActivities = MutableLiveData<List<Activity?>?>()

        firebaseUser?.let {
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
        } ?: run {
            Log.e(TAG, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER).toString())
        }

        return liveDataActivities
    }
}