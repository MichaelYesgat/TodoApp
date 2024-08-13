package edu.metrostate.todoapp.api

// Data class representing a Todo response payload
data class TodoResponse(
    val description: String,
    val completed: Boolean,
    val user_id: Int,
    val author: String,
    val id: Int,
    val meta: String?
)
