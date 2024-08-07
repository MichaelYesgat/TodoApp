package edu.metrostate.todoapp.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.todoapp.api.LoginRequest
import edu.metrostate.todoapp.api.TodoApiService
import kotlinx.coroutines.launch

/**
 * ViewModel for handling login operations.
 *
 * @param apiService The API service for making network requests.
 * @param sharedPreferences The shared preferences for storing user data.
 */
class LoginViewModel(
    private val apiService: TodoApiService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    /**
     * Logs in a user.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param onSuccess Callback to be invoked when login is successful.
     * @param onError Callback to be invoked when an error occurs.
     */
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.token.isNotEmpty() && response.id != 0) {
                    sharedPreferences.edit().putString("token", response.token).apply()
                    sharedPreferences.edit().putString("userId", response.id.toString()).apply()
                    onSuccess()
                } else {
                    onError("Invalid login response")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }
}
