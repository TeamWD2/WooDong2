package com.wd.woodong2.presentation.home.detail

import android.content.Context
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
import com.wd.woodong2.domain.usecase.UserAddIdsUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.UserRemoveIdsUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.add.HomeAddViewModel
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.home.content.HomeViewModel

class HomeDetailViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userAddIdsUseCase: UserAddIdsUseCase,
    private val userRemoveIdsUseCase: UserRemoveIdsUseCase,
) : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private lateinit var itemRef: DatabaseReference

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

    fun postComment(homeItem: HomeItem, commentContent: String) {
        val comment = CommentItem(username = "익명의 우동이", content = commentContent, location = "화정동")
        homeItem.comments.add(comment)
        itemRef.setValue(homeItem)
    }

    fun updateChatCount(homeItem: HomeItem) {
        homeItem.chatCount = homeItem.comments.size
        itemRef.setValue(homeItem)
    }

    fun toggleThumbCount(homeItem: HomeItem) {
        if (!homeItem.isLiked) {
            homeItem.thumbCount += 1
            userAddIdsUseCase(getUserInfo()?.id ?: "UserId",null,homeItem.id)
        } else {
            homeItem.thumbCount -= 1
            userRemoveIdsUseCase(getUserInfo()?.id ?: "UserId",null,homeItem.id,null,null)
        }
        homeItem.isLiked = !homeItem.isLiked
        itemRef.setValue(homeItem)

    }
    fun deleteComment(homeItem: HomeItem, commentToDelete: CommentItem) {
        itemRef.child("comments").child(commentToDelete.timestamp.toString()).removeValue()
        homeItem.comments.remove(commentToDelete)
        updateChatCount(homeItem)
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
            FirebaseDatabase.getInstance().getReference("users"),
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
