package com.lyvetech.lyve.utils

class Constants {

    companion object {

        /**
         * USER DATA
         */
        const val NAME = "name"
        const val EMAIL = "email"
        const val UID = "uid"
        const val AVATAR = "avatar"
        const val PASS = "pass"
        const val CREATED_AT = "createdAt"
        const val BIO = "bio"
        const val IS_VERIFIED = "isVerified"
        const val FOLLOWERS = "followers"
        const val FOLLOWINGS = "followings"

        /**
         * ACTIVITY DATA
         */
        const val ACTIVITY_ID = "aid"
        const val ACTIVITY_TITLE = "acTitle"
        const val ACTIVITY_DESC = "acDesc"
        const val ACTIVITY_TYPE = "acType"
        const val ACTIVITY_IMG_REFS = "acImgRefs"
        const val ACTIVITY_CREATED_AT = "acCreatedAt"
        const val ACTIVITY_TIME = "acTime"
        const val ACTIVITY_LOCATION = "acLocation"
        const val ACTIVITY_CREATED_BY_ID = "acCreatedByID"
        const val ACTIVITY_PARTICIPANTS = "acParticipants"

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

        /**
         * DI CONSTANTS
         */
        const val SHARED_PREFERENCES_NAME = "sharedPref"
        const val KEY_EMAIL = "KEY_EMAIL"
        const val KEY_PASSWORD = "KEY_PASSWORD"
    }


}