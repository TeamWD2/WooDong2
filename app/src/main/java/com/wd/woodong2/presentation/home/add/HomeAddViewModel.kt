package com.wd.woodong2.presentation.home.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID


class HomeAddViewModel : ViewModel() {

        private val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
        private val storageReference = FirebaseStorage.getInstance().reference

        fun uploadData(title: String, description: String, selectedImageUri: Uri?, selectedTag: String?, onComplete: () -> Unit) {
            viewModelScope.launch {
                if (selectedImageUri != null) {
                    val storageRef = storageReference.child("images/${UUID.randomUUID()}")
                    storageRef.putFile(selectedImageUri).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                            val newRef = databaseReference.push()
                            val newItemId = newRef.key ?:""
                            val data = HomeAddItem(newItemId, title, description, thumbnail = imageUrl.toString(), tag = selectedTag)
                            newRef.setValue(data)
                            onComplete()
                        }
                    }
                } else {
                    val newRef = databaseReference.push()
                    val newItemId = newRef.key ?: ""
                    val data = HomeAddItem(id = newItemId, title, description, tag = selectedTag)
                    newRef.setValue(data)
                    onComplete()
                }
            }
        }

}
