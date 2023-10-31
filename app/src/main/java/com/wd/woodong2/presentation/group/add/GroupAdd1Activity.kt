package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAdd1ActivityBinding

class GroupAdd1Activity : AppCompatActivity() {

    companion object {
        fun newAdd1Intent(context: Context): Intent =
            Intent(context, GroupAdd1Activity::class.java)
    }

    private lateinit var binding: GroupAdd1ActivityBinding

    private val add2Launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                finish()
            }
        }

    private var groupTag: String? = null

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

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            groupTag = when (checkedId) {
                R.id.chip_exercise -> getString(R.string.group_add_chip_exercise)
                R.id.chip_neighbor_friend -> getString(R.string.group_add_chip_neighbor_friend)
                R.id.chip_study -> getString(R.string.group_add_chip_study)
                R.id.chip_family -> getString(R.string.group_add_chip_family)
                R.id.chip_pet -> getString(R.string.group_add_chip_pet)
                R.id.chip_volunteer -> getString(R.string.group_add_chip_volunteer)
                R.id.chip_food -> getString(R.string.group_add_chip_food)
                R.id.chip_finance -> getString(R.string.group_add_chip_finance)
                R.id.chip_arts -> getString(R.string.group_add_chip_arts)
                R.id.chip_game -> getString(R.string.group_add_chip_game)
                R.id.chip_music -> getString(R.string.group_add_chip_music)
                R.id.chip_making -> getString(R.string.group_add_chip_making)
                R.id.chip_etc -> getString(R.string.group_add_chip_etc)
                else -> null
            }
        }

        btnNext.setOnClickListener {
            if (
                groupTag.isNullOrEmpty()
                || edtIntroduceName.text.isNullOrEmpty()
                || edtIntroduceDescription.text.isNullOrEmpty()
            ) {
                Toast.makeText(this@GroupAdd1Activity, R.string.group_add_toast_no_info, Toast.LENGTH_SHORT).show()
            } else {
                add2Launcher.launch(
                    GroupAdd2Activity.newAdd2Intent(
                        this@GroupAdd1Activity,
                        groupTag,
                        edtIntroduceName.text.toString(),
                        edtIntroduceDescription.text.toString()
                    )
                )
            }
        }
    }
}