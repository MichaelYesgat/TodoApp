package edu.metrostate.todoapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.metrostate.todoapp.ui.CreateAccountScreen
import edu.metrostate.todoapp.ui.LoginScreen
import edu.metrostate.todoapp.ui.TodoListScreen

/**
 * Sets up the navigation host for the application.
 *
 * @param context The context of the calling component.
 */
@Composable
fun NavHostSetup(context: Context) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("createAccount") {
            CreateAccountScreen(navController)
        }
        composable("todoList") {
            TodoListScreen(navController)
        }
    }
}
