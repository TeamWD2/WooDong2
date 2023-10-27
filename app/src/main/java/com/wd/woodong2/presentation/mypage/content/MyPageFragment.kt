package com.wd.woodong2.presentation.mypage.content

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MyPageFragmentBinding
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.mypage.update.MyPageUpdateActivity
class MyPageFragment : Fragment() {

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_USER_PROFILE = "extra_user_profile"
        const val EXTRA_USER_EMAIL = "extra_user_email"
        fun newInstance() = MyPageFragment()
        fun extractLocationInfo(address: String): String {
            val parts = address.split(" ")
            for (part in parts) {
                if (part.contains("동") || part.contains("읍") || part.contains("면")) {
                    return part
                }
            }
            return ""
        }
    }
    private lateinit var editUserLauncher : ActivityResultLauncher<Intent>

    private lateinit var UserInfo : UserItem
    private var _binding : MyPageFragmentBinding? = null
    private val binding get() = _binding!!

    private var myPageViewPagerAdapter :MyPageViewPagerAdapter? = null

    private val viewModel : MyPageViewModel //= MyPageViewModelFactory().create(MyPageViewModel::class.java)
        by activityViewModels {
            MyPageViewModelFactory()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?     //이전 상태에 대한 데이터 제공
    ): View {
        _binding = MyPageFragmentBinding.inflate(inflater, container, false)

        editUserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val receivedNameData = result.data?.getStringExtra(EXTRA_USER_NAME)
                    val receivedProfileData = result.data?.getStringExtra(EXTRA_USER_PROFILE)
                    val receivedEmailData = result.data?.getStringExtra(EXTRA_USER_EMAIL)
                    viewModel.updateUserItem(receivedNameData.toString(),receivedProfileData.toString(),receivedEmailData.toString())
                }else{

                }
            }//무조건 있는건 아니다..

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //myPageViewPagerAdapter = MyPageViewPagerAdapter(this)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {

        myPageViewPagerAdapter = MyPageViewPagerAdapter(this@MyPageFragment, lifecycle)
        myPageViewPager2.adapter = myPageViewPagerAdapter
        myPageViewPager2.offscreenPageLimit = myPageViewPagerAdapter!!.itemCount


        TabLayoutMediator(myPageTabLayout, myPageViewPager2) { tab, position ->
            tab.setText(myPageViewPagerAdapter!!.getTitle(position))
        }.attach()

        //프로필 변경
        ivUserEdit.setOnClickListener{
            UserInfo.let { notNullUser ->
                editUserLauncher.launch(
                    MyPageUpdateActivity.newIntent(requireContext(), notNullUser)
                )
            }
        }
        btnUpdateProfile.setOnClickListener{
            UserInfo.let { notNullUser ->
                editUserLauncher.launch(
                    MyPageUpdateActivity.newIntent(requireContext(), notNullUser)
                )
            }
        }
    }

    private fun initViewModel(){
        with(viewModel){
            userInfo.observe(viewLifecycleOwner){userInfo->
                UserInfo = userInfo
                Glide.with(requireContext())
                    .load(Uri.parse(userInfo.imgProfile))
                    .error(R.drawable.group_ic_no_image)
                    .fitCenter()
                    .into(binding.ivProfile)
                binding.tvName.text = userInfo.name
                binding.tvLocation.text = extractLocationInfo(userInfo.firstLocation.toString())
            }
        }
    }


//    override fun onPause() {
//        super.onPause()
//       binding.run {
//           myPageViewPager2.adapter = null
//       }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.run {
//            myPageViewPager2.adapter = myPageViewPagerAdapter
//            initView()
//            initViewModel()
//        }
//    } or LIFECYCLE을 강제로 ACTIVITYLIFECYCLE을 넣어서 사용 하는 방법..

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}