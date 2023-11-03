package com.wd.woodong2.domain.provider

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FirebaseTokenProvider(
    private val firebaseMessaging: FirebaseMessaging,
) : TokenProvider {

    override fun getToken(): String {
        return runBlocking {
            firebaseMessaging.token.await()
        }
    }
}
