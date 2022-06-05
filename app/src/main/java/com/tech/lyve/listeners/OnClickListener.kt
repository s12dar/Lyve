package com.tech.lyve.listeners

import com.tech.lyve.models.Event
import com.tech.lyve.models.User

interface OnClickListener {
    fun onPostClicked(event: Event)
    fun onPostLongClicked(event: Event)
    fun onUserClicked(user: User)
    fun onUserFollowBtnClicked(user: User, isChecked: Boolean)
}