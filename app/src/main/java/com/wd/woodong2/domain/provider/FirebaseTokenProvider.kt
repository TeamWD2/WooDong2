package com.wd.woodong2.domain.provider

import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseTokenProvider(
    private val firebaseMessaging: FirebaseMessaging,
) : TokenProvider {

    override suspend fun getToken(): String = suspendCoroutine { continuation ->
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result!!)
            } else {
                continuation.resumeWithException(task.exception!!)
            }
        }
    }
}
