package com.silviucanton.myandroidapp.core

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private const val URL = "http://192.168.1.105:8080/"

    private const val WS_URL = "ws://192.168.1.105:8080/mobile/myHandler"

    val tokenInterceptor = TokenInterceptor()

    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(tokenInterceptor)
    }.build()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val eventChannel = Channel<String>()

    init {
        val request = Request.Builder().url(WS_URL).build()
        OkHttpClient().newWebSocket(request, MyWebSocketListener())
    }

    private class MyWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "onMessage$text")
            runBlocking { eventChannel.send(text) }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "onFailure", t)
            t.printStackTrace()
        }

        private fun output(txt: String) {
            Log.d("WebSocket", txt)
        }
    }
}
