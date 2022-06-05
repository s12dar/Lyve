package com.tech.lyve.models

data class BasketTypeEvent(
    var uid: String = "",
    val imgUrl: String = "",
    var title: String = "",
    var desc: String = "",
    var createdBy: String = "",
    var date: String = ""
)