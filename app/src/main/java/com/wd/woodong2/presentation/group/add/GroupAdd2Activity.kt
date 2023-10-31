package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAdd2ActivityBinding
import java.util.regex.Pattern

class GroupAdd2Activity : AppCompatActivity() {

    companion object {
        private const val ADD_ITEM_GROUP_TAG = "add_item_group_tag"
        private const val ADD_ITEM_GROUP_NAME = "add_item_group_name"
        private const val ADD_ITEM_GROUP_INTRO = "add_item_group_intro"
        fun newAdd2Intent(
            context: Context,
            groupTag: String?,
            groupName: String,
            groupIntro: String
        ): Intent =
            Intent(context, GroupAdd2Activity::class.java).apply {
                putExtra(ADD_ITEM_GROUP_TAG, groupTag)
                putExtra(ADD_ITEM_GROUP_NAME, groupName)
                putExtra(ADD_ITEM_GROUP_INTRO, groupIntro)
            }
    }

    private lateinit var binding: GroupAdd2ActivityBinding

    private val add3Launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(RESULT_OK, Intent())
                finish()
            }
        }

    private val groupTag by lazy {
        intent.getStringExtra(ADD_ITEM_GROUP_TAG)
    }
    private val groupName by lazy {
        intent.getStringExtra(ADD_ITEM_GROUP_NAME)
    }
    private val groupIntro by lazy {
        intent.getStringExtra(ADD_ITEM_GROUP_INTRO)
    }

    private var ageLimit: String? = null
    private var memberLimit: String? = null
    private var password: String? = null

    private val textWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val pwdPattern = Pattern.matches(
                    "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$",
                    binding.edtPassword.text.toString().trim()
                )
                if (pwdPattern) {
                    binding.txtPasswordValid.apply {
                        setText(R.string.group_add_txt_password_valid)
                        setTextColor(Color.GREEN)
                        password = binding.edtPassword.text.toString()
                    }
                } else {
                    binding.txtPasswordValid.apply {
                        setText(R.string.group_add_txt_password_invalid)
                        setTextColor(Color.RED)
                    }
                }
            }
        }
    }

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

        chipGroupAgeLimit.setOnCheckedChangeListener { _, checkedId ->
            ageLimit = when (checkedId) {
                R.id.chip_anyone -> getString(R.string.group_add_chip_anyone)
                R.id.chip_nineteen_under -> getString(R.string.group_add_chip_nineteen_under)
                R.id.chip_nineteen_over -> getString(R.string.group_add_chip_nineteen_over)
                else -> null
            }
        }

        chipGroupMemberLimit.setOnCheckedChangeListener { _, checkedId ->
            memberLimit = when (checkedId) {
                R.id.chip_no_limit -> getString(R.string.group_add_chip_no_limit)
                R.id.chip_5_people -> getString(R.string.group_add_chip_5_people)
                R.id.chip_10_people -> getString(R.string.group_add_chip_10_people)
                R.id.chip_20_people -> getString(R.string.group_add_chip_20_people)
                R.id.chip_30_people -> getString(R.string.group_add_chip_30_people)
                R.id.chip_50_people -> getString(R.string.group_add_chip_50_people)
                else -> null
            }
        }

        edtPassword.addTextChangedListener(textWatcher)

        checkBox.setOnCheckedChangeListener { _, isChkBox ->
            edtPassword.apply {
                isEnabled = !isChkBox
                if (isChkBox) {
                    setBackgroundResource(R.drawable.group_border_box_disabled)
                    removeTextChangedListener(textWatcher)
                    password = "[WD2] No Password"
                    txtPasswordValid.text = ""
                } else {
                    setBackgroundResource(R.drawable.public_border_box)
                    addTextChangedListener(textWatcher)
                    password = ""
                }
            }
        }

        btnNext.setOnClickListener {
            if (
                groupTag.isNullOrEmpty()
                || groupName.isNullOrEmpty()
                || groupIntro.isNullOrEmpty()
                || ageLimit.isNullOrEmpty()
                || memberLimit.isNullOrEmpty()
                || password.isNullOrEmpty()
            ) {
                Toast.makeText(this@GroupAdd2Activity, R.string.group_add_toast_no_info, Toast.LENGTH_SHORT).show()
            } else {
                add3Launcher.launch(
                    GroupAdd3Activity.newAdd3Intent(
                        this@GroupAdd2Activity,
                        groupTag,
                        groupName,
                        groupIntro,
                        ageLimit,
                        memberLimit,
                        password
                    )
                )
            }
        }
    }
}