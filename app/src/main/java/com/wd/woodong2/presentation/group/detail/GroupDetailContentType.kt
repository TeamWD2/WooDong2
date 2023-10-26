package com.wd.woodong2.presentation.group.detail

enum class GroupDetailContentType {
    WRITE_BOARD, JOIN_GROUP;

    companion object {
        fun from(name: String?): GroupDetailContentType? {
            return GroupDetailContentType.values().find {
                it.name.uppercase() == name?.uppercase()
            }
        }
    }
}