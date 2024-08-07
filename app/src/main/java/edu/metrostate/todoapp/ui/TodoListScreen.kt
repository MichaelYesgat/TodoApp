package edu.metrostate.todoapp.ui


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.metrostate.todoapp.R
import edu.metrostate.todoapp.viewmodel.TodoListViewModel
import kotlinx.coroutines.launch

/**
 * Composable function to render the TODO list screen.
 *
 * @param navController The NavController for navigating between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: TodoListViewModel = getTodoListViewModel(context)

    // State variables for managing UI elements and data
    val todos by viewModel.todos.collectAsState()
    val focusManager = LocalFocusManager.current
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var newTodo by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Fetch the todos when the screen is first loaded
    LaunchedEffect(Unit) {
        viewModel.userId?.let { userId ->
            viewModel.fetchTodos(userId)
        }
    }

    // Scaffold to structure the screen layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            modifier = Modifier.padding(horizontal = 123.dp)
                        )

                        // Logout icon button
                        IconButton(onClick = {
                            viewModel.logout()
                            Toast.makeText(context, context.getString(R.string.logged_out), Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("todoList") { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(id = R.string.logout),
                                tint = colorResource(id = R.color.primary_color),
                                modifier = Modifier.size(33.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.secondary_color)
                )
            )
        },
        floatingActionButton = {
            // Floating action button to add a new TODO item
            FloatingActionButton(
                onClick = {
                    openBottomSheet = true
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                },
                containerColor = colorResource(id = R.color.primary_color)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_todo))
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp)
                    .background(colorResource(id = R.color.secondary_color))
            ) {
                Spacer(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                )
                if (todos.isEmpty()) {
                    Text(stringResource(id = R.string.no_todos_available), modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn {
                        items(todos) { todo ->
                            TodoItemRow(
                                todo = todo,
                                onToggle = { description ->
                                    viewModel.updateTodoStatus(
                                        todoId = todo.id,
                                        description = description,
                                        isCompleted = !todo.completed,
                                        onSuccess = {
                                            Toast.makeText(context, context.getString(R.string.todo_updated), Toast.LENGTH_SHORT).show()
                                        },
                                        onError = {
                                            Toast.makeText(context, context.getString(R.string.todo_update_failed), Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    // Modal bottom sheet to add a new TODO item
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            content = {
                BottomSheetContent(
                    newTodo = newTodo,
                    onNewTodoChange = { newTodo = it },
                    onSave = {
                        if (newTodo.isNotBlank()) {
                            viewModel.createTodo(
                                description = newTodo,
                                onSuccess = {
                                    newTodo = ""
                                    showError = false
                                    focusManager.clearFocus()
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                    }.invokeOnCompletion {
                                        if (!bottomSheetState.isVisible) {
                                            openBottomSheet = false
                                        }
                                    }
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            showError = true
                        }
                    },
                    onCancel = {
                        newTodo = ""
                        showError = false
                        focusManager.clearFocus()
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
