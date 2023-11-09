package com.wd.woodong2.presentation.home.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.wd.woodong2.presentation.home.content.HomeItem

class HomeDetailViewModel : ViewModel() {
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
        val comment = CommentItem(username = "익명의 우동이", content = commentContent, location = "화정동")
        homeItem.comments.add(comment)
        itemRef.setValue(homeItem)
        _commentsLiveData.value = homeItem.comments
        return homeItem
    }

    fun updateChatCount(homeItem: HomeItem) {
        homeItem.chatCount = homeItem.comments.size
        itemRef.setValue(homeItem)
    }

    fun toggleThumbCount(homeItem: HomeItem) {
    val newCount = if (homeItem.isLiked) homeItem.thumbCount - 1 else homeItem.thumbCount + 1
    homeItem.isLiked = !homeItem.isLiked
    ///itemRef.setValue(homeItem)///
    homeItem.thumbCount = newCount
    itemRef.setValue(homeItem).addOnSuccessListener {
        _thumbCountLiveData.value = homeItem.thumbCount
        _commentsLiveData.value = homeItem.comments
    }.addOnFailureListener {
    }
}

    fun deleteComment(homeItem: HomeItem, commentToDelete: CommentItem) {
        itemRef.child("comments").child(commentToDelete.timestamp.toString()).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                homeItem.comments.remove(commentToDelete)
                _commentsLiveData.value = homeItem.comments
                updateChatCount(homeItem)
            } else {
                task.exception?.let {
                }
            }
        }
    }

}

