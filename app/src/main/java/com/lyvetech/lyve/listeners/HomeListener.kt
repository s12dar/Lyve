package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.models.Event

interface HomeListener {
    fun onPostClicked(event: Event)
}