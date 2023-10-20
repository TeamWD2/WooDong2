package com.wd.woodong2.presentation.home.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HomeAddViewModel : ViewModel() {

        private val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
        private val storageReference = FirebaseStorage.getInstance().reference

        fun uploadData(title: String, description: String, selectedImageUri: Uri?, selectedTag: String?, onComplete: () -> Unit) {
            viewModelScope.launch {
                if (selectedImageUri != null) {
                    val storageRef = storageReference.child("images/${UUID.randomUUID()}")
                    storageRef.putFile(selectedImageUri).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                            val data = HomeAddItem(title, description, thumbnail = imageUrl.toString(), tag = selectedTag)
                            val newRef = databaseReference.push()
                            newRef.setValue(data)
                            onComplete()
                        }
                    }
                } else {
                    val data = HomeAddItem(title, description, tag = selectedTag)
                    val newRef = databaseReference.push()
                    newRef.setValue(data)
                    onComplete()
                }
            }
        }

}
