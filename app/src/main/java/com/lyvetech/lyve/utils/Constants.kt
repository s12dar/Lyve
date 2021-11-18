package com.lyvetech.lyve.utils

class Constants {

    companion object {

        /**
         * USER DATA
         */
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val PHONE_NUMBER = "phoneNumber"
        const val EMAIL = "email"
        const val UID = "uid"
        const val CREATED_AT = "createdAt"
        const val BIO = "bio"
        const val IS_VERIFIED = "isVerified"
        const val NR_OF_FOLLOWERS = "nrOfFollowers"
        const val NR_OF_FOLLOWINGS = "nrOfFollowings"

        /**
         * ACTIVITY DATA
         */
        const val ACTIVITY_ID = "aid"
        const val ACTIVITY_TITLE = "acTitle"
        const val ACTIVITY_DESC = "acDesc"
        const val ACTIVITY_TYPE = "acType"
        const val ACTIVITY_CREATED_AT = "acCreatedAt"
        const val ACTIVITY_TIME = "acTime"
        const val ACTIVITY_LOCATION = "acLocation"
        const val ACTIVITY_CREATED_BY_ID = "acCreatedByID"
        const val ACTIVITY_PARTICIPANTS = "acParticipants"
        const val ACTIVITY_UID = "acUID"

        /**
         * FIREBASE DATA
         */
        const val COLLECTION_USER = "Users"
        const val COLLECTION_ACTIVITIES = "Activities"

        /**
         * BUNDLE CONSTANTS
         */
        const val EXISTING_USER = "existingUser"
        const val USER_STATUS = "userStatus"
    }


}