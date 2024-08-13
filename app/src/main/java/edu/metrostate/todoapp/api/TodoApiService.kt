package edu.metrostate.todoapp.api

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface defining the API service for the Todo application.
 * This interface contains methods for user authentication, account creation,
 * and operations related to Todo items.
 */
interface TodoApiService {

    /**
     * Endpoint for user login.
     *
     * @param request The login request body containing email and password.
     * @param apiKey The API key for authentication.
     * @return The login response containing user details and token.
     */
    @POST("api/users/login")
    suspend fun login(
        @Body request: LoginRequest,
        @Query("apikey") apiKey: String = API_KEY
    ): LoginResponse

    /**
     * Endpoint for creating a new user account.
     *
     * @param request The registration request body containing user details.
     * @param apiKey The API key for authentication.
     * @return The registration response containing the newly created user details.
     */
    @POST("api/users/register")
    suspend fun createAccount(
        @Body request: RegistrationRequest,
        @Query("apikey") apiKey: String = API_KEY
    ): RegistrationResponse

    /**
     * Endpoint for creating a new Todo item.
     *
     * @param userId The ID of the user creating the Todo item.
     * @param todo The request body containing the details of the Todo item.
     * @param apiKey The API key for authentication.
     * @return The response containing the created Todo item details.
     */
    @POST("api/users/{user_id}/todos")
    suspend fun createTodo(
        @Path("user_id") userId: String,
        @Body todo: TodoRequest,
        @Query("apikey") apiKey: String = API_KEY
    ): TodoResponse

    /**
     * Endpoint for fetching Todo items of a user.
     *
     * @param userId The ID of the user whose Todo items are to be fetched.
     * @param apiKey The API key for authentication.
     * @return A list of Todo items belonging to the user.
     */
    @GET("api/users/{user_id}/todos")
    suspend fun getTodos(
        @Path("user_id") userId: String,
        @Query("apikey") apiKey: String = API_KEY
    ): List<TodoResponseList>

    /**
     * Endpoint for updating a specific Todo item.
     *
     * @param userId The ID of the user who owns the Todo item.
     * @param todoId The ID of the Todo item to be updated.
     * @param todo The request body containing the updated details of the Todo item.
     * @param apiKey The API key for authentication.
     * @return The response containing the updated Todo item details.
     */
    @PUT("api/users/{user_id}/todos/{id}")
    suspend fun updateTodo(
        @Path("user_id") userId: String,
        @Path("id") todoId: String,
        @Body todo: TodoRequest,
        @Query("apikey") apiKey: String = API_KEY
    ): TodoResponse

    /**
     * Companion object to create an instance of TodoApiService with necessary configurations.
     */
    companion object {
        private const val BASE_URL = "https://todos.simpleapi.dev/"
        const val API_KEY = "812a6df4-f342-4d91-aa0a-6545424fc390"

        /**
         * Function to create an instance of TodoApiService.
         *
         * @param sharedPreferences The SharedPreferences instance to retrieve the token.
         * @return An instance of TodoApiService.
         */
        fun create(sharedPreferences: SharedPreferences): TodoApiService {
            // Logging interceptor to log the details of network requests and responses
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // OkHttpClient to add interceptors for logging and adding headers
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${sharedPreferences.getString("token", "")}")
                        .build()
                    chain.proceed(request)
                }
                .build()

            // Moshi instance for JSON serialization/deserialization
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            // Retrofit instance to create the TodoApiService
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(TodoApiService::class.java)
        }
    }
}
