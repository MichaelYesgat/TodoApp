package edu.metrostate.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable

// Data class representing a Todo item
data class TodoItem(val text: String, var isCompleted: Boolean)

// Main activity for the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Set the content to the TodoApp composable
            TodoApp()
        }
    }
}

// Main composable function for the Todo application
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    // State to hold the list of todos
    val todos = remember { mutableStateListOf<TodoItem>() }
    // State to hold the new todo input
    val (newTodo, setNewTodo) = remember { mutableStateOf("") }
    // State to show error if input is invalid
    val (showError, setShowError) = remember { mutableStateOf(false) }
    // Focus manager to handle input focus
    val focusManager = LocalFocusManager.current
    // State to track the visibility of the modal bottom sheet
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        topBar = {
            // Top bar of the app
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(id = R.string.app_name))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEEFDF6)
                )
            )
        },
        floatingActionButton = {
            // Floating action button to add new todos
            FloatingActionButton(
                onClick = {
                    openBottomSheet = true
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                },
                containerColor = Color(0xFF43e69f)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_todo))
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp)
                    .background(Color(0xFFEEFDF6))
            ) {
                Spacer(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                )
                // Iterate through the list of todos and display each
                todos.forEachIndexed { index, todo ->
                    TodoItemRow(
                        todo = todo,
                        onToggle = {
                            // Toggle the completion status of the todo
                            todos[index] = todo.copy(isCompleted = !todo.isCompleted)
                        }
                    )
                }
            }
        }
    )

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            modifier = Modifier.height(screenHeight / 2),
            content = {
                BottomSheet(
                    newTodo = newTodo,
                    onNewTodoChange = setNewTodo,
                    onSave = {
                        if (newTodo.isNotBlank()) {
                            // Add new todo to the list
                            todos.add(TodoItem(newTodo, false))
                            // Reset input and error states
                            setNewTodo("")
                            setShowError(false)
                            // Clear focus
                            focusManager.clearFocus()
                            // Hide the bottom sheet
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }.invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                        } else {
                            // Show error if input is empty
                            setShowError(true)
                        }
                    },
                    onCancel = {
                        // Reset input and error states on cancel
                        setNewTodo("")
                        setShowError(false)
                        focusManager.clearFocus()
                        // Hide the bottom sheet
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    },
                    showError = showError
                )
            }
        )
    }
}

// Composable function to display a single Todo item
@Composable
fun TodoItemRow(todo: TodoItem, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        // Display the text of the todo
        Text(
            text = todo.text,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )
        // Checkbox to mark the todo as completed
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF43e69f)
            )
        )
    }
}

// Composable function for the bottom sheet content
@Composable
fun BottomSheet(
    newTodo: String,
    onNewTodoChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    showError: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(min = 275.dp, max = 700.dp), // Adjusted height to half of 550.dp (min height)
        shape = MaterialTheme.shapes.medium,
        color = Color(0xfff5f3f9)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Input field for new todo
            OutlinedTextField(
                value = newTodo,
                onValueChange = onNewTodoChange,
                label = { Text(stringResource(id = R.string.new_todo)) },
                trailingIcon = {
                    if (newTodo.isNotEmpty()) {
                        IconButton(onClick = { onNewTodoChange("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.clear_text)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSave() }
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = showError
            )
            // Display error message if input is invalid
            if (showError) {
                Text(
                    text = stringResource(id = R.string.error_empty_todo),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(25.dp)) // Spacer added here
            // Save button
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(Color(0xFF43e69f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.save))
            }
            // Cancel button
            OutlinedButton(
                onClick = onCancel,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xfff5f3f9),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    }
}

// Preview function to display the TodoApp composable in Android Studio
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoApp()
}
