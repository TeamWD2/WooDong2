package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.UserResponse
import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class UserRepositoryImpl(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
) : UserRepository {
    companion object {
        const val TAG = "UserRepositoryImpl"
    }

    override suspend fun getUserItems(): Flow<UserItemsEntity?> {
        TODO("Not yet implemented")
    }

//    override fun getUserItems(
//        userId: String,
//        entityResult: (UserItemsEntity?) -> Unit
//    ) {
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val gson = GsonBuilder().create()
//                    val userResponses = snapshot.children.mapNotNull { childSnapshot ->
//                        val jsonString = gson.toJson(childSnapshot.value)
//                        val response = gson.fromJson(jsonString, UserResponse::class.java)
//                        response.copy(id = childSnapshot.key)
//                    }.filter {
//                        it.id == userId
//                    }
//                    val entity = UserItemsResponse(userResponses).toEntity()
//                    entityResult(entity)
//                } else {
//                    entityResult(null)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("sinw", "$error")
//                entityResult(null)
//            }
//        })
//    }

    override suspend fun getUser(userId: String): Flow<UserEntity?> =
        callbackFlow {
            val listener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gson = GsonBuilder().create()

                        val userListResponses =
                            snapshot.children.mapNotNull { childSnapshot ->
                                val jsonString = gson.toJson(childSnapshot.value)
                                val response = gson.fromJson(jsonString, UserResponse::class.java)
                                response.copy(id = childSnapshot.key)
                            }

                        val userResponse = userListResponses.find {
                            it.id == userId
                        }

                        val entity: UserEntity? = userResponse?.toEntity()
                        trySend(entity)
                    } else {
                        throw RuntimeException("snapshot is not exists")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
            awaitClose {
                databaseReference.removeEventListener(listener)
            }
        }

    override suspend fun addUser(user: UserEntity) {
        if (user.id == null) {
            Log.e(TAG, "user.id == null")
            return
        }

        val addItem = mapOf(
            user.id to user
        )

        databaseReference.setValue(addItem)

        Log.d(TAG, "addUser 성공")
    }

    override suspend fun signUp(email: String, password: String, name: String): Flow<Boolean> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = UserEntity(
                            id = task.result?.user?.uid,
                            email = email,
                            name = name,
                            chatIds = listOf(),
                            imgProfile = null,
                            firstLocation = null,
                            secondLocation = null
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            addUser(user)
                        }

                        Log.d(TAG, "$task 성공")

                        trySend(true)
                    } else {
                        trySend(false)
                    }
                }
            awaitClose { }
        }

    override suspend fun signIn(email: String, password: String): Flow<Boolean> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "로그인 성공")
                    trySend(true)
                } else {
                    Log.d(TAG, "로그인 실패")
                    trySend(false)
                }
            }
        awaitClose { }
    }
}

