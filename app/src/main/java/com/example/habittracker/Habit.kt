package com.example.habittracker

data class Habit(
    val id: Int,
    val name: String,
    val goal: String,
    var completed: Boolean,
    val started: Boolean,
    val startedAt: String?,
    val completedAt: String?
)