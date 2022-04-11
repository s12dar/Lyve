package com.lyvetech.lyve.utils

object Constants {

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
    const val ATTENDINGS = "attendings"

    /**
     * ACTIVITY DATA
     */
    const val ACTIVITY_ID = "aid"
    const val ACTIVITY_TITLE = "acTitle"
    const val ACTIVITY_DESC = "acDesc"
    const val ACTIVITY_TYPE = "isOnline"
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
     * DI CONSTANTS
     */
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val KEY_EMAIL = "KEY_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"
    const val KEY_UID = "uid"

    /**
     * BUNDLE CONSTANTS
     */
    const val BUNDLE_KEY = "bundleKey"
    const val BUNDLE_FOLLOWING = "bundleFollowing"
    const val BUNDLE_FOLLOWER = "bundleFollower"
}