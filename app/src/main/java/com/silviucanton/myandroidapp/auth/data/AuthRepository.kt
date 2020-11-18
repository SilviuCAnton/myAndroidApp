package com.silviucanton.myandroidapp.auth.data

import com.silviucanton.myandroidapp.auth.data.remote.RemoteAuthDataSource
import com.silviucanton.myandroidapp.core.Api
import com.silviucanton.myandroidapp.core.Constants
import com.silviucanton.myandroidapp.core.Result

object AuthRepository {
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        Api.tokenInterceptor.token = null
    }

    suspend fun login(
            username: String,
            password: String
    ): Result<TokenHolder> {
        val user = User(username, password)
        val result = RemoteAuthDataSource.login(user)

        if (result is Result.Success<TokenHolder>) {
            print("Success!!")
            val tokenHolder = result.data
            print(tokenHolder)

            setLoggedInUser(user, result.data)
            Constants.instance()?.storeValueString("token", tokenHolder.token)
            Constants.instance()?.storeValueString("_id", tokenHolder.userId)
        }

        return result
    }

    private fun setLoggedInUser(
            user: User,
            tokenHolder: TokenHolder
    ) {
        AuthRepository.user = user
        Api.tokenInterceptor.token = tokenHolder.token
        Api.tokenInterceptor.userId = tokenHolder.userId
    }
}