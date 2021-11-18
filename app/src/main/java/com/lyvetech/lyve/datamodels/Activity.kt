package com.lyvetech.lyve.datamodels

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_CREATED_AT
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_CREATED_BY_ID
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_DESC
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_ID
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_IMG_REFS
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_LOCATION
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_PARTICIPANTS
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_TIME
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_TITLE
import com.lyvetech.lyve.utils.Constants.Companion.ACTIVITY_TYPE
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
    @get:PropertyName(ACTIVITY_IMG_REFS)
    var acImgRefs = ""
    @get:PropertyName(ACTIVITY_CREATED_AT)
    var acCreatedAt = Timestamp(Date())
    @get:PropertyName(ACTIVITY_TIME)
    var acTime = ""
    @get:PropertyName(ACTIVITY_LOCATION)
    var acLocation = ""
    @get:PropertyName(ACTIVITY_CREATED_BY_ID)
    var acCreatedByID = ""
    @get:PropertyName(ACTIVITY_PARTICIPANTS)
    var acParticipants: Int = 0

    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[ACTIVITY_TITLE] = acTitle
        map[ACTIVITY_DESC] = acDesc
        map[ACTIVITY_TYPE] = acType
        map[ACTIVITY_CREATED_AT] = acCreatedAt
        map[ACTIVITY_TIME] = acTime
        map[ACTIVITY_LOCATION] = acLocation
        map[ACTIVITY_CREATED_BY_ID] = acCreatedByID
        map[ACTIVITY_PARTICIPANTS] = acParticipants
        map[ACTIVITY_IMG_REFS] = acImgRefs
        map[ACTIVITY_ID] = aid

        return map
    }

    fun toUserActivityMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[ACTIVITY_ID] = aid
        map[ACTIVITY_TITLE] = acTitle
        map[ACTIVITY_TYPE] = acType
        map[ACTIVITY_LOCATION] = acLocation
        map[ACTIVITY_PARTICIPANTS] = acParticipants

        return map
    }
}