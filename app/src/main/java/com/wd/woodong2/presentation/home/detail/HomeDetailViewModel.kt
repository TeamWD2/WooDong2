package com.wd.woodong2.presentation.home.detail

import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.wd.woodong2.presentation.home.content.HomeItem

class HomeDetailViewModel : ViewModel() {
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
        } else {
            homeItem.thumbCount -= 1
        }
        homeItem.isLiked = !homeItem.isLiked
        itemRef.setValue(homeItem)
        
    }
    fun deleteComment(homeItem: HomeItem, commentToDelete: CommentItem) {
        itemRef.child("comments").child(commentToDelete.timestamp.toString()).removeValue()
        homeItem.comments.remove(commentToDelete)
        updateChatCount(homeItem)
    }


}
