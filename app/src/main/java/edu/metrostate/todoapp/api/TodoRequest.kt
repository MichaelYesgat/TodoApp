package edu.metrostate.todoapp.api

// Data class representing a Todo request payload
data class TodoRequest(
    val id: String = "",
    val description: String,
    val completed: Boolean
)
