package com.lyvetech.lyve.datamodels

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_CREATED_BY
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_DATE_AND_TIME
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_DESC
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_ID
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_LOCATION
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_PARTICIPANTS
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_TIME
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_TITLE
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_TYPE
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

class Activity {

    @get:PropertyName(ACTIVITY_ID)
    var aid = ""
    @get:PropertyName(ACTIVITY_TITLE)
    var acTitle = ""
    @get:PropertyName(ACTIVITY_DESC)
    var acDesc = ""
    @get:PropertyName(ACTIVITY_TYPE)
    var acType = ""
    @get:PropertyName(ACTIVITY_TIME)
    var acTime = Timestamp(Date())
    @get:PropertyName(ACTIVITY_DATE_AND_TIME)
    var acDateAndTime = Timestamp(Date())
    @get:PropertyName(ACTIVITY_LOCATION)
    var acLocation = ""
    @get:PropertyName(ACTIVITY_CREATED_BY)
    var acCreatedBy = ""
    @get:PropertyName(ACTIVITY_PARTICIPANTS)
    var acParticipants: Int = 0

    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[ACTIVITY_TITLE] = acTitle
        map[ACTIVITY_DESC] = acDesc
        map[ACTIVITY_TYPE] = acType
        map[ACTIVITY_TIME] = acTime
        map[ACTIVITY_DATE_AND_TIME] = acDateAndTime
        map[ACTIVITY_LOCATION] = acLocation
        map[ACTIVITY_CREATED_BY] = acCreatedBy
        map[ACTIVITY_PARTICIPANTS] = acParticipants

        return map
    }
}