package com.wd.woodong2.presentation.group.add

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddActivityBinding
import java.util.concurrent.atomic.AtomicLong

class GroupAddActivity: AppCompatActivity() {

    private lateinit var binding: GroupAddActivityBinding

    private val viewModel: GroupAddViewModel by viewModels()

    private val atomicLong = AtomicLong(1L)
    //초기 데이터 세팅
    private val groupAddItems by lazy {
        listOf(
            GroupAddItem.Title("어떤 모임을 만들까요?"),
            GroupAddItem.ChipGroup(true,listOf("운동", "동네친구", "스터디", "가족/육아", "반려동물", "봉사활동", "음식", "투자/금융", "문화/예술", "게임", "음악", "공예/만들기", "기타")),
            GroupAddItem.Divider,
            GroupAddItem.Title("모임을 소개해주세요."),
            GroupAddItem.Description("모임명"),
            GroupAddItem.EditText(atomicLong.getAndIncrement(), 1, 1, "모임명을 입력해주세요."),
            GroupAddItem.Description("모임 소개"),
            GroupAddItem.EditText(atomicLong.getAndIncrement(), 10, 4, "모임 소개를 입력해주세요. 자세할 수록 좋습니다."),
            GroupAddItem.Divider,
            GroupAddItem.Title("어떤 이웃과 함께하고 싶나요?"),
            GroupAddItem.Description("연령대(다중선택 가능)"),
            GroupAddItem.ChipGroup(false, listOf("누구나", "미성년자 제외", "20대", "30대", "40대", "50대", "60대")),
            GroupAddItem.Description("최대 인원"),
            GroupAddItem.ChipGroup(true, listOf("제한없음", "10명", "20명", "30명", "50명", "100명")),
            GroupAddItem.Divider,
            GroupAddItem.Title("비밀번호를 설정할까요?"),
            GroupAddItem.Description("비밀번호"),
            GroupAddItem.Password(atomicLong.getAndIncrement(), "비밀번호를 입력해주세요.", "없음", false),
            GroupAddItem.Divider,
            GroupAddItem.Title("모임 사진을 등록해주세요."),
            GroupAddItem.Description("대표 사진"),
            GroupAddItem.Image(atomicLong.getAndIncrement(), R.drawable.group_add_ic_add_image),
            GroupAddItem.Description("배경 사진"),
            GroupAddItem.Image(atomicLong.getAndIncrement(), R.drawable.group_add_ic_add_image)
        )
    }

    private val groupAddListAdapter by lazy {
        GroupAddListAdapter(
            onCheckBoxChecked = { position, item ->
                viewModel.updateGroupAddItem(position, item)
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
        viewModel.initGroupAddItem(groupAddItems)
    }

    private fun initViewModel() = with(viewModel) {
        groupAddList.observe(this@GroupAddActivity) {
            groupAddListAdapter.submitList(it)
        }
    }
}