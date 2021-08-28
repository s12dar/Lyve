package com.lyvetech.lyve.datamanager

import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.ktx.Firebase
import com.kopxyz.olayi.utils.Constants.Companion.COLLECTION_USER
import com.lyvetech.lyve.datamodels.User

class DataManager : DataManagerInterface {

    companion object {
        const val AUTHENTICATION = "Authentication"
        const val INVALID_USER = "Invalid User"
        lateinit var mInstance: DataManager
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
}