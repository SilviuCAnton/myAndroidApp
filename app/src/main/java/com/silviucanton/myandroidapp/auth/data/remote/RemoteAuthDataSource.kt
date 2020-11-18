package com.silviucanton.myandroidapp.auth.data.remote

import com.silviucanton.myandroidapp.auth.data.TokenHolder
import com.silviucanton.myandroidapp.auth.data.User
import com.silviucanton.myandroidapp.core.Api
import com.silviucanton.myandroidapp.core.Result
import okhttp3.internal.http2.Header
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


object RemoteAuthDataSource {
    interface AuthService {
        @Headers("Content-Type: application/json")
        @POST("mobile/api/login")
        suspend fun login(@Body user: User) : TokenHolder
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<TokenHolder> {
        try {
            return Result.Success(authService.login(user))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}