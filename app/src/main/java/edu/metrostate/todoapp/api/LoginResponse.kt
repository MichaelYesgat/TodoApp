package edu.metrostate.todoapp.api

// Data class representing a login response payload
data class LoginResponse(
    val id: Int,
    val name: String,
    val email: String,
    val enabled: Int,
    val token: String,
    val admin: Int
)
