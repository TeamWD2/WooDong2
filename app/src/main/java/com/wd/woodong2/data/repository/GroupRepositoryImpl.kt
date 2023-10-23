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
import com.wd.woodong2.presentation.group.add.GroupAddSetItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GroupRepositoryImpl(private val databaseReference: DatabaseReference) : GroupRepository {
    override suspend fun getGroupItems(): Flow<GroupItemsEntity> = callbackFlow {

        val listener = databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder().create()
                    val groupResponses = snapshot.children.mapNotNull { childSnapshot ->
                        val jsonString = gson.toJson(childSnapshot.value)
                        val response = gson.fromJson(jsonString, GroupResponse::class.java)
                        response.copy(id = childSnapshot.key)
                    }
                    val entity = GroupItemsResponse(groupResponses).toEntity()
                    trySend(entity)
                } else {
                    //snapshot 이 존재하지 않는 경우
                    trySend(GroupItemsEntity(emptyList()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        awaitClose {
            Log.d("sinw", "awaitClose")
            databaseReference.removeEventListener(listener)
        }
    }

    override suspend fun setGroupItem(groupAddSetItem: GroupAddSetItem) {
        val groupRef = databaseReference.push()
        groupRef.setValue(groupAddSetItem) { databaseError, _ ->
            if(databaseError != null) {
                Log.e("sinw", "Fail: ${databaseError.message}")
            } else {
                Log.e("sinw", "Success")
            }
        }
    }
}