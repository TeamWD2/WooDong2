package com.wd.woodong2.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.GCMRequest
import com.wd.woodong2.data.model.UserResponse
import com.wd.woodong2.domain.model.GroupMemberItemEntity
import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.provider.TokenProvider
import com.wd.woodong2.domain.repository.UserRepository
import com.wd.woodong2.retrofit.GCMRetrofitClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth?,
    private val tokenProvider: TokenProvider?,
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
    override fun addUser(user: UserEntity) {
        if (user.id == null) {
            Log.e(TAG, "user.id == null")
            return
        }

        val addItem = mapOf(
            user.id to user
        )

        databaseReference.updateChildren(addItem)

        Log.d(TAG, "addUser 성공")
    }

    /*
    * Auth 회원가입
    * */
    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        imgProfile: Uri?,
    ): Flow<Any> =
        callbackFlow {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful && tokenProvider?.getToken() != null) {

                        val user = UserEntity(
                            id = task.result?.user?.uid,
                            email = email,
                            name = name,
                            chatIds = listOf(),
                            groupIds = listOf(),        //모임
                            likedIds = listOf(),        //좋아요 게시물
                            writtenIds = listOf(),        //작성한 게시물
                            imgProfile = imgProfile.toString(),
                            firstLocation = "",
                            secondLocation = "",
                            token = tokenProvider.getToken()
                        )
                        addUser(user)

                        Log.d(TAG, "${task.result} 성공")
                        trySend(true)
                    }
                }
                ?.addOnFailureListener { exception ->
                    when (exception) {
                        is FirebaseAuthWeakPasswordException -> trySend("ERROR_WEAK_PASSWORD")
                        is FirebaseAuthInvalidCredentialsException -> trySend("ERROR_INVALID_EMAIL")
                        is FirebaseAuthUserCollisionException -> trySend("ERROR_EMAIL_ALREADY_IN_USE")
                        is FirebaseNetworkException -> trySend("ERROR_NETWORK")
                        else -> trySend("ERROR_UNKNOWN")
                    }
                }
            awaitClose { }
        }


    override suspend fun signIn(email: String, password: String): Flow<Boolean> = callbackFlow {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        updateUserToken(uid)
                    }

                    Log.d(TAG, "로그인 성공")
                    trySend(true)
                } else {
                    Log.e(TAG, "로그인 실패")
                    trySend(false)
                }
            }
        awaitClose { }
    }

    override suspend fun updateUserPassword(
        email: String,
        currentPassword: String,
        newPassword: String,
    ) {
        //패스워드 재설정
        try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            auth?.currentUser?.reauthenticate(credential)?.await()
            auth?.currentUser?.updatePassword(newPassword)?.await()
            Log.d(TAG, "비밀번호 변경 성공")
        } catch (e: Exception) {
            Log.e(TAG, "비밀번호 변경 실패")
        }
    }

    /*
    UID 가져오는 메소드
    * */
    override fun getUid(): String? {
        return auth?.currentUser?.uid
    }

    /*
    * Realtime database 유저 토큰 업데이트
    * */
    override fun updateUserToken(userId: String) {
        val userDataReference = databaseReference.child(userId)
        val token = mapOf("token" to tokenProvider?.getToken())
        userDataReference.updateChildren(token)
    }

    override fun updateUserInfo(
        userId: String,
        imgProfile: String,
        name: String,
        firstLocation: String,
        secondLocation: String,
    ) {
        val userInfo = databaseReference.child(userId)
        val updateUserInfo = mapOf(
            "name" to name,
            "imgProfile" to imgProfile,
            "firstLocation" to firstLocation,
            "secondLocation" to secondLocation
        )


        userInfo.updateChildren(updateUserInfo)
    }

    override fun addUserIds(
        userId: String,
        writtenId: String?,
        likedId: String?,
    ) {
        val userIds = databaseReference.child(userId)

        if (writtenId.isNullOrBlank().not()) {
            val writtenIds = userIds.child("writtenIds")
            writtenIds.push().setValue(writtenId) { databaseError, _ ->
                if (databaseError != null) {
                    Log.e(TAG, "Fail: ${databaseError.message}")
                } else {
                    Log.e(TAG, "Success")
                }
            }
        }
        if (likedId.isNullOrBlank().not()) {
            val likedIds = userIds.child("likedIds")
            likedIds.push().setValue(likedId) { databaseError, _ ->
                if (databaseError != null) {
                    Log.e(TAG, "Fail: ${databaseError.message}")
                } else {
                    Log.e(TAG, "Success")
                }
            }
        }
    }

    override fun removeUserIds(
        userId: String,
        writtenId: String?,
        likedId: String?,
        groupId: String?,
        chatId: String?,
    ) {
        val userIds = databaseReference.child(userId)


        fun removeData(childPath: String, value: String?, onSuccess: () -> Unit) {
            if (value.isNullOrBlank().not()) {
                userIds.child(childPath).orderByValue().equalTo(value)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        onSuccess()
                                        Log.d(TAG, "데이터 삭제 성공")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "데이터 삭제 실패: ${e.message}")
                                    }
                                return
                            }
                            Log.w(TAG, "해당 값을 찾지 못했습니다.")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "데이터 읽기 취소: ${error.message}")
                        }
                    })
            }
        }

        removeData("writtenIds", writtenId) {}

        removeData("likedIds", likedId) {}

        removeData("groupIds", groupId) {}

        removeData("chatIds", chatId) {}
    }

    override suspend fun checkNicknameDup(nickname: String): Boolean {
        val query = databaseReference.orderByChild("name").equalTo(nickname)
        val dataSnapshot = query.get().await()
        return dataSnapshot.exists() // 중복이면 true, 아니면 false
    }

    override suspend fun updateGroupInfo(userId: String, groupId: String?, chatId: String?) {
        if (groupId.isNullOrBlank().not()) {
            databaseReference.child(userId).child("groupIds").push()
                .setValue(groupId) { databaseError, _ ->
                    if (databaseError != null) {
                        Log.e(TAG, "Fail: ${databaseError.message}")
                    } else {
                        Log.e(TAG, "Success")
                    }
                }

        }

        if (chatId.isNullOrBlank().not()) {
            databaseReference.child(userId).child("chatIds").push()
                .setValue(chatId) { databaseError, _ ->
                    if (databaseError != null) {
                        Log.e(TAG, "Fail: ${databaseError.message}")
                    } else {
                        Log.e(TAG, "Success")
                    }
                }
        }
    }

    override fun logout() {
        auth?.signOut()
    }

    // 회원 탈퇴
    override suspend fun deleteUser(userId: String) {
        val userIdRef = databaseReference.child(userId)
        userIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userIdRef.removeValue()
                    // Auth에서 삭제
                    auth?.currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Firebase Auth user deleted.")
                        } else {
                            Log.e(TAG, "Failed to delete Firebase Auth user.", task.exception)
                        }
                    }
                } else {
                    Log.d(TAG, "No data found for userId: $userId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }


    /**
     * groupId에 해당하는 사람들에게
     * push 알람 전송 */
    override suspend fun sendPushMessageToGroupMember(memberList: List<GroupMemberItemEntity>?) {
        coroutineScope {
            memberList.orEmpty().forEach { member ->
                launch {
                    getUser(member.userId.orEmpty()).collect { user ->
                        if (user != null) {
                            // TODO FCM Notification 객체 생성
                            val notification = GCMRequest(
                                to = user.token.orEmpty(),
                                data = mapOf("action" to "ChatDetail"),
                                notification = mapOf(
                                    "title" to "새로운 동네 친구 등장!",
                                    "body" to "새로운 분이 모임에 합류했어요! 반갑게 인사해주세요!"
                                ),
                            )

                            try {
                                val response =
                                    GCMRetrofitClient.gcmRemoteSource.sendNotification(notification)

                                if (response.isSuccessful) {
                                    Log.d(
                                        TAG,
                                        "Notification sent successfully. ${
                                            response.body()?.string()
                                        }"
                                    )
                                } else {
                                    Log.e(
                                        TAG,
                                        "Failed to send notification: ${response.code()} ${response.message()}"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Exception during network request.", e)
                            }
                        }
                    }
                }
            }
        }
    }
}