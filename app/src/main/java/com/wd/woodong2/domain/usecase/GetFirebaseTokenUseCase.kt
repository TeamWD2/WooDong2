package com.wd.woodong2.domain.usecase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFirebaseTokenUseCase {
    operator fun invoke(): Flow<String> = flow {
        val tokenDeferred = CompletableDeferred<String>()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tokenDeferred.complete(task.result!!)
            } else {
                tokenDeferred.completeExceptionally(task.exception!!)
            }
        }
        emit(tokenDeferred.await())
    }
}
