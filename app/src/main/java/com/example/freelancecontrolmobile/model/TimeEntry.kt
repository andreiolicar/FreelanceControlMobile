package com.example.freelancecontrolmobile.model

/**
 * Representa um registro de tempo trabalhado em um projeto.
 */
data class TimeEntry(
    val id: Int? = null,
    val projectId: Int,
    val workDate: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val notes: String? = null
)
