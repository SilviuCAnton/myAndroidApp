package com.silviucanton.myandroidapp.bugs.data.remote

import com.silviucanton.myandroidapp.bugs.data.Bug

data class MessageData(var event: String, var payload: Payload) {
    data class Payload(var error: String, var bug: Bug)
}