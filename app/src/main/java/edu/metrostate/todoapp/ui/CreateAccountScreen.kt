package edu.metrostate.todoapp.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.metrostate.todoapp.R
import edu.metrostate.todoapp.ui.theme.ToDoAppTheme
import edu.metrostate.todoapp.viewmodel.CreateAccountViewModel
import java.util.regex.Pattern

/**
 * Composable function to render the create account screen.
 *
 * @param navController The NavController for navigating between screens.
 */
@Composable
fun CreateAccountScreen(navController: NavController) {
    // Get the context
    val context = LocalContext.current

    // Get the ViewModel for create account
    val viewModel: CreateAccountViewModel = getCreateAccountViewModel(context)

    // State variables to hold input values
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State variables to manage password visibility
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    // Apply the app theme
    ToDoAppTheme {
        // Column layout to arrange the UI components vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the app name
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Input fields for user details
            InputField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(id = R.string.name)
            )
            InputField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            InputField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(id = R.string.password),
                isPasswordField = true,
                passwordVisible = passwordVisible,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            InputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = stringResource(id = R.string.confirm_password),
                isPasswordField = true,
                passwordVisible = confirmPasswordVisible,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Spacer to add space between input fields and button
            Spacer(modifier = Modifier.height(32.dp))

            // Button to create an account
            CreateAccountButton(
                name = name,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                context = context,
                viewModel = viewModel,
                navController = navController
            )

            // Text button for navigating to the login screen
            TextButton(onClick = { navController.navigate("login") }) {
                Text(
                    stringResource(id = R.string.already_have_account),
                    color = colorResource(id = R.color.primary_color)
                )
            }
        }
    }
}

/**
 * Composable function to render the create account button and handle its click event.
 *
 * @param name The name input value.
 * @param email The email input value.
 * @param password The password input value.
 * @param confirmPassword The confirm password input value.
 * @param context The context to show Toast messages.
 * @param viewModel The ViewModel for creating an account.
 * @param navController The NavController for navigating between screens.
 */
@Composable
fun CreateAccountButton(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    context: Context,
    viewModel: CreateAccountViewModel,
    navController: NavController
) {
    // Button for creating an account
    Button(
        onClick = {
            val emailPattern = Pattern.compile(
                "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            )
            // Validation checks for input fields
            when {
                name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                    Toast.makeText(context, R.string.error_all_fields_required, Toast.LENGTH_SHORT).show()
                }
                !emailPattern.matcher(email).matches() -> {
                    Toast.makeText(context, R.string.error_invalid_email, Toast.LENGTH_SHORT).show()
                }
                password.length < 8 -> {
                    Toast.makeText(context, R.string.error_password_length, Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(context, R.string.error_passwords_do_not_match, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Create account using ViewModel
                    viewModel.createAccount(name, email, password, {
                        Toast.makeText(context, R.string.account_created_successfully, Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    }, {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    })
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primary_color)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = stringResource(id = R.string.create_account), color = MaterialTheme.colorScheme.onPrimary)
    }
}
