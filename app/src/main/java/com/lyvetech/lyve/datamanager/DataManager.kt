package com.lyvetech.lyve.datamanager

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.datamodels.Activity
import com.lyvetech.lyve.datamodels.User
import com.lyvetech.lyve.listeners.DataListener
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_ACTIVITIES
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_USER
import java.util.*

class DataManager : DataManagerInterface {

    companion object {
        const val AUTHENTICATION = "Authentication"
        const val INVALID_USER = "Invalid User"
        var mInstance: DataManager = DataManager()
    }

    override fun createUser(user: User, listener: DataListener<Boolean>) {
        val currentUser: FirebaseUser? = Firebase.auth.currentUser
        if (currentUser != null) {
            listener.onData(null, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER))
        }

        val userBatch: WriteBatch = FirebaseFirestore.getInstance().batch()
        val userDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(COLLECTION_USER).document(user.uid)

        userBatch.set(userDocRef, user.toMap())
        userBatch.commit().addOnCompleteListener{ task ->
            run {
                if (task.isSuccessful) {
                    listener.onData(true, null)
                } else {
                    listener.onData(false, task.exception)
                }
            }
        }
    }

    override fun getCurrentUser(listener: DataListener<User>) {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            listener.onData(null, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER))
        } else {
            val userDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(COLLECTION_USER).document(firebaseUser.uid)

            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val userDoc: DocumentSnapshot = transaction.get(userDocRef)

                val currentUser: User? = userDoc.toObject(User::class.java)
                if (currentUser != null) {
                    if (currentUser.email != firebaseUser.email) {
                        currentUser.email = firebaseUser.email.toString()
                    }

                    if (currentUser.email.isEmpty()) {
                        currentUser.email = firebaseUser.email.toString()
                    }

                    transaction.set(userDocRef, currentUser.toMap(), SetOptions.merge())
                    currentUser
                } else {
                    null
                }
            }.addOnCompleteListener {task ->
                run {
                    if (task.isSuccessful && task.result != null) {
                        val currentUser: User = task.result!!
                        listener.onData(currentUser, null)
                    } else {
                        listener.onData(null, task.exception)
                    }
                }
            }
        }
    }

    override fun createActivity(activity: Activity, user: FirebaseUser, listener: DataListener<Boolean>) {
        val currentUser: FirebaseUser? = Firebase.auth.currentUser
        if (currentUser != null) {
            listener.onData(null, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER))
        }

        val activityBatch: WriteBatch = FirebaseFirestore.getInstance().batch()

        val activityDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(
            COLLECTION_ACTIVITIES).document(activity.aid)
        val subActivityDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(
            COLLECTION_USER).document(user.uid).collection(COLLECTION_ACTIVITIES).document(activity.aid)

        activityBatch.set(activityDocRef, activity.toMap())
        activityBatch.set(subActivityDocRef, activity.toUserActivityMap())

        activityBatch.commit().addOnCompleteListener{ task ->
            run {
                if (task.isSuccessful) {
                    listener.onData(true, null)
                } else {
                    listener.onData(false, task.exception)
                }
            }
        }
    }

    override fun getActivities(listener: DataListener<List<Activity?>>) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            listener.onData(null, FirebaseAuthInvalidUserException(AUTHENTICATION, INVALID_USER))
        } else {
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful) {
                        val querySnapshot = task.result
                        val activities: MutableList<Activity?> =
                            LinkedList<Activity?>()
                        if (querySnapshot == null || querySnapshot.isEmpty) {
                            listener.onData(null, null)
                            return@addOnCompleteListener
                        }
                        for (document in querySnapshot) {
                            activities.add(document.toObject(Activity::class.java))
                        }
                        listener.onData(activities, null)
                    } else {
                        listener.onData(null, task.exception)
                    }
                }
        }
    }
}