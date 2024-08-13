package edu.metrostate.todoapp.api

import com.squareup.moshi.Json

// Data class representing a list of Todo responses
data class TodoResponseList(
    val id: Int,
    val user_id: Int,
    val description: String,
    @Json(name = "completed")
    private val _completed: Int,
    val author: String,
    val meta: String?
) {
    // Getter to convert the _completed integer to a boolean
    val completed: Boolean
        get() = _completed != 0
}
