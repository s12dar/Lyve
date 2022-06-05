package com.tech.lyve.listeners

import com.tech.lyve.models.User

interface HomeInfoListener {
    fun onUserClicked(user: User)
}