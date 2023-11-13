package com.wd.woodong2.presentation.mypage.content

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MyPageFragmentBinding
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.mypage.update.MyPageUpdateActivity
import com.wd.woodong2.presentation.signin.SignInActivity

class MyPageFragment : Fragment() {

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
        const val EXTRA_USER_PROFILE = "extra_user_profile"

        lateinit var UserInfo: UserItem
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

    private lateinit var editUserLauncher: ActivityResultLauncher<Intent>


    private var _binding: MyPageFragmentBinding? = null
    private val binding get() = _binding!!

    private var myPageViewPagerAdapter: MyPageViewPagerAdapter? = null

    private var imgCheck: Boolean = false
    private val viewModel: MyPageViewModel by viewModels {
        MyPageViewModelFactory(
            requireContext(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,     //이전 상태에 대한 데이터 제공
    ): View {
        _binding = MyPageFragmentBinding.inflate(inflater, container, false)

        editUserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val receivedNameData = result.data?.getStringExtra(EXTRA_USER_NAME)
                    val receivedProfileData = result.data?.getStringExtra(EXTRA_USER_PROFILE)

                    if(receivedNameData.isNullOrEmpty().not()&&receivedProfileData.isNullOrEmpty().not()){
                        viewModel.updateUserItem(
                            receivedNameData.toString(),
                            receivedProfileData.toString()
                        )
                        Log.d("mypage2", receivedNameData.toString())
                        viewModel.userInfo.value = viewModel.editPrefUserInfo(
                            receivedNameData.toString(),
                            receivedProfileData.toString(),
                            viewModel.userInfo.value?.firstLocation,
                            viewModel.userInfo.value?.secondLocation)
                        Log.d("check",viewModel.userInfo.value?.name.toString())
                        Glide.with(requireContext())
                            .load(Uri.parse(receivedProfileData))
                            .error(R.drawable.public_default_wd2_ivory)
                            .fitCenter()
                            .into(binding.ivProfile)
                    }

                    else if(receivedNameData.isNullOrEmpty().not()){
                        viewModel.updateUserItem(
                            receivedNameData.toString(),
                            viewModel.userInfo.value?.imgProfile.toString()
                        )
                        Log.d("mypage2", receivedNameData.toString())
                        viewModel.userInfo.value = viewModel.editPrefUserInfo(
                            receivedNameData.toString(),
                            viewModel.userInfo.value?.imgProfile.toString(),
                            viewModel.userInfo.value?.firstLocation,
                            viewModel.userInfo.value?.secondLocation)
                        Log.d("check",viewModel.userInfo.value?.name.toString())
                    }

                    else if(receivedProfileData.isNullOrEmpty().not()){
                        viewModel.updateUserItem(
                            viewModel.userInfo.value?.name.toString(),
                            receivedProfileData.toString()
                        )
                        Log.d("mypage2", receivedNameData.toString())
                        viewModel.userInfo.value = viewModel.editPrefUserInfo(
                            viewModel.userInfo.value?.name.toString(),
                            receivedProfileData.toString(),
                            viewModel.userInfo.value?.firstLocation,
                            viewModel.userInfo.value?.secondLocation)
                        Log.d("check",viewModel.userInfo.value?.name.toString())
                        Glide.with(requireContext())
                            .load(Uri.parse(receivedProfileData))
                            .error(R.drawable.public_default_wd2_ivory)
                            .fitCenter()
                            .into(binding.ivProfile)
                    }
                    //imgCheck = true
                } else {

                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        //로그아웃
        toolbarMyPage.setRightIcOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton(
                    "확인"
                ) { _, _ ->
                    viewModel.logout()

                    /*
                    * 로그인 화면으로 돌아가기
                    * */
                    startActivity(
                        Intent(context, SignInActivity::class.java)
                    )
                    activity?.finish()
                }
                .setNegativeButton(
                    "취소"
                ) { _, _ ->
                }

            builder.show()
        }

        myPageViewPagerAdapter =
            MyPageViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        myPageViewPager2.adapter = myPageViewPagerAdapter
        myPageViewPager2.offscreenPageLimit = myPageViewPagerAdapter?.itemCount ?: 0

        TabLayoutMediator(myPageTabLayout, myPageViewPager2) { tab, position ->
            myPageViewPagerAdapter?.getTitle(position)?.let { tab.setText(it) }
        }.attach()


        //프로필 변경
        ivUserEdit.setOnClickListener {
            UserInfo.let { notNullUser ->
                editUserLauncher.launch(
                    MyPageUpdateActivity.newIntent(requireContext(), notNullUser)
                )
            }
        }
        userEdit.setOnClickListener {
            UserInfo.let { notNullUser ->
                editUserLauncher.launch(
                    MyPageUpdateActivity.newIntent(requireContext(), notNullUser)
                )
            }
        }
//        if (imgCheck) {
//            ivProfile.setOnClickListener {
//                val builder = AlertDialog.Builder(requireContext())
//                builder.setTitle("기본 이미지 변경 여부")
//                    .setMessage("기본 이미지로 변경 하시겠습니까?.")
//                    .setPositiveButton(
//                        "확인"
//                    ) { _, _ ->
//                        viewModel.updateUserItem(
//                            UserInfo.name.toString(),
//                            R.drawable.group_ic_no_profile.toString()
//                        )
//                        Glide.with(requireContext())
//                            .load(R.drawable.group_ic_no_profile)
//                            .error(R.drawable.group_ic_no_profile)
//                            .fitCenter()
//                            .into(binding.ivProfile)
//                        imgCheck = false
//                    }
//                    .setNegativeButton(
//                        "취소"
//                    ) { _, _ ->
//                    }
//                // 다이얼로그를 띄워주기
//                builder.show()
//            }
//        }
    }

    private fun initViewModel() {
        with(viewModel) {
            userInfo.observe(viewLifecycleOwner) { userInfo ->
                UserInfo = userInfo
                Glide.with(requireContext())
                    .load(Uri.parse(userInfo.imgProfile))
                    .error(R.drawable.public_default_wd2_ivory)
                    .fitCenter()
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