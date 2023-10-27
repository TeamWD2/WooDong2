package com.wd.woodong2.presentation.mypage.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MyPageUpdateActivityBinding
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.mypage.content.MyPageFragment


class MyPageUpdateActivity : AppCompatActivity(){

    companion object {
        lateinit var userInfo: UserItem
        fun newIntent(context: Context, userItem: UserItem)=//MutableLiveData<UserItem>)=
            Intent(context, MyPageUpdateActivity::class.java).apply {
                userInfo = userItem
            }

    }

    private lateinit var binding : MyPageUpdateActivityBinding

    private val editUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                userInfo.imgProfile = result.data?.data.toString()

                Glide.with(this)
                    .load(Uri.parse(userInfo.imgProfile))
                    .error(R.drawable.group_ic_no_image)
                    .fitCenter()
                    .into(binding.myPageUpdateUserImgProfile)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPageUpdateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() {

    }

    private fun initView() = with(binding) {

        myPageUpdateClose.setOnClickListener{
            finish()
        }
        myPageUpdateBtn.setOnClickListener{
            userInfo.name = myPageUpdateEtUserNameEdit.text.toString()
            userInfo.email = myPageUpdateEtUserEmailEdit.text.toString()

            val intent = Intent().apply{
                putExtra(
                    MyPageFragment.EXTRA_USER_NAME,
                    userInfo.name
                )
                putExtra(
                    MyPageFragment.EXTRA_USER_PROFILE,
                    userInfo.imgProfile
                )
                putExtra(
                    MyPageFragment.EXTRA_USER_EMAIL,
                    userInfo.email
                )
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        myPageUpdateEtUserEmailEdit.setText(userInfo.email)
        myPageUpdateEtUserNameEdit.setText(userInfo.name)


        val imgProfileUri = if (userInfo.imgProfile != null) Uri.parse(userInfo.imgProfile) else null

        Glide.with(this@MyPageUpdateActivity)
            .load(imgProfileUri)
            .error(R.drawable.group_ic_no_image)
            .fitCenter()
            .into(myPageUpdateUserImgProfile)

        myPageUpdateIvUserImgProfileEdit.setOnClickListener{
            //갤러리로 가기
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            editUserLauncher.launch(intent)
        }

        myPageUpdateIvUserNameEdit.setOnClickListener{
            myPageUpdateEtUserNameEdit.isEnabled = !myPageUpdateEtUserNameEdit.isEnabled
        }

        myPageUpdateIvUserEmailEdit.setOnClickListener{
            myPageUpdateEtUserEmailEdit.isEnabled = !myPageUpdateEtUserEmailEdit.isEnabled
        }
    }
}