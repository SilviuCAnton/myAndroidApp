package com.silviucanton.myandroidapp.bugs.data.remote

import android.util.Log
import com.silviucanton.myandroidapp.bugs.data.Bug
import com.silviucanton.myandroidapp.core.Api
import com.silviucanton.myandroidapp.core.Api.WS_URL
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okio.ByteString
import retrofit2.http.*
import retrofit2.http.Headers

object BugApi {
    interface Service {
        @GET("mobile/api/bugs")
        suspend fun find(): List<Bug>

        @GET("mobile/api/bugs/{id}")
        suspend fun read(@Path("id") BugId: Long): Bug;

        @Headers("Content-Type: application/json")
        @POST("mobile/api/bugs")
        suspend fun create(@Body Bug: Bug): Bug

        @Headers("Content-Type: application/json")
        @PUT("mobile/api/bugs/{id}")
        suspend fun update(@Path("id") BugId: Long, @Body Bug: Bug): Bug

        @DELETE("mobile/api/bugs/{id}")
        suspend fun delete(@Path("id") BugId: Long)
    }

    val service: Service = Api.retrofit.create(Service::class.java)

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

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocket", "onMessage bytes")
            output("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "onClosing")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "onFailure", t)
            t.printStackTrace()
        }

        private fun output(txt: String) {
            Log.d("WebSocket", txt)
        }
    }
}