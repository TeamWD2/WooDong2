package com.wd.woodong2.presentation.group.add

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddActivityBinding
import java.util.concurrent.atomic.AtomicLong

class GroupAddActivity : AppCompatActivity() {

    private lateinit var binding: GroupAddActivityBinding

    private val viewModel: GroupAddViewModel by viewModels {
        GroupAddViewModelFactory()
    }

    private lateinit var groupAddSetItem: GroupAddSetItem

    //초기 데이터 세팅
    private val groupAddGetItems by lazy {
        listOf(
            GroupAddGetItem.Title("GroupTendencyTitle", "어떤 모임을 만들까요?"),
            GroupAddGetItem.ChipGroup("GroupTendencyChipG", listOf("운동", "동네친구", "스터디", "가족/육아", "반려동물", "봉사활동", "음식", "투자/금융", "문화/예술", "게임", "음악", "공예/만들기", "기타")),
            GroupAddGetItem.Divider("GroupTendencyDiv"),
            GroupAddGetItem.Title("GroupIntroTitle", "모임을 소개해주세요."),
            GroupAddGetItem.Description("GroupIntroNameDes", "모임명"),
            GroupAddGetItem.EditText("GroupIntroNameEdt", 1, 1, "모임명을 입력해주세요."),
            GroupAddGetItem.Description("GroupIntroDesDes", "모임 소개"),
            GroupAddGetItem.EditText("GroupIntroDesEdt", 10, 4, "모임 소개를 입력해주세요. 자세할 수록 좋습니다."),
            GroupAddGetItem.Divider("GroupIntroDiv"),
            GroupAddGetItem.Title("GroupWithTitle", "어떤 이웃과 함께하고 싶나요?"),
            GroupAddGetItem.Description("GroupWithAgeDes", "연령대"),
            GroupAddGetItem.ChipGroup("GroupWithAgeChipG", listOf("누구나", "만 19세 미만", "만 19세 이상")),
            GroupAddGetItem.Description("GroupWithMemberDes", "최대 인원"),
            GroupAddGetItem.ChipGroup("GroupWithMemberChipG", listOf("제한없음", "10명", "20명", "30명", "50명", "100명")),
            GroupAddGetItem.Divider("GroupWithDiv"),
            GroupAddGetItem.Title("GroupPwTitle", "비밀번호를 설정할까요?"),
            GroupAddGetItem.Description("GroupPwDes", "비밀번호"),
            GroupAddGetItem.Password("GroupPwEdtChk", "비밀번호를 입력해주세요.", "없음", false),
            GroupAddGetItem.Divider("GroupPwDiv"),
            GroupAddGetItem.Title("GroupPhotoTitle", "모임 사진을 등록해주세요."),
            GroupAddGetItem.Description("GroupPhotoMainDes", "대표 사진"),
            GroupAddGetItem.Image("GroupPhotoMainImage", R.drawable.group_add_ic_add_image),
            GroupAddGetItem.Description("GroupPhotoBackDes", "배경 사진"),
            GroupAddGetItem.Image("GroupPhotoBackImage", R.drawable.group_add_ic_add_image)
        )
    }

    private val groupAddListAdapter by lazy {
        GroupAddListAdapter(
            onCreateGroupAdd = { position, text ->
                createGroupAdd(position, text)
            },
            onCheckBoxChecked = { position, item ->
                viewModel.updatePasswordChecked(position, item)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewAddGroup.adapter = groupAddListAdapter
        viewModel.initGroupAddItem(groupAddGetItems)

        groupAddSetItem = GroupAddSetItem()
        //테스트용 임시 사용자 계정 추가 (모임 생성하는 방장 - 최소 멤버로 가입)
        groupAddSetItem = groupAddSetItem.copy(
            memberList = listOf(
                Member(
                    "-NhImSiData",
                    "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
                    "sinw"
                )
            )
        )

        btnAddGroup.setOnClickListener {
            if(isCorrectGroupAddItem(groupAddSetItem)) {
                Toast.makeText(this@GroupAddActivity, "모임이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                viewModel.setGroupAddItem(groupAddSetItem)
                finish()
            } else {
                Toast.makeText(this@GroupAddActivity, "입력되지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        groupAddList.observe(this@GroupAddActivity) {
            groupAddListAdapter.submitList(it)
        }
    }

    private fun isCorrectGroupAddItem(groupAddSetItem: GroupAddSetItem) =
        groupAddSetItem.groupTag.isNullOrBlank().not()
                && groupAddSetItem.title.isNullOrBlank().not()
                && groupAddSetItem.introduce.isNullOrBlank().not()
                && groupAddSetItem.ageLimit.isNullOrBlank().not()
                && groupAddSetItem.memberLimit.isNullOrBlank().not()
                && groupAddSetItem.password.isNullOrBlank().not()
                && groupAddSetItem.mainImage.isNullOrBlank().not()
                && groupAddSetItem.backgroundImage.isNullOrBlank().not()

    private fun createGroupAdd(position: Int, text: String) {
        groupAddSetItem = when (groupAddGetItems[position].id) {
            "GroupTendencyChipG" -> groupAddSetItem.copy(groupTag = text)
            "GroupIntroNameEdt" -> groupAddSetItem.copy(title = text)
            "GroupIntroDesEdt" -> groupAddSetItem.copy(introduce = text)
            "GroupWithAgeChipG" -> groupAddSetItem.copy(ageLimit = text)
            "GroupWithMemberChipG" -> groupAddSetItem.copy(memberLimit = text)
            "GroupPwEdtChk" -> groupAddSetItem.copy(password = text)
            "GroupPhotoMainImage" -> groupAddSetItem.copy(mainImage = text)
            "GroupPhotoBackImage" -> groupAddSetItem.copy(backgroundImage = text)
            else -> groupAddSetItem.copy()
        }
        Log.d("sinw", "$groupAddSetItem")
    }
}