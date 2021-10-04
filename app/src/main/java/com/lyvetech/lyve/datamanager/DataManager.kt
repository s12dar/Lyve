package com.lyvetech.lyve.datamanager

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.utils.Constants.Companion.COLLECTION_USER
import com.lyvetech.lyve.datamodels.User

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
        val userDocRef: DocumentReference = FirebaseFirestore.getInstance().collection(COLLECTION_USER).document(user.userId)

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
}