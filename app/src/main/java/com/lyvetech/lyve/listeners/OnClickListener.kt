package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.models.User

interface OnClickListener {
    fun onPostClicked(event: Event)
    fun onPostLongClicked(event: Event)
    fun onUserClicked(user: User)
    fun onUserFollowBtnClicked(user: User, isChecked: Boolean)
}