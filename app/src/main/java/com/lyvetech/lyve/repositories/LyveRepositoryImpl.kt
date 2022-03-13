package com.lyvetech.lyve.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObjects
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.utils.Constants.ACTIVITY_CREATED_BY_ID
import com.lyvetech.lyve.utils.Constants.ACTIVITY_TITLE
import com.lyvetech.lyve.utils.Constants.COLLECTION_ACTIVITIES
import com.lyvetech.lyve.utils.Constants.COLLECTION_USER
import com.lyvetech.lyve.utils.Constants.NAME
import com.lyvetech.lyve.utils.Constants.UID
import com.lyvetech.lyve.utils.Resource
import com.lyvetech.lyve.utils.SimpleResource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class LyveRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : LyveRepository {

    override suspend fun createUser(user: User): SimpleResource =
        suspendCoroutine { cont ->
            val userDocRef: DocumentReference =
                firebaseFirestore.collection(COLLECTION_USER).document(user.uid)

            firebaseFirestore.batch()
                .set(userDocRef, user.toMap())
                .commit().addOnSuccessListener {
                    cont.resume(Resource.Success(Unit))
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getCurrentUser(): Resource<User> =
        suspendCoroutine { cont ->
            firebaseAuth.currentUser?.let { firebaseUser ->
                val userDocRef: DocumentReference =
                    firebaseFirestore.collection(COLLECTION_USER)
                        .document(firebaseUser.uid)
                var currentUser: User? = User()

                firebaseFirestore.runTransaction { transaction ->
                    val userDoc: DocumentSnapshot = transaction.get(userDocRef)

                    userDoc.toObject(User::class.java)?.let { user ->
                        if (user.email != firebaseUser.email) {
                            user.email = firebaseUser.email.toString()
                        }
                        if (user.email.isEmpty()) {
                            user.email = firebaseUser.email.toString()
                        }

                        currentUser = user
                        transaction.set(userDocRef, user.toMap(), SetOptions.merge())
                    }
                }.addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(currentUser))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(currentUser, e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
            }
        }

    override suspend fun createActivity(
        activity: Activity,
        user: User
    ): SimpleResource = suspendCoroutine { cont ->
        val activityDocRef: DocumentReference = firebaseFirestore.collection(
            COLLECTION_ACTIVITIES
        ).document(activity.aid)
        val subActivityDocRef: DocumentReference = firebaseFirestore.collection(
            COLLECTION_USER
        ).document(user.uid).collection(COLLECTION_ACTIVITIES).document(activity.aid)

        firebaseFirestore.batch().set(activityDocRef, activity.toMap())
            .set(subActivityDocRef, activity.toUserActivityMap())
            .commit().addOnSuccessListener {
                cont.resume(Resource.Success(Unit))
            }.addOnFailureListener {
                cont.resume(Resource.Error(null, it.toString()))
            }
    }

    override suspend fun updateUser(user: User): SimpleResource =
        suspendCoroutine { cont ->
            val activityDocRef = firebaseFirestore.collection(
                COLLECTION_USER
            ).document(user.uid)

            firebaseFirestore.batch().update(activityDocRef, user.toMap())
                .commit()
                .addOnSuccessListener {
                    cont.resume(Resource.Success(Unit))
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }

        }

    override suspend fun updateActivity(
        activity: Activity,
        user: User,
    ): SimpleResource = suspendCoroutine { cont ->
        val activityDocRef = firebaseFirestore.collection(
            COLLECTION_ACTIVITIES
        ).document(activity.aid)

        firebaseFirestore.batch().update(activityDocRef, activity.toMap())
            .commit()
            .addOnSuccessListener {
                cont.resume(Resource.Success(Unit))
            }.addOnFailureListener {
                cont.resume(Resource.Error(null, it.toString()))
            }

    }

    override suspend fun getUsers(): Resource<List<User>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_USER)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getActivities(): Resource<List<Activity>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getSearchedActivities(searchQuery: String): Resource<List<Activity>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_ACTIVITIES)
                .whereEqualTo(ACTIVITY_TITLE, searchQuery)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getFollowers(user: User): Resource<List<User>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_USER)
                .whereArrayContainsAny(UID, user.followers)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getFollowings(user: User): Resource<List<User>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_USER)
                .whereArrayContainsAny(UID, user.followings)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getFollowingActivities(user: User): Resource<List<Activity>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_ACTIVITIES)
                .whereArrayContainsAny(ACTIVITY_CREATED_BY_ID, user.followings)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getSearchedUsers(searchQuery: String): Resource<List<User>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_USER)
                .whereEqualTo(NAME, searchQuery)
                .get()
                .addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(it.toObjects()))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }
}