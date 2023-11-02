package com.wd.woodong2.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface ImageStorageRepository {
    suspend fun getImageUri(galleryUri: Uri): Flow<Uri>
}