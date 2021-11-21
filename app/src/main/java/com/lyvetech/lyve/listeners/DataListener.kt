package com.lyvetech.lyve.listeners

import java.lang.Exception

interface DataListener<T> {
    fun onData(data: T?, exception: Exception?)
}