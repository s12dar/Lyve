package com.lyvetech.lyve.datamodels

import com.google.firebase.firestore.PropertyName
import com.kopxyz.olayi.utils.Constants.Companion.CREATED_AT
import com.kopxyz.olayi.utils.Constants.Companion.EMAIL
import com.kopxyz.olayi.utils.Constants.Companion.FIRST_NAME
import com.kopxyz.olayi.utils.Constants.Companion.LAST_NAME
import com.kopxyz.olayi.utils.Constants.Companion.PHONE_NUMBER
import com.kopxyz.olayi.utils.Constants.Companion.UID

class User {

    @get:PropertyName(UID)
    var userId: String = ""
    @get:PropertyName(FIRST_NAME)
    var firstName: String = ""
    @get:PropertyName(LAST_NAME)
    var lastName: String = ""
    @get:PropertyName(EMAIL)
    var email: String = ""
    @get:PropertyName(PHONE_NUMBER)
    var phoneNumber: String = ""
    @get:PropertyName(CREATED_AT)
    var createdAt: String = ""

    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[UID] = userId
        map[FIRST_NAME] = firstName
        map[LAST_NAME] = lastName
        map[EMAIL] = email
        map[PHONE_NUMBER] = phoneNumber
        map[CREATED_AT] = createdAt

        return map
    }
}