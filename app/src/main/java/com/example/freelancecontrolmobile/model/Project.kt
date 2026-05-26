package com.example.freelancecontrolmobile.model

/**
 * Representa um projeto associado a um cliente.
 */
data class Project(
    val id: Int? = null,
    val clientId: Int,
    val title: String,
    val description: String? = null,
    val hourlyRate: Double,
    val status: String = "ativo"
)
