package com.mileskrell.playdate.api

data class GenerateRequest(
    val token: String,
    val start: String,
    val end: String,
    val duration: Int
)
