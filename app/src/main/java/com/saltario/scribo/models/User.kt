package com.saltario.scribo.models

data class User (
        val id: String = "",
        var username: String = "",
        var bio: String = "",
        var fullname: String = "",
        var state: String = "",
        var phone: String = "",
        var photoUrl: String = "empty"
)