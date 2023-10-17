package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.data.model.MessageItemsResponse
import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.MessageRepositoryTest

class MessageRepositoryImplTest(private val databaseReference: DatabaseReference) :
    MessageRepositoryTest {

    override fun getMessageItems(chatId: String, entityResult: (MessageItemsEntity?) -> Unit) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder().create()
                    val chatResponses = snapshot.children.mapNotNull { childSnapshot ->
                        val jsonString = gson.toJson(childSnapshot.value)
                        val response = gson.fromJson(jsonString, ChatResponse::class.java)
                        response.copy(id = childSnapshot.key)
                    }

                    //
                    val messageResponses = chatResponses.find { chatResponse ->
                        chatResponse.id == chatId
                    }?.message ?: emptyList()

                    val entity = MessageItemsResponse(messageResponses).toEntity()
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