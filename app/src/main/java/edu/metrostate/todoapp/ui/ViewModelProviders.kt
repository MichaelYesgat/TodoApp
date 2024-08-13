package edu.metrostate.todoapp.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.todoapp.api.TodoApiService
import edu.metrostate.todoapp.viewmodel.CreateAccountViewModel
import edu.metrostate.todoapp.viewmodel.LoginViewModel
import edu.metrostate.todoapp.viewmodel.TodoListViewModel

/**
 * Composable function to provide the TodoListViewModel.
 *
 * @param context The context to access shared preferences.
 * @return The TodoListViewModel instance.
 */
@Composable
fun getTodoListViewModel(context: Context): TodoListViewModel {
    val sharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    val apiService = TodoApiService.create(sharedPreferences)
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
                return TodoListViewModel(apiService, sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })
}

/**
 * Composable function to provide the CreateAccountViewModel.
 *
 * @param context The context to access shared preferences.
 * @return The CreateAccountViewModel instance.
 */
@Composable
fun getCreateAccountViewModel(context: Context): CreateAccountViewModel {
    val sharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    val apiService = TodoApiService.create(sharedPreferences)
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateAccountViewModel::class.java)) {
                return CreateAccountViewModel(apiService, sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })
}

/**
 * Composable function to provide the LoginViewModel.
 *
 * @param context The context to access shared preferences.
 * @return The LoginViewModel instance.
 */
@Composable
fun getLoginViewModel(context: Context): LoginViewModel {
    val sharedPreferences = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    val apiService = TodoApiService.create(sharedPreferences)
    return viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(apiService, sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })
}
