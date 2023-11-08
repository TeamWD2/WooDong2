package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.wd.woodong2.data.model.UserResponse
import com.wd.woodong2.domain.model.ChatItemsEntity
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
import kotlinx.coroutines.tasks.await

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
                            groupIds= listOf(),        //모임
                            likedIds= listOf(),        //좋아요 게시물
                            writtenIds= listOf(),        //작성한 게시물
                            imgProfile = "",
                            firstLocation = "",
                            secondLocation = "",
                            token = ""
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
    override suspend fun updateUserPassword(email: String, currentPassword: String, newPassword: String): Flow<Boolean> =
        callbackFlow {
        //패스워드 재설정
        try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            auth.currentUser?.reauthenticate(credential)?.await()
            auth.currentUser?.updatePassword(newPassword)?.await()
            Log.d(TAG, "비밀번호 변경 성공")
            trySend(true)
        } catch (e: Exception) {
            Log.d(TAG, "비밀번호 변경 실패")
            trySend(false)
        }
        awaitClose {

        }
    }
    override fun updateUserInfo(userId: String, imgProfile: String, name: String, firstLocation: String, secondLocation: String)
    {
            Log.d("locationcf", firstLocation)
            Log.d("locationcf", secondLocation)
            val userInfo = databaseReference.child(userId)
            val updateUserInfo = mapOf(
                "name" to name,
                "imgProfile" to imgProfile,
                "firstLocation" to firstLocation,
                "secondLocation" to secondLocation
                )


            userInfo.updateChildren(updateUserInfo)
        }

    override suspend fun addUserIds(userId: String,writtenId: String?, groupId: String?, likedId: String?): Flow<UserEntity?> =
        callbackFlow {
            val userIds = databaseReference.child(userId)

            if (!writtenId.isNullOrBlank()) {
                val writtenIds = userIds.child("writtenIds")
                writtenIds.push().setValue(writtenId)
            }
            if (!groupId.isNullOrBlank()) {
                val groupIds = userIds.child("groupIds")
                groupIds.push().setValue(groupId)
            }
            if (!likedId.isNullOrBlank()) {
                val likedIds = userIds.child("likedIds")
                likedIds.push().setValue(likedId)
            }

            awaitClose {

            }
        }

}

