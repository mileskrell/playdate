package com.mileskrell.playdate.model

data class Playdate(
    val name: String,
    val code: String,
    val startTime: String?, // If null, it means the start time hasn't yet been decided (the playdate is pending)
    val duration: Int
)
