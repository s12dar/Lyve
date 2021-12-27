package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User

interface OnClickListener {
    fun onPostClicked(activity: Activity)
    fun onPostLongClicked(activity: Activity)
    fun onUserClicked(user: User)
    fun onUserFollowBtnClicked(user: User, isChecked: Boolean)
}