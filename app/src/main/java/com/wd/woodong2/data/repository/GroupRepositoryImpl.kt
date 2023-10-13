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

class GroupRepositoryImpl(private val databaseReference: DatabaseReference): GroupRepository {

    override fun getGroupItems(
        entityResult: (GroupItemsEntity?) -> Unit
    ) {
        Log.d("sinw", "repositoryImpl")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("sinw", "snapshot / $snapshot")
                    val gson = GsonBuilder().create()
                    val groupResponses = snapshot.children.mapNotNull { childSnapshot ->
                        val jsonString = gson.toJson(childSnapshot.value)
                        val response = gson.fromJson(jsonString, GroupResponse::class.java)
                        response.copy(id = childSnapshot.key)
                    }
                    val entity = GroupItemsResponse(groupResponses).toEntity()
                    entityResult(entity)
//                    val jsonString = gson.toJson(snapshot.value)
//                    Log.d("sinw", "snapshot / $jsonString")
//                    val entity = gson.fromJson(jsonString, GroupItemsResponse::class.java).toEntity()
//                    entityResult(entity)
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

