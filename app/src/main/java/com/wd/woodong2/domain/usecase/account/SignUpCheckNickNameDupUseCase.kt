package com.wd.woodong2.domain.usecase.account

import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SignUpCheckNickNameDupUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(nickname: String): Boolean {
        return repository.checkNicknameDup(nickname)
    }
}