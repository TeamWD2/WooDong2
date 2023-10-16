package com.wd.woodong2.presentation.group.add

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddActivityBinding

class GroupAddActivity: AppCompatActivity() {

    private lateinit var binding: GroupAddActivityBinding

    private val groupAddItems = listOf(
        GroupAddItem.Title("어떤 모임을 만들까요?"),
        GroupAddItem.ChipGroup(listOf("운동", "동네친구", "스터디", "가족/육아", "반려동물", "봉사활동", "음식", "투자/금융", "문화/예술", "게임", "음악", "공예/만들기", "기타")),
        GroupAddItem.Divider,
        GroupAddItem.Title("모임을 소개해주세요."),
        GroupAddItem.Description("모임명"),
        GroupAddItem.EditText(""),
        GroupAddItem.Description("모임 소개"),
        GroupAddItem.EditText(""),
        GroupAddItem.Divider,
        GroupAddItem.Title("어떤 이웃과 함께하고 싶나요?"),
        GroupAddItem.Description("연령대"),
        GroupAddItem.ChipGroup(listOf("누구나", "20대", "30대", "40대", "50대", "60대")),
        GroupAddItem.Description("최대 인원"),
        GroupAddItem.ChipGroup(listOf("제한없음", "10명", "20명", "30명", "50명", "100명")),
        GroupAddItem.Divider,
        GroupAddItem.Title("모임 사진을 등록해주세요."),
        GroupAddItem.Description("대표 사진"),
        GroupAddItem.Image(R.drawable.group_add_ic_add_image),
        GroupAddItem.Description("배경 사진"),
        GroupAddItem.Image(R.drawable.group_add_ic_add_image)
    )

    private val groupAddListAdapter by lazy {
        GroupAddListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        recyclerViewAddGroup.adapter = groupAddListAdapter
        groupAddListAdapter.submitList(groupAddItems)
    }
}