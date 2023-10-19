package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class MessageItemsResponse(
    val messageItems: Map<String, MessageResponse>?
)