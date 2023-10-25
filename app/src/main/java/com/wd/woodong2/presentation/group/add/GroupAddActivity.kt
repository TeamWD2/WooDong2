package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddActivityBinding

class GroupAddActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, GroupAddActivity::class.java)
    }

    private lateinit var binding: GroupAddActivityBinding

    private val viewModel: GroupAddViewModel by viewModels {
        GroupAddViewModelFactory()
    }

    private var groupAddMain: GroupAddSetItem.GroupAddMain? = null
    private var groupAddIntroduce: GroupAddSetItem.GroupAddIntroduce? = null
    private var groupAddMember: GroupAddSetItem.GroupAddMember? = null

    //초기 데이터 세팅
    private val groupAddGetItems by lazy {
        listOf(
            GroupAddGetItem.Title("GroupTendencyTitle", "어떤 모임을 만들까요?"),
            GroupAddGetItem.ChipGroup("GroupTendencyChipG", listOf("운동", "동네친구", "스터디", "가족/육아", "반려동물", "봉사활동", "음식", "투자/금융", "문화/예술", "게임",                 "음악", "공예/만들기", "기타")),
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
            GroupAddGetItem.Image("GroupPhotoMainImage", null),
            GroupAddGetItem.Description("GroupPhotoBackDes", "배경 사진"),
            GroupAddGetItem.Image("GroupPhotoBackImage", null)
        )
    }

    private var currentPosition: Int = -1
    private lateinit var currentItem: GroupAddGetItem.Image

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.updateImage(currentPosition, currentItem, uri)
                    createGroupAdd(currentPosition, uri.toString())
                }
            }
        }

    private val groupAddListAdapter by lazy {
        GroupAddListAdapter(
            onCreateGroupAdd = { position, text ->
                createGroupAdd(position, text)
            },
            onCheckBoxChecked = { position, item ->
                viewModel.updatePasswordChecked(position, item)
            },
            onClickImage = { position, item ->
                currentPosition = position
                currentItem = item
                galleryLauncher.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
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
        viewModel.initGroupAddItems(groupAddGetItems)

        //테스트용 임시 사용자 계정 추가 (모임 생성하는 방장 - 최소 멤버로 가입)
        groupAddMember = GroupAddSetItem.GroupAddMember(
            title = "멤버",
            memberList = listOf(
                GroupAddSetItem.AddMember(
                    "-NhImSiData",
                    "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
                    "sinw",
                    "권선동"
                )
            )
        )
        groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(
            memberCount = (groupAddMain?.memberCount ?: 0) + 1
        )

        btnAddGroup.setOnClickListener {
            Log.d("sinw", "$groupAddIntroduce / $groupAddMember")
            if (isCorrectGroupAddItem()) {
                Toast.makeText(this@GroupAddActivity, getString(R.string.group_add_toast_create_group), Toast.LENGTH_SHORT).show()
                val groupAddSetItem = mutableListOf<GroupAddSetItem>().apply {
                    groupAddMain?.let {
                        add(it)
                    }
                    groupAddIntroduce?.let {
                        add(it)
                    }
                    groupAddMember?.let {
                        add(it)
                    }
                }
                viewModel.setGroupAddItem(groupAddSetItem)
                finish()
            } else {
                Toast.makeText(this@GroupAddActivity, getString(R.string.group_add_toast_no_info), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        groupAddList.observe(this@GroupAddActivity) {
            groupAddListAdapter.submitList(it)
        }
    }

    private fun isCorrectGroupAddItem(): Boolean =
        isCorrectMain(groupAddMain)
                && isCorrectIntroduce(groupAddIntroduce)
                && isCorrectMember(groupAddMember)

    private fun isCorrectMain(groupAddMain: GroupAddSetItem.GroupAddMain?): Boolean =
        groupAddMain?.let {
            it.groupName.isNullOrBlank().not()
                    && it.groupTag.isNullOrBlank().not()
                    && it.ageLimit.isNullOrBlank().not()
                    && it.memberLimit.isNullOrBlank().not()
                    && it.password.isNullOrBlank().not()
                    && it.mainImage.isNullOrBlank().not()
                    && it.backgroundImage.isNullOrBlank().not()
                    && it.memberCount != 0
        } ?: false

    private fun isCorrectIntroduce(groupAddIntroduce: GroupAddSetItem.GroupAddIntroduce?): Boolean =
        groupAddIntroduce?.let {
            it.title.isNullOrBlank().not()
                    && it.introduce.isNullOrBlank().not()
                    && it.groupTag.isNullOrBlank().not()
                    && it.ageLimit.isNullOrBlank().not()
                    && it.memberLimit.isNullOrBlank().not()
        } ?: false

    private fun isCorrectMember(groupAddMember: GroupAddSetItem.GroupAddMember?): Boolean =
        groupAddMember?.let {
            it.title.isNullOrBlank().not()
                    && it.memberList.isNullOrEmpty().not()
        } ?: false

    //Refactoring 예정
    private fun createGroupAdd(position: Int, text: String) {
        when (groupAddGetItems[position].id) {
            "GroupTendencyChipG" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(groupTag = text)
                groupAddIntroduce = (groupAddIntroduce ?: GroupAddSetItem.GroupAddIntroduce(title = "소개")).copy(groupTag = text)
            }
            "GroupIntroNameEdt" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(groupName = text)
            }
            "GroupIntroDesEdt" -> {
                groupAddIntroduce = (groupAddIntroduce ?: GroupAddSetItem.GroupAddIntroduce(title = "소개")).copy(introduce = text)
            }
            "GroupWithAgeChipG" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(ageLimit = text)
                groupAddIntroduce = (groupAddIntroduce ?: GroupAddSetItem.GroupAddIntroduce(title = "소개")).copy(ageLimit = text)
            }
            "GroupWithMemberChipG" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(memberLimit = text)
                groupAddIntroduce = (groupAddIntroduce ?: GroupAddSetItem.GroupAddIntroduce(title = "소개")).copy(memberLimit = text)
            }
            "GroupPwEdtChk" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(password = text)
            }
            "GroupPhotoMainImage" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(mainImage = text)
            }
            "GroupPhotoBackImage" -> {
                groupAddMain = (groupAddMain ?: GroupAddSetItem.GroupAddMain()).copy(backgroundImage = text)
            }
            else -> groupAddIntroduce
        }
    }
}