package com.mileskrell.playdate.api

data class JoinRequest(
    val token: String,
    val code: String,
    val index: Int
)
