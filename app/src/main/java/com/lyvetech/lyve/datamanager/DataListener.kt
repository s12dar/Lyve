package com.lyvetech.lyve.datamanager

import java.lang.Exception

interface DataListener<T> {
    fun onData(data: T?, exception: Exception?)
}