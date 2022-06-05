package com.tech.lyve.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tech.lyve.R
import com.tech.lyve.utils.Constants.SHARED_PREFERENCES_NAME
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
}