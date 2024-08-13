package edu.metrostate.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.metrostate.todoapp.R
import edu.metrostate.todoapp.api.TodoResponseList

/**
 * Composable function to render a row representing a single TODO item.
 *
 * @param todo The TODO item to be displayed.
 * @param onToggle Callback to be invoked when the checkbox is toggled.
 */
@Composable
fun TodoItemRow(todo: TodoResponseList, onToggle: (String) -> Unit) {
    // Row layout to arrange the text and checkbox horizontally
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        // Text displaying the description of the TODO item
        Text(
            text = todo.description,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )
        // Checkbox to toggle the completion status of the TODO item
        Checkbox(
            checked = todo.completed,
            onCheckedChange = { onToggle(todo.description) },
            colors = CheckboxDefaults.colors(
                checkedColor = colorResource(id = R.color.primary_color)
            )
        )
    }
}
