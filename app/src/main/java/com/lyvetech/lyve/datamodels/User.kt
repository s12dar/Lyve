package com.lyvetech.lyve.datamodels

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lyvetech.lyve.utils.Constants.Companion.BIO
import com.lyvetech.lyve.utils.Constants.Companion.CREATED_AT
import com.lyvetech.lyve.utils.Constants.Companion.EMAIL
import com.lyvetech.lyve.utils.Constants.Companion.FIRST_NAME
import com.lyvetech.lyve.utils.Constants.Companion.IS_VERIFIED
import com.lyvetech.lyve.utils.Constants.Companion.LAST_NAME
import com.lyvetech.lyve.utils.Constants.Companion.NR_OF_FOLLOWERS
import com.lyvetech.lyve.utils.Constants.Companion.NR_OF_FOLLOWINGS
import com.lyvetech.lyve.utils.Constants.Companion.PHONE_NUMBER
import com.lyvetech.lyve.utils.Constants.Companion.UID
import java.util.*
import kotlin.collections.HashMap

class User {

    @get:PropertyName(UID)
    var userId = ""
    @get:PropertyName(FIRST_NAME)
    var firstName = ""
    @get:PropertyName(LAST_NAME)
    var lastName = ""
    @get:PropertyName(EMAIL)
    var email= ""
    @get:PropertyName(PHONE_NUMBER)
    var phoneNumber = ""
    @get:PropertyName(CREATED_AT)
    var createdAt = Timestamp(Date())
    @get:PropertyName(IS_VERIFIED)
    var isVerified: Boolean = true
    @get:PropertyName(BIO)
    var bio = ""
    @get: PropertyName(NR_OF_FOLLOWERS)
    var nrOfFollowers: Int = 0
    @get: PropertyName(NR_OF_FOLLOWINGS)
    var nrOfFollowings: Int = 0


    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[UID] = userId
        map[FIRST_NAME] = firstName
        map[LAST_NAME] = lastName
        map[EMAIL] = email
        map[PHONE_NUMBER] = phoneNumber
        map[CREATED_AT] = createdAt
        map[BIO] = bio
        map[IS_VERIFIED] = isVerified
        map[NR_OF_FOLLOWERS] = nrOfFollowers
        map[NR_OF_FOLLOWINGS] = nrOfFollowings

        return map
    }
}