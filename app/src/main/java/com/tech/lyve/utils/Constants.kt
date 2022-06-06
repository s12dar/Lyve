package com.tech.lyve.utils

object Constants {

    /**
     * User data
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
    const val LAST_LOCATION = "lastLocation"
    const val NR_OF_EVENTS = "nrOfEvents"
    const val COLLECTION_USER = "Users"

    /**
     * Event data
     */
    const val EVENT_ID = "uid"
    const val EVENT_TITLE = "title"
    const val EVENT_DESC = "desc"
    const val EVENT_TYPE = "isOnline"
    const val EVENT_IMG_REFS = "imgRefs"
    const val EVENT_CREATED_AT = "createdAt"
    const val EVENT_DATE = "date"
    const val EVENT_TIME = "time"
    const val EVENT_LOCATION = "location"
    const val EVENT_URL = "url"
    const val EVENT_CREATED_BY_ID = "createdByID"
    const val EVENT_PARTICIPANTS = "participants"
    const val COLLECTION_ACTIVITIES = "Activities"

    /**
     * DI constants
     */
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val KEY_EMAIL = "keyEmail"
    const val KEY_PASSWORD = "keyPassword"

    /**
     * Bundle constants
     */
    const val BUNDLE_KEY = "bundleKey"
    const val BUNDLE_FOLLOWING = "bundleFollowing"
    const val BUNDLE_FOLLOWER = "bundleFollower"
    const val BUNDLE_EVENT_KEY = "bundleEventKey"
    const val BUNDLE_HOST_USER_KEY = "bundleHostUserKey"
    const val BUNDLE_CURRENT_USER_KEY = "bundleCurrentUserKey"

    /**
     * Intent constants
     */
    const val INTENT_GOOGLE_MAPS = "com.google.android.apps.maps"

    /**
     * Permission contants
     */
    const val REQUEST_LOCATION_PERMISSION = 0

    /**
     * Pagination constants
     */
    const val QUERY_PAGE_SIZE = 20

    /**
     * Ping constants
     */
    const val REQUEST_DECLINED = "declined"
    const val REQUEST_ACCEPTED = "accepted"
}