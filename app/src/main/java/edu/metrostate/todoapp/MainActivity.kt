package edu.metrostate.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Main activity for the TODO application.
 * Sets up the content view with the navigation host.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to the navigation host composable
        setContent {
            NavHostSetup(context = this)
        }
    }
}
