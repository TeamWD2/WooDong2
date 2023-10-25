package com.wd.woodong2.presentation.mypage.content.thumb


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wd.woodong2.presentation.home.content.HomeItem

class MyPageThumbViewModel : ViewModel(){
    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list
    init {
        loadDataFromFirebase()
    }
    private fun loadDataFromFirebase() {
        MyPageThumbViewModelFactory().databaseReference.addValueEventListener(object : ValueEventListener {
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
class MyPageThumbViewModelFactory() : ViewModelProvider.Factory {

    val databaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("home_list")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageThumbViewModel::class.java)) {
            return MyPageThumbViewModel(
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}