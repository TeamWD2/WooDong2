package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import com.wd.woodong2.common.GroupViewType
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupItemsResponseJsonDeserializer
import com.wd.woodong2.data.model.GroupMemberItemResponse
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMemberItemEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.add.GroupAddSetItem
import com.wd.woodong2.presentation.group.detail.GroupDetailMemberAddItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class GroupRepositoryImpl(private val databaseReference: DatabaseReference) : GroupRepository {
    companion object {
        const val TAG = "GroupRepositoryImpl"
    }

    /**
     * 모임 전체 데이터 가져오기
     * */
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


    /**
     * groupId에 해당하는 그룹 MainEntity 반환
     * */
    override suspend fun getGroupItem(groupId: String): Flow<GroupItemsEntity?> = callbackFlow {
        val listener =
            databaseReference.child(groupId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gson = GsonBuilder()
                            .registerTypeAdapter(
                                GroupItemsResponse::class.java,
                                GroupItemsResponseJsonDeserializer()
                            ).create()

                        // TODO 임시
                        val jsonString = "{\"$groupId\":" + gson.toJson(snapshot.value) + "}"

                        val groupItemsResponse = gson.fromJson(
                            jsonString,
                            GroupItemsResponse::class.java
                        )

                        val entity = groupItemsResponse?.toEntity()

                        trySend(entity)
                    } else {
                        //snapshot 이 존재하지 않는 경우
                        trySend(null)
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

    /**
     * groupId에 해당하는 group의
     * MemberList 반환
     * */
    override suspend fun getGroupMemberList(groupId: String): Flow<List<GroupMemberItemEntity>?> =
        callbackFlow {
            val listener =
                databaseReference.child(groupId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach { groupSnapshot ->
                                if (groupSnapshot.hasChild("memberList")) {
                                    val gson = GsonBuilder().create()

                                    val memberListSnapshot = groupSnapshot.child("memberList")

                                    val memberListResponses =
                                        memberListSnapshot.children.mapNotNull { childSnapshot ->
                                            val jsonString = gson.toJson(childSnapshot.value)
                                            val response = gson.fromJson(
                                                jsonString,
                                                GroupMemberItemResponse::class.java
                                            )
                                            response.copy()
                                        }

                                    val memberList = memberListResponses.map { it.toEntity() }

                                    trySend(memberList)
                                }
                            }
                        } else {
                            //snapshot 이 존재하지 않는 경우
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

    override fun setGroupBoardItem(
        itemId: String,
        groupBoardItem: GroupDetailBoardAddItem,
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

    override fun setGroupAlbumItem(
        itemId: String,
        groupAlbumItems: List<String>,
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

    override fun addGroupBoardComment(
        itemId: String,
        groupId: String,
        boardComment: GroupDetailBoardDetailItem.BoardComment,
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

    override fun deleteGroupBoardComment(
        itemId: String,
        boardId: String,
        commentId: String,
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

    override fun setGroupMemberItem(
        itemId: String,
        groupMemberItem: GroupDetailMemberAddItem,
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

    override suspend fun deleteGroupBoardItem(itemId: String, boardId: String): Flow<List<String>?> =
        callbackFlow {
            val boardListQuery = databaseReference.child(itemId).orderByChild("viewType").equalTo("board")
            boardListQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { childSnapshot ->
                        val boardRef = childSnapshot.ref.child("boardList").child(boardId)
                        boardRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(boardSnapshot: DataSnapshot) {
                                val imageList = boardSnapshot.child("images").children.map { it.value.toString() }
                                boardRef.removeValue() // 게시글 삭제
                                trySend(imageList)
                                close()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                throw error.toException()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })

            awaitClose {
                Log.e(TAG, "awaitClose")
            }
        }

    override fun deleteGroupAlbumItem(itemId: String, imageList: List<String>) {
        imageList.forEach { imageUrl ->
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            storageRef.delete().addOnSuccessListener { // Firebase Storage 이미지 삭제
                Log.d(TAG, "Success : Delete Firebase Storage")

                val albumListQuery = databaseReference.child(itemId).orderByChild("viewType").equalTo("album")
                albumListQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { childSnapshot ->
                            val albumRef = childSnapshot.ref.child("images")
                            albumRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.firstOrNull { it.value == imageUrl }?.ref?.removeValue() // 앨범 데이터 이미지 삭제
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    throw error.toException()
                                }
                            })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }
                })
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Fail : Delete Firebase Storage - $exception")
            }
        }
    }
}