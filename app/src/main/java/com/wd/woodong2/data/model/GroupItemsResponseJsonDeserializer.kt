package com.wd.woodong2.data.model

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.wd.woodong2.common.GroupViewType
import java.lang.reflect.Type

class GroupItemsResponseJsonDeserializer : JsonDeserializer<GroupItemsResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GroupItemsResponse {
        val items = arrayListOf<GroupResponse>()
        runCatching {
            json?.asJsonObject?.entrySet()?.forEach { entry ->
                val id = entry.key
                entry.value.asJsonArray.forEach { item ->
                    val itemObject = item.asJsonObject
                    itemObject.addProperty("id", id)
                    val viewType = itemObject.get("viewType").asString
                    when (viewType.uppercase()) {
                        GroupViewType.MAIN.name -> context?.deserialize<GroupMainResponse>(
                            itemObject,
                            GroupMainResponse::class.java
                        )

                        GroupViewType.INTRODUCE.name -> context?.deserialize<GroupIntroduceResponse>(
                            itemObject,
                            GroupIntroduceResponse::class.java
                        )

                        GroupViewType.MEMBER.name -> context?.deserialize<GroupMemberResponse>(
                            itemObject,
                            GroupMemberResponse::class.java
                        )

                        GroupViewType.BOARD.name -> context?.deserialize<GroupBoardResponse>(
                            itemObject,
                            GroupBoardResponse::class.java
                        )

                        GroupViewType.ALBUM.name -> context?.deserialize<GroupAlbumResponse>(
                            itemObject,
                            GroupAlbumResponse::class.java
                        )

                        else -> null

                    }?.also {
                        items.add(it)
                    }
                }
            }
        }

        return GroupItemsResponse(
            items
        )
    }
}