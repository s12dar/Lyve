package com.tech.lyve.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.tech.lyve.utils.Constants.ATTENDINGS
import com.tech.lyve.utils.Constants.AVATAR
import com.tech.lyve.utils.Constants.BIO
import com.tech.lyve.utils.Constants.CREATED_AT
import com.tech.lyve.utils.Constants.EMAIL
import com.tech.lyve.utils.Constants.FOLLOWERS
import com.tech.lyve.utils.Constants.FOLLOWINGS
import com.tech.lyve.utils.Constants.IS_VERIFIED
import com.tech.lyve.utils.Constants.LAST_LOCATION
import com.tech.lyve.utils.Constants.NAME
import com.tech.lyve.utils.Constants.NR_OF_EVENTS
import com.tech.lyve.utils.Constants.PASS
import com.tech.lyve.utils.Constants.UID
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
    var attendings = mutableListOf<BasketTypeEvent>()

    @get: PropertyName(LAST_LOCATION)
    var lastLocation = GeoPoint(0.0, 0.0)

    @get: PropertyName(NR_OF_EVENTS)
    var nrOfEvents = 0;

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
        map[LAST_LOCATION] = lastLocation
        map[NR_OF_EVENTS] = nrOfEvents

        return map
    }
}