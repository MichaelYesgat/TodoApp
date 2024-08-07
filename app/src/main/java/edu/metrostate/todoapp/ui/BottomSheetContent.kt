package edu.metrostate.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import edu.metrostate.todoapp.R
import edu.metrostate.todoapp.ui.theme.ToDoAppTheme

/**
 * Composable function to render the content of the bottom sheet used for adding a new TODO item.
 *
 * @param newTodo The current value of the new TODO item input.
 * @param onNewTodoChange Callback to be invoked when the new TODO item input value changes.
 * @param onSave Callback to be invoked when the save button is clicked.
 * @param onCancel Callback to be invoked when the cancel button is clicked.
 * @param showError Flag indicating whether to show an error message.
 */
@Composable
fun BottomSheetContent(
    newTodo: String,
    onNewTodoChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    showError: Boolean
) {
    // Apply the app theme
    ToDoAppTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 700.dp, max = 800.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color(0xfff5f3f9)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Input field for the new TODO item with a trailing icon to clear the input field
                InputField(
                    value = newTodo,
                    onValueChange = onNewTodoChange,
                    label = stringResource(id = R.string.new_todo),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    visualTransformation = VisualTransformation.None, // Keep default visual transformation
                    trailingIcon = {
                        if (newTodo.isNotEmpty()) {
                            IconButton(onClick = { onNewTodoChange("") }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(id = R.string.clear_text)
                                )
                            }
                        }
                    }
                )

                // Displaying error message if showError is true
                if (showError) {
                    Text(
                        text = stringResource(id = R.string.error_empty_todo),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                // Save button
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.primary_color)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.save))
                }

                // Cancel button
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xfff5f9f9),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        }
    }
}
