package com.wd.woodong2.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ChatResponseDeserializer : JsonDeserializer<ChatResponse> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ChatResponse {
        val jsonObject = json.asJsonObject

        val id = jsonObject["id"]?.asString
        val groupId = jsonObject["groupId"]?.asString
        val last: MessageResponse? = if (jsonObject.has("last") && !jsonObject["last"].isJsonNull) {
            context.deserialize(jsonObject["last"], MessageResponse::class.java)
        } else null
        val mainImage = jsonObject["mainImage"]?.asString
        val memberLimit = jsonObject["memberLimit"]?.asString
        var message: Map<String, MessageResponse>? = null
        if (jsonObject.has("message") && !jsonObject["message"].isJsonNull) {
            val messageElement = jsonObject["message"]
            if (messageElement.isJsonObject) {
                message = context.deserialize(messageElement, object : TypeToken<Map<String, MessageResponse>>() {}.type)
            } else {
                println("Warning: 'message' field is not a JSON object: $messageElement")
            }
        }
        val title = jsonObject["title"]?.asString

        var lastSeemTime: Map<String?, Long>? = null
        if (jsonObject.has("lastSeemTime") && !jsonObject["lastSeemTime"].isJsonNull) {
            val lastSeemTimeElement = jsonObject["lastSeemTime"]
            if (lastSeemTimeElement.isJsonPrimitive) {
                try {
                    val longValue = lastSeemTimeElement.asLong
                    lastSeemTime = mapOf(null to longValue)
                } catch (e: Exception) {
                    println("lastSeemTime could not be parsed as Long: $lastSeemTimeElement")
                }
            } else if (lastSeemTimeElement.isJsonObject) {
                lastSeemTime = context.deserialize(lastSeemTimeElement, object : TypeToken<Map<String?, Long>>() {}.type)
            }
        }

        // ChatResponse 객체 생성
        return ChatResponse(id, groupId, last, mainImage,  memberLimit, message, title, lastSeemTime)
    }
}