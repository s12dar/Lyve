package com.lyvetech.lyve.datamodels

import kotlin.properties.Delegates

class WelcomeItem {
    private var mImage: Int = 0
    private var mTitle: String = ""
    private var mDescription: String = ""

    fun getImage(): Int = mImage

    fun getTitle(): String = mTitle

    fun getDescription(): String = mDescription

    fun setImage(image: Int) {
        mImage = image
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setDescription(desc: String) {
        mDescription = desc
    }
}