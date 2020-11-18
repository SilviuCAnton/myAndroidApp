package com.silviucanton.myandroidapp.auth.data

import okhttp3.Headers


data class TokenHolder(
        var token: String,
        var userId: String
)