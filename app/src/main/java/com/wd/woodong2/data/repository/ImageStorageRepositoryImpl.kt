package com.wd.woodong2.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.StorageReference
import com.wd.woodong2.domain.repository.ImageStorageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ImageStorageRepositoryImpl (
    private val storageReference: StorageReference
) : ImageStorageRepository {

    companion object {
        const val TAG = "ImageStorageRepositoryImpl"
    }

    override suspend fun getImageUri(galleryUri: Uri): Flow<Uri> = callbackFlow {
        storageReference.putFile(galleryUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { imageUri ->
                trySend(imageUri)
                close()
            }.addOnFailureListener { exception ->
                close(exception)
            }
        }.addOnFailureListener { exception ->
            close(exception)
        }

        awaitClose {
            Log.e(TAG, "awaitClose")
        }
    }
}