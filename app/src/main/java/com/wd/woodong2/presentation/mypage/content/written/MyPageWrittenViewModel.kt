package com.wd.woodong2.presentation.mypage.content.written

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbViewModel

class MyPageWrittenViewModel : ViewModel(){
    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list
    init {
        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        MyPageWrittenViewModelFactory().databaseReference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = ArrayList<HomeItem>()

                for (postSnapshot in dataSnapshot.children) {
                    val firebaseData = postSnapshot.getValue(HomeItem::class.java)
                    if (firebaseData != null) {
                        dataList.add(firebaseData)
                    }
                }
                _list.value = dataList
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
class MyPageWrittenViewModelFactory : ViewModelProvider.Factory {

    val databaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("home_list")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageWrittenViewModel::class.java)) {
            return MyPageWrittenViewModel(
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}