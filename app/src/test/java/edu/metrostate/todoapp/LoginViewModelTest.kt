package edu.metrostate.todoapp

import android.content.SharedPreferences
import edu.metrostate.todoapp.api.LoginRequest
import edu.metrostate.todoapp.api.LoginResponse
import edu.metrostate.todoapp.api.TodoApiService
import edu.metrostate.todoapp.viewmodel.LoginViewModel
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Unit tests for the LoginViewModel class.
 *
 * This class provides unit tests to verify the behavior of the login functionality
 * within the LoginViewModel. The tests utilize MockK to mock dependencies such as
 * the TodoApiService and SharedPreferences, and to verify interactions with these
 * dependencies.
 */
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class LoginViewModelTest {

    /**
     * Test to verify that the login process succeeds when valid credentials are provided.
     *
     * This test ensures that when valid credentials are passed to the login method,
     * the TodoApiService's login function is called, the token and userId are stored in
     * SharedPreferences, and the onSuccess callback is invoked. Additionally, the onError
     * callback should not be called.
     */
    @Test
    fun `login should succeed when valid credentials are provided`() = runTest {
        // Arrange
        val token = "validToken"
        val userId = 123
        val loginRequest = LoginRequest("test@example.com", "password")

        // Mocking the LoginResponse to return the expected token and userId
        val response = mockk<LoginResponse> {
            every { this@mockk.token } returns token
            every { this@mockk.id } returns userId
        }

        // Mocking the API service to return the mocked response
        val apiService = mockk<TodoApiService>()
        coEvery { apiService.login(loginRequest) } returns response

        // Mocking SharedPreferences with relaxed behavior to avoid unnecessary setup
        val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        val viewModel = LoginViewModel(apiService, sharedPreferences)

        // Mocking the success and error callbacks
        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.login("test@example.com", "password", onSuccess, onError)

        // Assert
        coVerify { apiService.login(loginRequest) } // Verifying that the login API is called
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
     * Test to verify that the login process fails when invalid credentials are provided.
     *
     * This test ensures that when invalid credentials are passed to the login method,
     * the TodoApiService's login function throws an exception, and the onError callback
     * is invoked with the appropriate error message. The onSuccess callback should not
     * be called in this scenario.
     */
    @Test
    fun `login should fail when invalid credentials are provided`() = runTest {
        // Arrange
        val loginRequest = LoginRequest("wrong@example.com", "wrongPassword")
        val errorMessage = "Invalid login response"

        // Mocking the API service to throw an exception for invalid login
        val apiService = mockk<TodoApiService>()
        coEvery { apiService.login(loginRequest) } throws RuntimeException(errorMessage)

        // Mocking SharedPreferences with relaxed behavior
        val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        val viewModel = LoginViewModel(apiService, sharedPreferences)

        // Mocking the success and error callbacks
        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.login("wrong@example.com", "wrongPassword", onSuccess, onError)

        // Assert
        coVerify { apiService.login(loginRequest) } // Verifying that the login API is called
        verify {
            // Verifying that the onError callback is called with the correct error message
            onError(errorMessage)
            // Verifying that the onSuccess callback is not called
            onSuccess wasNot Called
        }
    }
}
