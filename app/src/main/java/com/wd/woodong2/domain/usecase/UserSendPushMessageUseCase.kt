package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.GroupMemberItemEntity
import com.wd.woodong2.domain.repository.UserRepository

class UserSendPushMessageUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(memberList: List<GroupMemberItemEntity>?) {
        repository.sendPushMessageToGroupMember(memberList)
    }
}