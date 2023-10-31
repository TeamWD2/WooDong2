package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAdd2ActivityBinding
import java.util.regex.Pattern

class GroupAdd2Activity: AppCompatActivity() {

    companion object {
        fun newAdd2Intent(context: Context): Intent =
            Intent(context, GroupAdd2Activity::class.java)
    }

    private lateinit var binding: GroupAdd2ActivityBinding

    private val viewModel: GroupAddSharedViewModel by viewModels {
        GroupAddSharedViewModelFactory()
    }

    private val add3Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK, Intent())
            finish()
        }
    }

    private var isChkBoxChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAdd2ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        imgBack.setOnClickListener {
            finish()
        }

        val ageLimitChips = listOf(
            R.id.chip_anyone to R.string.group_add_chip_anyone,
            R.id.chip_nineteen_under to R.string.group_add_chip_nineteen_under,
            R.id.chip_nineteen_over to R.string.group_add_chip_nineteen_over
        )

        val memberLimitChips = listOf(
            R.id.chip_no_limit to R.string.group_add_chip_no_limit,
            R.id.chip_5_people to R.string.group_add_chip_5_people,
            R.id.chip_10_people to R.string.group_add_chip_10_people,
            R.id.chip_20_people to R.string.group_add_chip_20_people,
            R.id.chip_30_people to R.string.group_add_chip_30_people,
            R.id.chip_50_people to R.string.group_add_chip_50_people
        )

        chipGroupAgeLimit.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId != -1) {
                Log.d("sinw", "ageLimitChips / ${ageLimitChips.find { it.first == checkedId }?.second}")
            }
        }

        chipGroupMemberLimit.setOnCheckedChangeListener { _, checkedId ->
            if(checkedId != -1) {
                Log.d("sinw", "memberLimitChips / ${memberLimitChips.find { it.first == checkedId }?.second}")
            }
        }

        if(!isChkBoxChecked) {
            edtPassword.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.group_border_box)
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        val pwdPattern = Pattern.matches(
                            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$",
                            text.toString().trim()
                        )
                        if (pwdPattern) {
                            txtPasswordValid.apply {
                                setText(R.string.group_add_txt_password_valid)
                                setTextColor(Color.GREEN)
                                Log.d("sinw", "txtPasswordValid / Valid")
                            }
                        } else {
                            txtPasswordValid.apply {
                                setText(R.string.group_add_txt_password_invalid)
                                setTextColor(Color.RED)
                            }
                        }
                    }
                })
            }
        } else {
            edtPassword.apply {
                isEnabled = false
                setBackgroundResource(R.drawable.group_border_box_disabled)
                Log.d("sinw", "txtPassword / [WD2] No Password")
            }
            txtPasswordValid.text = ""
        }

        checkBox.setOnCheckedChangeListener { _, isChkBox ->
            Log.d("sinw", "checkBox / $isChkBox")
        }

        btnNext.setOnClickListener {
            //Todo("입력 예외처리")
            Log.d("sinw", "GroupAdd2 다음 버튼 클릭")
            add3Launcher.launch(
                GroupAdd3Activity.newAdd3Intent(
                    this@GroupAdd2Activity
                )
            )
        }
    }
}