package com.example.freelancecontrolmobile.model

/**
 * Representa um cliente no sistema.
 */
data class Client(
    val id: Int? = null,
    val name: String,
    val email: String? = null,
    val phone: String? = null
)
