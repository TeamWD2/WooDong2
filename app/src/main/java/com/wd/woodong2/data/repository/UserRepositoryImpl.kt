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
import com.wd.woodong2.domain.provider.TokenProvider
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
    private val tokenProvider: TokenProvider,
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

    /*
    * Realtime database에 user 추가
    * */
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

    /*
    * Auth 회원가입
    * */
    override suspend fun signUp(email: String, password: String, name: String): Flow<Boolean> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val user = UserEntity(
                                id = task.result?.user?.uid,
                                email = email,
                                name = name,
                                chatIds = emptyList(),
                                imgProfile = "",
                                firstLocation = "",
                                secondLocation = "",
                                token = tokenProvider.getToken()
                            )
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
                    CoroutineScope(Dispatchers.IO).launch {
                        updateUserToken(email)
                    }

                    Log.d(TAG, "로그인 성공")
                    trySend(true)
                } else {
                    Log.d(TAG, "로그인 실패")
                    trySend(false)
                }
            }
        awaitClose { }
    }

    /*
    * ID 중복확인 메소드
    * */
    override suspend fun isIdDuplicated(email: String): Flow<Boolean> = callbackFlow {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(task.result?.signInMethods?.isEmpty() ?: true)
                } else {
                    trySend(false)
                }
            }
        awaitClose { }
    }


    override suspend fun updateUserToken(userId: String): Flow<Boolean> = callbackFlow {
        val userDataReference = databaseReference.child(userId)
        val token = mapOf("token" to tokenProvider.getToken())
        userDataReference.updateChildren(token)
    }

    override fun updateUserLocations(
        userId: String,
        firstLocation: String,
        secondLocation: String,
    ) {
        val userLocations = databaseReference.child(userId)
        val locations = mapOf(
            "firstLocation" to firstLocation,
            "secondLocation" to secondLocation
        )
        Log.d("location", firstLocation)
        userLocations.updateChildren(locations)
    }

    override fun updateUserInfo(userId: String, name: String, imgProfile: String, email: String) {
        val userInfo = databaseReference.child(userId)
        val updateUserData = mapOf(
            "name" to name,
            "imgProfile" to imgProfile,
            "email" to email,
        )
        userInfo.updateChildren(updateUserData)
    }
}

