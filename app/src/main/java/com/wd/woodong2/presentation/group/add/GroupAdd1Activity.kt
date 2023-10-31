package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAdd1ActivityBinding

class GroupAdd1Activity: AppCompatActivity() {

    companion object {
        fun newAdd1Intent(context: Context): Intent =
            Intent(context, GroupAdd1Activity::class.java)
    }

    private lateinit var binding: GroupAdd1ActivityBinding

    private val viewModel: GroupAddSharedViewModel by viewModels {
        GroupAddSharedViewModelFactory()
    }

    private val add2Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAdd1ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        imgBack.setOnClickListener {
            finish()
        }

        val groupTagChips = listOf(
            R.id.chip_exercise to R.string.group_add_chip_exercise,
            R.id.chip_neighbor_friend to R.string.group_add_chip_neighbor_friend,
            R.id.chip_study to R.string.group_add_chip_study,
            R.id.chip_family to R.string.group_add_chip_family,
            R.id.chip_pet to R.string.group_add_chip_pet,
            R.id.chip_volunteer to R.string.group_add_chip_volunteer,
            R.id.chip_food to R.string.group_add_chip_food,
            R.id.chip_finance to R.string.group_add_chip_finance,
            R.id.chip_arts to R.string.group_add_chip_arts,
            R.id.chip_game to R.string.group_add_chip_game,
            R.id.chip_music to R.string.group_add_chip_music,
            R.id.chip_making to R.string.group_add_chip_making,
            R.id.chip_etc to R.string.group_add_chip_etc
        )

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId != -1) {
                Log.d("sinw", "groupTagChips / ${groupTagChips.find { it.first == checkedId }?.second}")
            }
        }

        edtIntroduceName.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) { //focus 가 다른 뷰로 이동될 때
                Log.d("sinw", "edtIntroduceName / focus 이동")
            }
        }

        edtIntroduceDescription.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) { //focus 가 다른 뷰로 이동될 때
                Log.d("sinw", "edtIntroduceDescription / focus 이동")
            }
        }

        btnNext.setOnClickListener {
            //Todo("입력 예외처리")
            Log.d("sinw", "GroupAdd1 다음 버튼 클릭")
            add2Launcher.launch(
                GroupAdd2Activity.newAdd2Intent(
                    this@GroupAdd1Activity
                )
            )
        }
    }
}