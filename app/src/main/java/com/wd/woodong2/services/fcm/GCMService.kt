package com.wd.woodong2.services.fcm

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GCMService {
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: FCMNotification
    ): Response<ResponseBody>
}

