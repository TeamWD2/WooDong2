package com.wd.woodong2.presentation.group.detail.board.add

import android.net.Uri

data class GroupDetailBoardAddImageItem (
    val id: Long?,
    val uri: Uri? = null,
    val isCancelBtn: Boolean = false,
    val isPlusBtn: Boolean = true
)