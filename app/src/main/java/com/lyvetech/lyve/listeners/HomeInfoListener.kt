package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.models.User

interface HomeInfoListener {
    fun onUserClicked(user: User)
}