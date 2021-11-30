package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.models.Activity

interface OnPostClickListener {
    fun onPostClicked(activity: Activity)
    fun onPostLongClicked(activity: Activity)
}