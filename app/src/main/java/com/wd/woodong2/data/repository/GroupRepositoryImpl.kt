package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.common.GroupViewType
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupItemsResponseJsonDeserializer
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.add.GroupAddSetItem
import com.wd.woodong2.presentation.group.detail.GroupDetailMemberAddItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GroupRepositoryImpl(private val databaseReference: DatabaseReference) : GroupRepository {
    companion object {
        const val TAG = "GroupRepositoryImpl"
    }

    override suspend fun getGroupItems(): Flow<GroupItemsEntity> = callbackFlow {
        val listener = databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder()
                        .registerTypeAdapter(
                            GroupItemsResponse::class.java,
                            GroupItemsResponseJsonDeserializer()
                        ).create()
                    val jsonString = gson.toJson(snapshot.value)
                    val entity = gson.fromJson(
                        jsonString,
                        GroupItemsResponse::class.java
                    ).toEntity()
                    trySend(entity)
                } else {
                    //snapshot 이 존재하지 않는 경우
                    trySend(GroupItemsResponse(emptyList()).toEntity())
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

    override suspend fun setGroupItem(groupAddSetItem: List<GroupAddSetItem>): String {
        val groupRef = databaseReference.push()
        val groupKey = groupRef.key
        groupRef.setValue(groupAddSetItem) { databaseError, _ ->
            if (databaseError != null) {
                Log.e(TAG, "Fail: ${databaseError.message}")
            } else {
                Log.e(TAG, "Success")
            }
        }
        return groupKey.toString()
    }

    override suspend fun setGroupBoardItem(
        itemId: String,
        groupBoardItem: GroupDetailBoardAddItem
    ) {
        databaseReference.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val viewType = childSnapshot.child("viewType").value as? String
                    if (viewType?.uppercase() == GroupViewType.BOARD.name) {
                        childSnapshot.ref.child("boardList").push().setValue(groupBoardItem)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    override suspend fun setGroupAlbumItem(
        itemId: String,
        groupAlbumItems: List<String>
    ) {
        databaseReference.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val viewType = childSnapshot.child("viewType").value as? String
                    if (viewType?.uppercase() == GroupViewType.ALBUM.name) {
                        childSnapshot.ref.child("images").apply {
                            groupAlbumItems.forEach { item ->
                                push().setValue(item)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    override suspend fun addGroupBoardComment(
        itemId: String,
        groupId: String,
        boardComment: GroupDetailBoardDetailItem.BoardComment
    ) {
        databaseReference.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val viewType = childSnapshot.child("viewType").value as? String
                    if (viewType?.uppercase() == GroupViewType.BOARD.name) {
                        childSnapshot.ref.child("boardList").child(groupId)
                            .child("commentList").push().setValue(boardComment)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    override suspend fun deleteGroupBoardComment(
        itemId: String,
        boardId: String,
        commentId: String
    ) {
        databaseReference.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val viewType = childSnapshot.child("viewType").value as? String
                    if (viewType?.uppercase() == GroupViewType.BOARD.name) {
                        childSnapshot.ref.child("boardList").child(boardId)
                            .child("commentList").child(commentId).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    override suspend fun setGroupMemberItem(
        itemId: String,
        groupMemberItem: GroupDetailMemberAddItem
    ) {
        databaseReference.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val viewType = childSnapshot.child("viewType").value as? String
                    if (viewType?.uppercase() == GroupViewType.MEMBER.name) {
                        childSnapshot.ref.child("memberList").push().setValue(groupMemberItem)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }
}