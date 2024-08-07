package edu.metrostate.todoapp.api

// Data class representing a login request payload
data class LoginRequest(
    val email: String,
    val password: String
)
