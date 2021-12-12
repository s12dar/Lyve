package com.lyvetech.lyve.di

import com.lyvetech.lyve.datamanager.DataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LyveModule {

    @Singleton
    @Provides
    @Named("String")
    fun provideTestString() = "This is a string i'll inject"

    @Singleton
    @Provides
    @Named("String1")
    fun provideTestString1() = "This is a string i'll inject 1"

    @Singleton
    @Provides
    fun provideDataManager() = DataManager
}