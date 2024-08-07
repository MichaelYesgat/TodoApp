package edu.metrostate.todoapp.api

// Data class representing a registration request payload
data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String
)
