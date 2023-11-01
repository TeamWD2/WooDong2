package com.wd.woodong2.domain.usecase

import android.net.Uri
import com.wd.woodong2.domain.repository.ImageStorageRepository
import kotlinx.coroutines.flow.Flow

class ImageStorageSetItemUseCase(
    private val repository: ImageStorageRepository
) {
    suspend operator fun invoke(galleryUri: Uri): Flow<Uri> {
        return repository.getImageUri(galleryUri)
    }
}