package com.lyvetech.lyve.listeners

import com.lyvetech.lyve.datamodels.Activity

interface OnPostClickListener {
    fun onPostClicked(activity: Activity)
    fun onPostLongClicked(activity: Activity)
}