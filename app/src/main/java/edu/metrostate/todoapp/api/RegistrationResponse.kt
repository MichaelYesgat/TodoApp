package edu.metrostate.todoapp.api

// Data class representing a registration response payload
data class RegistrationResponse(
    val id: Int,
    val name: String,
    val email: String,
    val enabled: Boolean,
    val token: String,
    val admin: Boolean
)
