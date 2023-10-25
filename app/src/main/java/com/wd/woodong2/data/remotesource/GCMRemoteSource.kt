package com.wd.woodong2.data.remotesource

import com.wd.woodong2.data.model.GCMRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GCMRemoteSource {
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: GCMRequest
    ): Response<ResponseBody>
}

