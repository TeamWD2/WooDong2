package com.wd.woodong2.presentation.home.detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.user.UserAddIdsUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.user.UserRemoveIdsUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.home.map.HomeMapActivity

class HomeDetailViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userAddIdsUseCase: UserAddIdsUseCase,
    private val userRemoveIdsUseCase: UserRemoveIdsUseCase,
) : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private lateinit var itemRef: DatabaseReference

    private val _commentsLiveData = MutableLiveData<List<CommentItem>>()
    val commentsLiveData: LiveData<List<CommentItem>> = _commentsLiveData

    private val _thumbCountLiveData = MutableLiveData<Int>()
    val thumbCountLiveData: LiveData<Int> = _thumbCountLiveData


    fun fetchComments(homeItem: HomeItem, onDataChangeCallback: (List<CommentItem>) -> Unit) {
        itemRef = database.getReference("home_list").child(homeItem.id)
        itemRef.child("comments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updatedComments =
                    dataSnapshot.children.mapNotNull { it.getValue(CommentItem::class.java) }
                homeItem.comments = updatedComments.toMutableList()
                onDataChangeCallback(updatedComments)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun postComment(homeItem: HomeItem, commentContent: String): HomeItem {
        val currentUser = getUserInfo()
        val comment = CommentItem(
            username = currentUser?.name ?: "알 수 없음",
            content = commentContent,
            location = HomeMapActivity.extractLocationInfo(currentUser?.firstLocation ?: ""),
            userImageUrl = currentUser?.imgProfile ?: "")
        homeItem.comments.add(comment)
        itemRef.setValue(homeItem)
        _commentsLiveData.value = homeItem.comments
        return homeItem
    }

    fun updateChatCount(homeItem: HomeItem) {
        val updateMap = mapOf("chatCount" to homeItem.comments.size)
        itemRef.updateChildren(updateMap)
    }

    fun toggleThumbCount(homeItem: HomeItem) {
        val currentUserID = getUserInfo()?.id ?: "UserId"
        val isLiked = currentUserID in homeItem.likedBy

        if (isLiked) {
            homeItem.likedBy.remove(currentUserID)
            homeItem.thumbCount--
        } else {
            homeItem.likedBy.add(currentUserID)
            homeItem.thumbCount++
        }

        val updateMap = mapOf(
            "likedBy" to homeItem.likedBy,
            "thumbCount" to homeItem.thumbCount
        )

        // updateChildren 메소드를 사용하여 데이터베이스 업데이트
        itemRef.updateChildren(updateMap).addOnSuccessListener {
            _thumbCountLiveData.value = homeItem.thumbCount
        }.addOnFailureListener {
            // 에러 처리
        }
    }

    fun deleteComment(homeItem: HomeItem, commentToDelete: CommentItem) {
        itemRef.child("comments").child(commentToDelete.timestamp.toString()).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                homeItem.comments.remove(commentToDelete)

                // 업데이트할 데이터 맵을 준비
                val updateMap = mapOf(
                    "comments" to homeItem.comments,
                    "chatCount" to homeItem.comments.size
                )

                // Firebase 데이터베이스 업데이트
                itemRef.updateChildren(updateMap).addOnSuccessListener {
                    _commentsLiveData.value = homeItem.comments
                    // 다른 필요한 UI 업데이트 처리
                }.addOnFailureListener {
                    // 에러 처리
                }
            } else {
                // 삭제 실패 시 에러 처리
                task.exception?.let {
                    // 에러 처리
                }
            }
        }
    }


    fun getUserInfo() =
        prefGetUserItem()?.let {
            UserItem(
                id = it.id ?: "unknown",
                name = it.name ?: "unknown",
                imgProfile = it.imgProfile,
                email = it.email ?: "unknown",
                chatIds = it.chatIds,
                groupIds = it.groupIds,
                likedIds = it.likedIds,
                writtenIds = it.writtenIds,
                firstLocation = it.firstLocation ?: "unknown",
                secondLocation = it.secondLocation ?: "unknown"
            )
        }
}
class HomeDetailViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory{
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    private val databaseReference = FirebaseDatabase.getInstance()

    val userPrefRepository = UserPreferencesRepositoryImpl(
        null,
        UserInfoPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        )
    )

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("user_list"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeDetailViewModel::class.java)) {
            return HomeDetailViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserAddIdsUseCase(userRepositoryImpl),
                UserRemoveIdsUseCase(userRepositoryImpl)
                ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}

