package com.lyvetech.lyve.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lyvetech.lyve.utils.Constants.ATTENDINGS
import com.lyvetech.lyve.utils.Constants.AVATAR
import com.lyvetech.lyve.utils.Constants.BIO
import com.lyvetech.lyve.utils.Constants.CREATED_AT
import com.lyvetech.lyve.utils.Constants.EMAIL
import com.lyvetech.lyve.utils.Constants.FOLLOWERS
import com.lyvetech.lyve.utils.Constants.FOLLOWINGS
import com.lyvetech.lyve.utils.Constants.IS_VERIFIED
import com.lyvetech.lyve.utils.Constants.NAME
import com.lyvetech.lyve.utils.Constants.PASS
import com.lyvetech.lyve.utils.Constants.UID
import java.io.Serializable
import java.util.*

class User : Serializable {

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

    @get: PropertyName(ATTENDINGS)
    var attendings = mutableListOf<Basket>()

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
        map[ATTENDINGS] = attendings

        return map
    }
}