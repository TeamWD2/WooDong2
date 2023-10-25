package com.wd.woodong2.presentation.mypage.content

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.databinding.MyPageFragmentBinding
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.mypage.update.MyPageUpdateActivity
class MyPageFragment : Fragment() {

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_USER_PROFILE = "extra_user_profile"
        const val EXTRA_USER_EMAIL = "extra_user_EMAIL"
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
    private val editUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val receivedNameData = result.data?.getStringExtra(EXTRA_USER_NAME)
                val receivedProfileData = result.data?.getStringExtra(EXTRA_USER_PROFILE)
                val receivedEmailData = result.data?.getStringExtra(EXTRA_USER_EMAIL)
                viewModel.updateUserItem(receivedNameData,receivedProfileData,receivedEmailData)

//                Glide.with(requireContext())
//                    .load(
//                        Uri.parse(viewModel.userInfo.imgProfile))
//                    .into(binding.ivProfile)
//                binding.tvName.text = viewModel.userInfo.name
            }
        }

    private lateinit var UserInfo : UserItem
    private var _binding : MyPageFragmentBinding? = null
    private val binding get() = _binding!!

    private val myPageViewPagerAdapter by lazy {
        MyPageViewPagerAdapter(this)
    }
    private val viewModel : MyPageViewModel by viewModels {
        MyPageViewModelFactory()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }
    private fun initView() = with(binding) {
        myPageViewPager2.adapter = myPageViewPagerAdapter
        myPageViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        })
        myPageViewPager2.offscreenPageLimit = myPageViewPagerAdapter.itemCount

        TabLayoutMediator(myPageTabLayout, myPageViewPager2) { tab, position ->
            tab.setText(myPageViewPagerAdapter.getTitle(position))
        }.attach()


        val user: UserItem = viewModel.userInfo.value!!

        Glide.with(requireContext())
            .load(Uri.parse(user.imgProfile))
            .into(binding.ivProfile)
        binding.tvName.text = user.name
        binding.tvLocation.text = extractLocationInfo(user.firstLocation.toString())

        //프로필 변경
        ivUserEdit.setOnClickListener{
            editUserLauncher.launch(
                MyPageUpdateActivity.newIntent(requireContext(),user)
            )
        }
        btnUpdateProfile.setOnClickListener{
            editUserLauncher.launch(
                MyPageUpdateActivity.newIntent(requireContext(),user)
            )
        }
    }

    private fun initViewModel(){
        with(viewModel){
            userInfo.observe(viewLifecycleOwner){userInfo->
                Glide.with(requireContext())
                    .load(Uri.parse(userInfo.imgProfile))
                    .into(binding.ivProfile)
                binding.tvName.text = userInfo.name
                binding.tvLocation.text = extractLocationInfo(userInfo.firstLocation.toString())
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}