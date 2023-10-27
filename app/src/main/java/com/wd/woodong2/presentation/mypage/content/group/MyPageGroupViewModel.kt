package com.wd.woodong2.presentation.mypage.content.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbViewModel

class MyPageGroupViewModel : ViewModel(){
    private val _list: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val list: LiveData<List<GroupItem>> get() = _list
//    init {
//    }
}
class MyPageGroupViewModelFactory() : ViewModelProvider.Factory {

    val databaseReference by lazy {
        //FirebaseDatabase.getInstance().reference.child("home_list")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageGroupViewModel::class.java)) {
            return MyPageGroupViewModel(
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}