package edu.metrostate.todoapp

import android.content.SharedPreferences
import edu.metrostate.todoapp.api.TodoApiService
import edu.metrostate.todoapp.api.TodoResponseList
import edu.metrostate.todoapp.viewmodel.TodoListViewModel
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Unit tests for the TodoListViewModel class.
 *
 * This class provides unit tests to verify the behavior of various operations
 * within the TodoListViewModel, including fetching, creating, and updating TODO items.
 * The tests use MockK to mock dependencies such as the TodoApiService and SharedPreferences,
 * ensuring that the ViewModel's logic is tested in isolation from external components.
 */
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class TodoListViewModelTest {

    private val userId = "123"

    /**
     * Helper function to create a mocked SharedPreferences instance.
     *
     * This function creates and returns a mock SharedPreferences object with a
     * relaxed SharedPreferences.Editor. It also ensures that a userId is returned
     * whenever the "userId" key is requested.
     */
    private fun createMockSharedPreferences(): SharedPreferences {
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)
        return mockk {
            every { getString("userId", null) } returns userId
            every { edit() } returns editor
        }
    }

    /**
     * Test to verify that fetchTodos populates the todos list when the API call is successful.
     *
     * This test ensures that when the API successfully returns a list of TODO items,
     * the ViewModel's todos StateFlow is updated accordingly.
     */
    @Test
    fun `fetchTodos should populate todos when API call is successful`() = runTest {
        // Arrange
        val todoList = listOf(
            mockk<TodoResponseList> {
                every { description } returns "Test TODO"
                every { completed } returns false
            }
        )

        val apiService = mockk<TodoApiService> {
            coEvery { getTodos(userId) } returns todoList
        }

        val sharedPreferences = createMockSharedPreferences()
        val viewModel = TodoListViewModel(apiService, sharedPreferences)

        // Act
        viewModel.fetchTodos(userId)

        // Assert
        coVerify { apiService.getTodos(userId) }
        assertEquals(todoList, viewModel.todos.first())
    }

    /**
     * Test to verify that fetchTodos handles exceptions by keeping the todos list empty.
     *
     * This test ensures that when the API call fails, the ViewModel does not populate
     * the todos list, and the StateFlow remains empty.
     */
    @Test
    fun `fetchTodos should handle failure when API call throws exception`() = runTest {
        // Arrange
        val apiService = mockk<TodoApiService> {
            coEvery { getTodos(userId) } throws RuntimeException("API call failed")
        }

        val sharedPreferences = createMockSharedPreferences()
        val viewModel = TodoListViewModel(apiService, sharedPreferences)

        // Act
        viewModel.fetchTodos(userId)

        // Assert
        coVerify { apiService.getTodos(userId) }
        assertTrue(viewModel.todos.first().isEmpty()) // Assuming the flow remains empty on failure
    }

    /**
     * Test to verify that createTodo adds a new TODO item and refreshes the list when successful.
     *
     * This test ensures that when a new TODO item is successfully created, the API is called,
     * the list is refreshed, and the ViewModel's todos StateFlow is updated accordingly.
     */
    @Test
    fun `createTodo should add a new todo and refresh the list when successful`() = runTest {
        // Arrange
        val description = "New TODO"

        // Mocking the TodoResponseList object
        val mockTodoResponse = mockk<TodoResponseList> {
            every { this@mockk.description } returns description
            every { this@mockk.completed } returns false
        }

        val apiService = mockk<TodoApiService> {
            coEvery { createTodo(userId, any()) } returns mockk()
            coEvery { getTodos(userId) } returns listOf(mockTodoResponse)
        }

        val sharedPreferences = createMockSharedPreferences()
        val viewModel = TodoListViewModel(apiService, sharedPreferences)

        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.createTodo(description, onSuccess, onError)

        // Assert
        coVerify { apiService.createTodo(userId, any()) }
        coVerify { apiService.getTodos(userId) }
        verify { onSuccess() }
        assertEquals(1, viewModel.todos.first().size)
        assertEquals(description, viewModel.todos.first()[0].description)
        verify { onError wasNot Called }
    }

    /**
     * Test to verify that createTodo handles failures by invoking the onError callback.
     *
     * This test ensures that when the API call to create a TODO item fails, the ViewModel
     * invokes the onError callback with the appropriate error message, and does not call
     * the onSuccess callback.
     */
    @Test
    fun `createTodo should handle failure when API call throws exception`() = runTest {
        // Arrange
        val description = "New TODO"
        val errorMessage = "Failed to create TODO"

        val apiService = mockk<TodoApiService> {
            coEvery { createTodo(userId, any()) } throws RuntimeException(errorMessage)
        }

        val sharedPreferences = createMockSharedPreferences()
        val viewModel = TodoListViewModel(apiService, sharedPreferences)

        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.createTodo(description, onSuccess, onError)

        // Assert
        coVerify { apiService.createTodo(userId, any()) }
        verify { onError(errorMessage) }
        verify { onSuccess wasNot Called }
    }

    /**
     * Test to verify that updateTodoStatus updates the TODO item and refreshes the list when successful.
     *
     * This test ensures that when the status of a TODO item is successfully updated, the API is called,
     * the list is refreshed, and the ViewModel's todos StateFlow is updated accordingly.
     */
    @Test
    fun `updateTodoStatus should update the todo and refresh the list when successful`() = runTest {
        // Arrange
        val todoId = 1
        val description = "Updated TODO"
        val isCompleted = true

        val mockTodoResponse = mockk<TodoResponseList> {
            every { this@mockk.description } returns description
            every { this@mockk.completed } returns isCompleted
        }

        val apiService = mockk<TodoApiService> {
            coEvery { updateTodo(userId, todoId.toString(), any()) } returns mockk()
            coEvery { getTodos(userId) } returns listOf(mockTodoResponse)
        }

        val sharedPreferences = createMockSharedPreferences()
        val viewModel = TodoListViewModel(apiService, sharedPreferences)

        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.updateTodoStatus(todoId, description, isCompleted, onSuccess, onError)

        // Assert
        coVerify { apiService.updateTodo(userId, todoId.toString(), any()) }
        coVerify { apiService.getTodos(userId) }
        verify { onSuccess() }
        assertEquals(1, viewModel.todos.first().size)
        assertEquals(description, viewModel.todos.first()[0].description)
        assertTrue(viewModel.todos.first()[0].completed)
        verify { onError wasNot Called }
    }

    /**
     * Test to verify that updateTodoStatus handles failures by invoking the onError callback.
     *
     * This test ensures that when the API call to update a TODO item fails, the ViewModel
     * invokes the onError callback with the appropriate error message, and does not call
     * the onSuccess callback.
     */
    @Test
    fun `updateTodoStatus should handle failure when API call throws exception`() = runTest {
        // Arrange
        val todoId = 1
        val description = "Updated TODO"
        val isCompleted = true
        val errorMessage = "Failed to update TODO"

        val apiService = mockk<TodoApiService> {
            coEvery { updateTodo(userId, todoId.toString(), any()) } throws RuntimeException(errorMessage)
        }

        val sharedPreferences = createMockSharedPreferences()
        val viewModel = TodoListViewModel(apiService, sharedPreferences)

        val onSuccess: () -> Unit = mockk(relaxed = true)
        val onError: (String) -> Unit = mockk(relaxed = true)

        // Act
        viewModel.updateTodoStatus(todoId, description, isCompleted, onSuccess, onError)

        // Assert
        coVerify { apiService.updateTodo(userId, todoId.toString(), any()) }
        verify { onError(errorMessage) }
        verify { onSuccess wasNot Called }
    }
}
