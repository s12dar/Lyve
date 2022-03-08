package com.lyvetech.lyve.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lyvetech.lyve.R
import com.lyvetech.lyve.repositories.LyveRepositoryImpl
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.utils.Constants.COLLECTION_ACTIVITIES
import com.lyvetech.lyve.utils.Constants.COLLECTION_USER
import com.lyvetech.lyve.utils.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LyveModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext app: Context
    ): SharedPreferences = app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideBundle(
    ): Bundle = Bundle()

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext app: Context
    ) = Glide.with(app).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.lyve)
            .error(R.drawable.lyve)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun provideFirebaseAuthInstance(
    ): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun provideFirebaseUser(
        auth: FirebaseAuth
    ): FirebaseUser? = auth.currentUser

    @Singleton
    @Provides
    fun provideFirebaseFirestore(
    ): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun provideUsersCollectionReference(
        rootRef: FirebaseFirestore
    ): CollectionReference = rootRef.collection(COLLECTION_USER)

    @Singleton
    @Provides
    fun provideEventsCollectionReference(
        rootRef: FirebaseFirestore
    ): CollectionReference = rootRef.collection(COLLECTION_ACTIVITIES)

    @Singleton
    @Provides
    fun provideLyveRepository(
        auth: FirebaseAuth,
        firebaseUser: FirebaseUser
    ): LyveRepository = LyveRepositoryImpl(auth, firebaseUser)
}