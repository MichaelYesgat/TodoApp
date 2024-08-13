package edu.metrostate.todoapp.ui

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import edu.metrostate.todoapp.viewmodel.LoginViewModel

/**
 * Composable function to render the login screen.
 *
 * @param navController The NavController for navigating between screens.
 */
@Composable
fun LoginScreen(navController: NavController) {
    // Get the context
    val context = LocalContext.current

    // Get the ViewModel for login
    val viewModel: LoginViewModel = getLoginViewModel(context)

    // State variables to hold input values
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State variables to manage password visibility
    val passwordVisible = remember { mutableStateOf(false) }

    // Permission launcher for requesting location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, context.getString(R.string.permission), Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Request the permission when the screen is first displayed
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

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

            // Spacer to add space between input fields and button
            Spacer(modifier = Modifier.height(32.dp))

            // Button to log in
            LoginButton(
                email = email,
                password = password,
                context = context,
                viewModel = viewModel,
                navController = navController
            )

            // Text button for navigating to the create account screen
            TextButton(onClick = { navController.navigate("createAccount") }) {
                Text(
                    stringResource(id = R.string.dont_have_account),
                    color = colorResource(id = R.color.primary_color)
                )
            }
        }
    }
}

/**
 * Composable function to render the login button and handle its click event.
 *
 * @param email The email input value.
 * @param password The password input value.
 * @param context The context to show Toast messages.
 * @param viewModel The ViewModel for logging in.
 * @param navController The NavController for navigating between screens.
 */
@Composable
fun LoginButton(
    email: String,
    password: String,
    context: Context,
    viewModel: LoginViewModel,
    navController: NavController
) {
    // Button for logging in
    Button(
        onClick = {
            viewModel.login(email, password, {
                Toast.makeText(context, R.string.login_successful, Toast.LENGTH_SHORT).show()
                navController.navigate("todoList")
            }, {
                Toast.makeText(context, R.string.login_failed, Toast.LENGTH_SHORT).show()
            })
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.primary_color)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.log_in),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
