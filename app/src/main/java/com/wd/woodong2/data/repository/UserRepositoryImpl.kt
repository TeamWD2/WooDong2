package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.UserItemsResponse
import com.wd.woodong2.data.model.UserResponse
import com.wd.woodong2.domain.model.UserItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.UserRepository

class UserRepositoryImpl(private val databaseReference: DatabaseReference) : UserRepository {

    override fun getUserItems(
        entityResult: (UserItemsEntity?) -> Unit
    ) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder().create()
                    val userResponses = snapshot.children.mapNotNull { childSnapshot ->
                        val jsonString = gson.toJson(childSnapshot.value)
                        val response = gson.fromJson(jsonString, UserResponse::class.java)
                        response.copy(id = childSnapshot.key)
                    }
                    val entity = UserItemsResponse(userResponses).toEntity()
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

