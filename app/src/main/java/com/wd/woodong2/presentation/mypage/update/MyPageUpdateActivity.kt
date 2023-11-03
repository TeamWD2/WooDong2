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

    private var profile = userInfo.imgProfile
    private var name = userInfo.name
    private var password = userInfo.email
    private var passwordCheck = userInfo.email
    private lateinit var binding : MyPageUpdateActivityBinding

    private val editUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                profile = result.data?.data.toString()

                Glide.with(this)
                    .load(Uri.parse(profile))
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
            name = myPageUpdateEtUserNameEdit.text.toString()
            password = myPageUpdateEtUserPasswordEdit.text.toString()
            passwordCheck = myPageUpdateEtUserPasswordEdit.text.toString()

            val intent = Intent().apply{
                putExtra(
                    MyPageFragment.EXTRA_USER_NAME,
                    name
                )
                putExtra(
                    MyPageFragment.EXTRA_USER_PROFILE,
                    profile
                )
                putExtra(
                    MyPageFragment.EXTRA_USER_PASSWORD,
                    password
                )
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        myPageUpdateEtUserPasswordEdit.setText(password)
        myPageUpdateEtUserPasswordCheckEdit.setText(passwordCheck)
        myPageUpdateEtUserNameEdit.setText(name)


        val imgProfileUri = if (profile != null) Uri.parse(profile) else null

        Glide.with(this@MyPageUpdateActivity)
            .load(imgProfileUri)
            .error(R.drawable.group_ic_no_image)
            .fitCenter()
            .into(myPageUpdateUserImgProfile)

        myPageUpdateUserImgProfile.setOnClickListener{
            //갤러리로 가기
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            editUserLauncher.launch(intent)
        }

        myPageUpdateEtUserNameEdit.setOnClickListener{
            myPageUpdateEtUserNameEdit.isEnabled = !myPageUpdateEtUserNameEdit.isEnabled
        }

        myPageUpdateEtUserPasswordEdit.setOnClickListener{
            myPageUpdateEtUserPasswordEdit.isEnabled = !myPageUpdateEtUserPasswordEdit.isEnabled
        }
        myPageUpdateEtUserPasswordCheckEdit.setOnClickListener{
            myPageUpdateEtUserPasswordCheckEdit.isEnabled = !myPageUpdateEtUserPasswordCheckEdit.isEnabled
        }
    }
}