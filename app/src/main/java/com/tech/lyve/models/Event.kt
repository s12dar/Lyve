package com.tech.lyve.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.tech.lyve.utils.Constants.EVENT_CREATED_AT
import com.tech.lyve.utils.Constants.EVENT_CREATED_BY_ID
import com.tech.lyve.utils.Constants.EVENT_DATE
import com.tech.lyve.utils.Constants.EVENT_DESC
import com.tech.lyve.utils.Constants.EVENT_ID
import com.tech.lyve.utils.Constants.EVENT_IMG_REFS
import com.tech.lyve.utils.Constants.EVENT_LOCATION
import com.tech.lyve.utils.Constants.EVENT_PARTICIPANTS
import com.tech.lyve.utils.Constants.EVENT_TIME
import com.tech.lyve.utils.Constants.EVENT_TITLE
import com.tech.lyve.utils.Constants.EVENT_TYPE
import com.tech.lyve.utils.Constants.EVENT_URL
import java.io.Serializable
import java.util.*

class Event : Serializable {

    @get:PropertyName(EVENT_ID)
    var uid = ""

    @get:PropertyName(EVENT_TITLE)
    var title = ""

    @get:PropertyName(EVENT_DESC)
    var desc = ""

    @get:PropertyName(EVENT_TYPE)
    var isOnline = false

    @get:PropertyName(EVENT_IMG_REFS)
    var imgRefs = ""

    @get:PropertyName(EVENT_CREATED_AT)
    var createdAt = Timestamp(Date())

    @get:PropertyName(EVENT_TIME)
    var time = ""

    @get:PropertyName(EVENT_DATE)
    var date = ""

    @get:PropertyName(EVENT_LOCATION)
    var location = HashMap<String, GeoPoint>()

    @get:PropertyName(EVENT_URL)
    var url = ""

    @get:PropertyName(EVENT_CREATED_BY_ID)
    var createdByID: String = ""

    @get:PropertyName(EVENT_PARTICIPANTS)
    var participants = mutableListOf<BasketTypeUser>()

    fun toMap(): HashMap<String, Any?> {
        val map = HashMap<String, Any?>()
        map[EVENT_TITLE] = title
        map[EVENT_DESC] = desc
        map[EVENT_TYPE] = isOnline
        map[EVENT_CREATED_AT] = createdAt
        map[EVENT_DATE] = date
        map[EVENT_TIME] = time
        map[EVENT_LOCATION] = location
        map[EVENT_URL] = url
        map[EVENT_CREATED_BY_ID] = createdByID
        map[EVENT_PARTICIPANTS] = participants
        map[EVENT_IMG_REFS] = imgRefs
        map[EVENT_ID] = uid

        return map
    }
}