package edu.metrostate.todoapp

import android.content.SharedPreferences
import edu.metrostate.todoapp.api.RegistrationRequest
import edu.metrostate.todoapp.api.TodoApiService
import edu.metrostate.todoapp.viewmodel.CreateAccountViewModel
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Unit tests for the CreateAccountViewModel class.
 *
 * This test class verifies the behavior of the account creation functionality
 * within the CreateAccountViewModel. The tests utilize MockK to mock dependencies
 * like the TodoApiService and SharedPreferences, ensuring that the ViewModel behaves
 * correctly under various conditions.
 */
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class CreateAccountViewModelTest {

    /**
     * Test to verify that the account creation process succeeds when valid data is provided.
     *
     * This test checks that when valid registration details are provided, the TodoApiService's
     * createAccount function is called, the token and userId are stored in SharedPreferences,
     * and the onSuccess callback is invoked. Additionally, the onError callback should not be
     * called.
     */
    @Test
    fun `createAccount should succeed when valid data is provided`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val token = "validToken"
        val userId = 123
        val registrationRequest = RegistrationRequest(name, email, password)

        // Mocking the API service to return a successful response
        val apiService = mockk<TodoApiService>()
        coEvery { apiService.createAccount(registrationRequest) } returns mockk {
            every { this@mockk.token } returns token
            every { this@mockk.id } returns userId
        }

        // Mocking SharedPreferences with relaxed behavior to avoid unnecessary setup
        val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        val viewModel = CreateAccountViewModel(apiService, sharedPreferences)

        // Mocking the success and error callbacks
        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.createAccount(name, email, password, onSuccess, onError)

        // Assert
        coVerify { apiService.createAccount(registrationRequest) } // Verifying that the API is called
        verify {
            // Verifying that the token and userId are stored in SharedPreferences
            sharedPreferences.edit().putString("token", token).apply()
            sharedPreferences.edit().putString("userId", userId.toString()).apply()
            // Verifying that the onSuccess callback is called and onError is not called
            onSuccess()
            onError wasNot Called
        }
    }

    /**
     * Test to verify that the account creation process fails when invalid data is provided.
     *
     * This test checks that when invalid registration details are provided, the TodoApiService's
     * createAccount function throws an exception, and the onError callback is invoked with the
     * appropriate error message. The onSuccess callback should not be called in this scenario.
     */
    @Test
    fun `createAccount should fail when invalid data is provided`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "invalid@example.com"
        val password = "password123"
        val registrationRequest = RegistrationRequest(name, email, password)
        val errorMessage = "Invalid create account response"

        // Mocking the API service to throw an exception for invalid account creation
        val apiService = mockk<TodoApiService>()
        coEvery { apiService.createAccount(registrationRequest) } throws RuntimeException(errorMessage)

        // Mocking SharedPreferences with relaxed behavior
        val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        val viewModel = CreateAccountViewModel(apiService, sharedPreferences)

        // Mocking the success and error callbacks
        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.createAccount(name, email, password, onSuccess, onError)

        // Assert
        coVerify { apiService.createAccount(registrationRequest) } // Verifying that the API is called
        verify {
            // Verifying that the onError callback is called with the correct error message
            onError(errorMessage)
            // Verifying that the onSuccess callback is not called
            onSuccess wasNot Called
        }
    }
}
