package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.models.Activity

interface OnPostClickListener {
    fun onPostClicked(item: Any)
    fun onPostLongClicked(activity: Activity)
}