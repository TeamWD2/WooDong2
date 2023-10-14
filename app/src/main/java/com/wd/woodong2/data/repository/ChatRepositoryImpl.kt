package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.ChatItemsResponse
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.ChatRepository

class ChatRepositoryImpl(private val databaseReference: DatabaseReference) : ChatRepository {

    override fun getChatItems(entityResult: (ChatItemsEntity?) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder().create()
                    val chatResponses = snapshot.children.mapNotNull { childSnapshot ->
                        val jsonString = gson.toJson(childSnapshot.value)
                        val response = gson.fromJson(jsonString, ChatResponse::class.java)
                        response.copy(id = childSnapshot.key)
                    }
                    val entity = ChatItemsResponse(chatResponses).toEntity()
                    entityResult(entity)
                } else {
                    entityResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("sinw", "$error")
                entityResult(null)
            }
        })
    }
}

