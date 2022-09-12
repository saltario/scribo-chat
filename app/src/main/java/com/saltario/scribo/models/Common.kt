package com.saltario.scribo.models

data class Common (
    // User
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",

    // Message
    var text: String = "",
    var type: String = "",
    var from: String = "",
    var time: Any = ""
)