package com.wd.woodong2.presentation.group.add

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddWithFragmentBinding
import java.util.regex.Pattern

class GroupAddWithFragment: Fragment() {
    companion object {
        fun newInstance() = GroupAddWithFragment()
    }

    private var _binding: GroupAddWithFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupAddSharedViewModel by activityViewModels {
        GroupAddSharedViewModelFactory()
    }

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
                        sharedViewModel.setItem("password", binding.edtPassword.text.toString())
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupAddWithFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        chipGroupAgeLimit.setOnCheckedChangeListener { _, checkedId ->
            val ageLimit = when (checkedId) {
                R.id.chip_anyone -> getString(R.string.group_add_chip_anyone)
                R.id.chip_nineteen_under -> getString(R.string.group_add_chip_nineteen_under)
                R.id.chip_nineteen_over -> getString(R.string.group_add_chip_nineteen_over)
                else -> null
            }
            sharedViewModel.setItem("ageLimit", ageLimit)
        }

        chipGroupMemberLimit.setOnCheckedChangeListener { _, checkedId ->
            val memberLimit = when (checkedId) {
                R.id.chip_no_limit -> getString(R.string.group_add_chip_no_limit)
                R.id.chip_5_people -> getString(R.string.group_add_chip_5_people)
                R.id.chip_10_people -> getString(R.string.group_add_chip_10_people)
                R.id.chip_20_people -> getString(R.string.group_add_chip_20_people)
                R.id.chip_30_people -> getString(R.string.group_add_chip_30_people)
                R.id.chip_50_people -> getString(R.string.group_add_chip_50_people)
                else -> null
            }
            sharedViewModel.setItem("memberLimit", memberLimit)
        }

        edtPassword.addTextChangedListener(textWatcher)

        checkBox.setOnCheckedChangeListener { _, isChkBox ->
            edtPassword.apply {
                isEnabled = !isChkBox
                if (isChkBox) {
                    setBackgroundResource(R.drawable.group_border_box_disabled)
                    removeTextChangedListener(textWatcher)
                    sharedViewModel.setItem("password", "[WD2] No Password")
                    txtPasswordValid.text = ""
                } else {
                    setBackgroundResource(R.drawable.public_border_box)
                    addTextChangedListener(textWatcher)
                    sharedViewModel.setItem("password", "")
                }
            }
        }

        btnNext.setOnClickListener {
            sharedViewModel.modifyViewPager2()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}