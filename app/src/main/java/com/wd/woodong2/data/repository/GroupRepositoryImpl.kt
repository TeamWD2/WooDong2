package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupResponse
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.GroupRepository
import kotlinx.coroutines.tasks.await

class GroupRepositoryImpl(private val databaseReference: DatabaseReference) : GroupRepository {

    override suspend fun getGroupItems(): GroupItemsEntity {
        val snapshot = databaseReference.get().await()
        val gson = GsonBuilder().create()
        val groupResponses = snapshot.children.mapNotNull { childSnapshot ->
            val jsonString = gson.toJson(childSnapshot.value)
            val response = gson.fromJson(jsonString, GroupResponse::class.java)
            response.copy(id = childSnapshot.key)
        }
        return GroupItemsResponse(groupResponses).toEntity()
    }
}

