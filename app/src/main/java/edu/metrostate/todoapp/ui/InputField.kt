package edu.metrostate.todoapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Composable function to render an input field with optional password visibility toggle.
 *
 * @param value The current text in the input field.
 * @param onValueChange Callback to be invoked when the input field value changes.
 * @param label The label text for the input field.
 * @param visualTransformation Visual transformation to apply to the input text (e.g., password masking).
 * @param keyboardOptions Keyboard options for the input field.
 * @param isPasswordField Flag indicating if the input field is for password input.
 * @param passwordVisible Mutable state to manage the visibility of the password.
 * @param trailingIcon Composable function to render a trailing icon in the input field.
 */
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPasswordField: Boolean = false,
    passwordVisible: MutableState<Boolean>? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPasswordField && passwordVisible != null && !passwordVisible.value) {
            PasswordVisualTransformation()
        } else {
            visualTransformation
        },
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            if (isPasswordField && passwordVisible != null) {
                val image = if (passwordVisible.value) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
            trailingIcon?.invoke()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}
