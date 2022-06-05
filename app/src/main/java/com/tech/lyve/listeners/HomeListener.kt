package com.tech.lyve.listeners

import com.tech.lyve.models.Event

interface HomeListener {
    fun onPostClicked(event: Event)
}