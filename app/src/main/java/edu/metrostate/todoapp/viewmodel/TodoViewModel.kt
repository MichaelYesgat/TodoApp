package edu.metrostate.todoapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.todoapp.api.TodoApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import edu.metrostate.todoapp.api.TodoRequest
import edu.metrostate.todoapp.api.TodoResponseList

/**
 * ViewModel for handling TODO list operations.
 *
 * @param apiService The API service for making network requests.
 * @param sharedPreferences The shared preferences for storing user data.
 */
class TodoListViewModel(
    private val apiService: TodoApiService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    // StateFlow to hold the list of TODO items
    private val _todos = MutableStateFlow<List<TodoResponseList>>(emptyList())
    val todos: StateFlow<List<TodoResponseList>> = _todos

    // User ID from shared preferences
    val userId: String? = sharedPreferences.getString("userId", null)

    init {
        userId?.let { id ->
            fetchTodos(id)
        }
    }

    /**
     * Fetches the list of TODO items for the given user.
     *
     * @param userId The ID of the user whose TODO items are to be fetched.
     */
    fun fetchTodos(userId: String) {
        viewModelScope.launch {
            try {
                val fetchedTodos = apiService.getTodos(userId)
                _todos.value = fetchedTodos
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Creates a new TODO item.
     *
     * @param description The description of the TODO item.
     * @param onSuccess Callback to be invoked when the TODO item is successfully created.
     * @param onError Callback to be invoked when an error occurs.
     */
    fun createTodo(description: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (userId != null) {
                    val todoRequest = TodoRequest(
                        description = description,
                        completed = false
                    )
                    apiService.createTodo(userId = userId, todo = todoRequest)
                    fetchTodos(userId) // Refresh the todo list after creation
                    onSuccess()
                } else {
                    onError("User ID is missing")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }

    /**
     * Updates the status of a TODO item.
     *
     * @param todoId The ID of the TODO item to be updated.
     * @param description The description of the TODO item.
     * @param isCompleted The completion status of the TODO item.
     * @param onSuccess Callback to be invoked when the TODO item is successfully updated.
     * @param onError Callback to be invoked when an error occurs.
     */
    fun updateTodoStatus(todoId: Int, description: String, isCompleted: Boolean, onSuccess:  () -> Unit, onError:  (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (userId != null) {
                    val updatedTodo = TodoRequest(
                        id = todoId.toString(),
                        description = description,
                        completed = isCompleted
                    )
                    apiService.updateTodo(
                        userId = userId,
                        todoId = todoId.toString(),
                        todo = updatedTodo
                    )
                    fetchTodos(userId) // Refresh the todo list after update
                    onSuccess()
                } else {
                    onError("User ID is missing")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }

    /**
     * Logs out the user by clearing the shared preferences.
     */
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
