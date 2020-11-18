package com.silviucanton.myandroidapp.bugs.data.remote

import com.silviucanton.myandroidapp.bugs.data.Bug
import com.silviucanton.myandroidapp.core.Api
import retrofit2.http.*

object BugApi {
    interface Service {
        @GET("mobile/api/bugs")
        suspend fun find(): List<Bug>

        @GET("mobile/api/bugs/{id}")
        suspend fun read(@Path("id") BugId: Long): Bug

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
}