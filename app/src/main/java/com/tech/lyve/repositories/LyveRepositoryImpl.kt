package com.tech.lyve.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import com.tech.lyve.utils.Constants.COLLECTION_ACTIVITIES
import com.tech.lyve.utils.Constants.COLLECTION_USER
import com.tech.lyve.utils.Constants.UID
import com.tech.lyve.utils.Resource
import com.tech.lyve.utils.SimpleResource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class LyveRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) : LyveRepository {

    override suspend fun createUser(user: User): SimpleResource =
        suspendCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword(user.email, user.pass)
                .addOnSuccessListener {
                    user.uid = firebaseAuth.currentUser?.uid.toString()
                    val userDocRef: DocumentReference =
                        firebaseFirestore.collection(COLLECTION_USER).document(user.uid)

                    firebaseFirestore.batch()
                        .set(userDocRef, user.toMap())
                        .commit().addOnSuccessListener {
                            cont.resume(Resource.Success(Unit))
                        }.addOnFailureListener {
                            cont.resume(Resource.Error(null, it.toString()))
                        }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun loginUser(email: String, pass: String): SimpleResource =
        suspendCoroutine { cont ->
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    cont.resume(Resource.Success(Unit))
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getCurrentUser(): Resource<User?> =
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
        event: Event,
        user: User
    ): SimpleResource = suspendCoroutine { cont ->
        val activityDocRef: DocumentReference = firebaseFirestore.collection(
            COLLECTION_ACTIVITIES
        ).document(event.uid)
        val subActivityDocRef: DocumentReference = firebaseFirestore.collection(
            COLLECTION_USER
        ).document(user.uid).collection(COLLECTION_ACTIVITIES).document(event.uid)

        firebaseFirestore.batch().set(activityDocRef, event.toMap())
            .set(subActivityDocRef, event.toUserEventMap())
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
        event: Event,
        user: User,
    ): SimpleResource = suspendCoroutine { cont ->
        val activityDocRef = firebaseFirestore.collection(
            COLLECTION_ACTIVITIES
        ).document(event.uid)

        val subActivityDocRef: DocumentReference = firebaseFirestore.collection(
            COLLECTION_USER
        ).document(user.uid).collection(COLLECTION_ACTIVITIES).document(event.uid)

        firebaseFirestore.batch().update(activityDocRef, event.toMap())
            .update(subActivityDocRef, event.toUserEventMap())
            .commit()
            .addOnSuccessListener {
                cont.resume(Resource.Success(Unit))
            }.addOnFailureListener {
                cont.resume(Resource.Error(null, it.toString()))
            }

    }

    override suspend fun getUsers(): Resource<List<User>?> =
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

    override suspend fun getActivities(): Resource<List<Event>?> =
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

    override suspend fun getSearchedActivities(searchQuery: String): Resource<List<Event>?> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnSuccessListener {
                    try {
                        val activitiesList = mutableListOf<Event>()
                        for (document in it) {
                            if (searchQuery.isNotEmpty() && document.toObject(Event::class.java)
                                    .title.lowercase().contains(searchQuery.lowercase())
                            ) {
                                activitiesList.add(document.toObject(Event::class.java))
                            }
                        }
                        cont.resume(Resource.Success(activitiesList))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getFollowers(user: User): Resource<List<User>?> =
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

    override suspend fun getFollowings(user: User): Resource<List<User>?> =
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

    override suspend fun getFollowingActivities(user: User): Resource<List<Event>?> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_ACTIVITIES)
                .get()
                .addOnSuccessListener {
                    try {
                        val activitiesList = mutableListOf<Event>()
                        for (document in it) {
                            if ((document.toObject(Event::class.java).createdByID in
                                        user.followings)
                            ) {
                                activitiesList.add(document.toObject(Event::class.java))
                            }
                        }
                        cont.resume(Resource.Success(activitiesList))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }

    override suspend fun getUploadImgUriFromFirebaseStorage(imgUri: Uri): Resource<Uri> =
        suspendCoroutine { cont ->
            val fileRef = firebaseStorage.getReference(
                System.currentTimeMillis().toString()
            )
            fileRef.putFile(imgUri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener {
                    try {
                        cont.resume(Resource.Success(data = it))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(data = it, message = e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
            }
        }

    override suspend fun getEventBelongingToCurrentUser(
        user: User
    ): Resource<List<Event>> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(
                COLLECTION_USER
            ).document(user.uid).collection(COLLECTION_ACTIVITIES)
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

    override suspend fun getSearchedUsers(searchQuery: String): Resource<List<User>?> =
        suspendCoroutine { cont ->
            firebaseFirestore.collection(COLLECTION_USER)
                .get()
                .addOnSuccessListener {
                    try {
                        val usersList = mutableListOf<User>()
                        for (document in it) {
                            if (searchQuery.isNotEmpty() && document.toObject(User::class.java)
                                    .name.lowercase().contains(searchQuery.lowercase())
                            ) {
                                usersList.add(document.toObject(User::class.java))
                            }
                        }
                        cont.resume(Resource.Success(usersList))
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(it.toObjects(), e.toString()))
                    }
                }.addOnFailureListener {
                    cont.resume(Resource.Error(null, it.toString()))
                }
        }
}