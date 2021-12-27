package com.lyvetech.lyve.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lyvetech.lyve.utils.Constants.Companion.AVATAR
import com.lyvetech.lyve.utils.Constants.Companion.BIO
import com.lyvetech.lyve.utils.Constants.Companion.CREATED_AT
import com.lyvetech.lyve.utils.Constants.Companion.EMAIL
import com.lyvetech.lyve.utils.Constants.Companion.FOLLOWERS
import com.lyvetech.lyve.utils.Constants.Companion.FOLLOWINGS
import com.lyvetech.lyve.utils.Constants.Companion.IS_VERIFIED
import com.lyvetech.lyve.utils.Constants.Companion.NAME
import com.lyvetech.lyve.utils.Constants.Companion.PASS
import com.lyvetech.lyve.utils.Constants.Companion.UID
import java.util.*
import kotlin.collections.HashMap

class User {

    @get:PropertyName(UID)
    var uid = ""

    @get:PropertyName(NAME)
    var name = ""

    @get:PropertyName(EMAIL)
    var email = ""

    @get:PropertyName(AVATAR)
    var avatar = ""

    @get:PropertyName(PASS)
    var pass = ""

    @get:PropertyName(BIO)
    var bio = ""

    @get:PropertyName(CREATED_AT)
    var createdAt = Timestamp(Date())

    @get:PropertyName(IS_VERIFIED)
    var isVerified: Boolean = true

    @get: PropertyName(FOLLOWERS)
    var followers = mutableListOf<String>()

    @get: PropertyName(FOLLOWINGS)
    var followings = mutableListOf<String>()


    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[UID] = uid
        map[NAME] = name
        map[EMAIL] = email
        map[AVATAR] = avatar
        map[CREATED_AT] = createdAt
        map[BIO] = bio
        map[IS_VERIFIED] = isVerified
        map[FOLLOWERS] = followers
        map[FOLLOWINGS] = followings

        return map
    }
}