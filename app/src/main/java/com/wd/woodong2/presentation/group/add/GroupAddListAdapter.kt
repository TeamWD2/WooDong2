package com.wd.woodong2.presentation.group.add

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.chip.Chip
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddChipGroupItemBinding
import com.wd.woodong2.databinding.GroupAddDescriptionItemBinding
import com.wd.woodong2.databinding.GroupAddDividerItemBinding
import com.wd.woodong2.databinding.GroupAddEditTextItemBinding
import com.wd.woodong2.databinding.GroupAddImageItemBinding
import com.wd.woodong2.databinding.GroupAddPasswordItemBinding
import com.wd.woodong2.databinding.GroupAddTitleItemBinding
import com.wd.woodong2.databinding.GroupAddUnknownItemBinding
import java.util.regex.Pattern

class GroupAddListAdapter(
    private val onCreateGroupAdd: (Int, String) -> Unit,
    private val onCheckBoxChecked: (Int, GroupAddGetItem.Password) -> Unit
) : ListAdapter<GroupAddGetItem, GroupAddListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupAddGetItem>() {
        override fun areItemsTheSame(
            oldItem: GroupAddGetItem,
            newItem: GroupAddGetItem
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: GroupAddGetItem,
            newItem: GroupAddGetItem
        ): Boolean =
            oldItem == newItem
    }
) {
    enum class GroupAddItemViewType {
        TITLE,
        DESCRIPTION,
        CHIP_GROUP,
        EDITTEXT,
        PASSWORD,
        IMAGE,
        DIVIDER
    }

    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun bind(item: GroupAddGetItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupAddGetItem.Title -> GroupAddItemViewType.TITLE.ordinal
        is GroupAddGetItem.Description -> GroupAddItemViewType.DESCRIPTION.ordinal
        is GroupAddGetItem.ChipGroup -> GroupAddItemViewType.CHIP_GROUP.ordinal
        is GroupAddGetItem.EditText -> GroupAddItemViewType.EDITTEXT.ordinal
        is GroupAddGetItem.Password -> GroupAddItemViewType.PASSWORD.ordinal
        is GroupAddGetItem.Image -> GroupAddItemViewType.IMAGE.ordinal
        is GroupAddGetItem.Divider -> GroupAddItemViewType.DIVIDER.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            GroupAddItemViewType.TITLE.ordinal -> TitleViewHolder(
                GroupAddTitleItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            GroupAddItemViewType.DESCRIPTION.ordinal -> DescriptionViewHolder(
                GroupAddDescriptionItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            GroupAddItemViewType.CHIP_GROUP.ordinal -> ChipGroupViewHolder(
                GroupAddChipGroupItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onCreateGroupAdd
            )

            GroupAddItemViewType.EDITTEXT.ordinal -> EditTextViewHolder(
                GroupAddEditTextItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onCreateGroupAdd
            )

            GroupAddItemViewType.PASSWORD.ordinal -> PasswordViewHolder(
                GroupAddPasswordItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onCreateGroupAdd,
                onCheckBoxChecked
            )

            GroupAddItemViewType.IMAGE.ordinal -> ImageViewHolder(
                GroupAddImageItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onCreateGroupAdd
            )

            GroupAddItemViewType.DIVIDER.ordinal -> DividerViewHolder(
                GroupAddDividerItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> UnknownViewHolder(
                GroupAddUnknownItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TitleViewHolder(private val binding: GroupAddTitleItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = with(binding) {
            if (item is GroupAddGetItem.Title) {
                txtTitle.text = item.title
            }
        }
    }

    class DescriptionViewHolder(private val binding: GroupAddDescriptionItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = with(binding) {
            if (item is GroupAddGetItem.Description) {
                txtDescription.text = item.description
            }
        }
    }

    class ChipGroupViewHolder(
        private val binding: GroupAddChipGroupItemBinding,
        private val onCreateGroupAdd: (Int, String) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = with(binding) {
            if (item is GroupAddGetItem.ChipGroup) {
                chipGroup.removeAllViews()
                item.chipButtons.forEach { chipButton ->
                    val chip = Chip(chipGroup.context).apply {
                        text = chipButton
                        isCheckable = true
                        setOnClickListener {
                            onCreateGroupAdd(
                                adapterPosition,
                                text.toString()
                            )
                        }
                    }
                    chipGroup.addView(chip)
                }
            }
        }
    }

    class EditTextViewHolder(
        private val binding: GroupAddEditTextItemBinding,
        private val onCreateGroupAdd: (Int, String) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = with(binding) {
            if (item is GroupAddGetItem.EditText) {
                edtText.apply {
                    maxLines = item.maxLines ?: Int.MAX_VALUE
                    minLines = item.minLines ?: 1
                    hint = item.hint
                    setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) { //focus가 다른 뷰로 이동될 때
                            onCreateGroupAdd(
                                adapterPosition,
                                edtText.text.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    class PasswordViewHolder(
        private val binding: GroupAddPasswordItemBinding,
        private val onCreateGroupAdd: (Int, String) -> Unit,
        private val onCheckBoxChecked: (Int, GroupAddGetItem.Password) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = with(binding) {
            if (item is GroupAddGetItem.Password) {
                if (!item.isChecked) {
                    edtPassword.apply {
                        hint = item.passwordHint
                        isEnabled = true
                        setBackgroundResource(R.drawable.group_border_box)
                        addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            }

                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            }

                            override fun afterTextChanged(p0: Editable?) {
                                Log.d("sinw", "afterTextChanged")
                                val pwdPattern = Pattern.matches(
                                    "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$",
                                    text.toString().trim()
                                )
                                if (pwdPattern) {
                                    txtPwValid.apply {
                                        setText(R.string.group_add_password_valid)
                                        setTextColor(Color.GREEN)
                                        onCreateGroupAdd(
                                            adapterPosition,
                                            edtPassword.text.toString()
                                        )
                                    }
                                } else {
                                    txtPwValid.apply {
                                        setText(R.string.group_add_password_invalid)
                                        setTextColor(Color.RED)
                                    }
                                }
                            }
                        })
                    }
                } else {
                    edtPassword.apply {
                        hint = item.passwordHint
                        isEnabled = false
                        setBackgroundResource(R.drawable.group_border_box_disabled)
                        onCreateGroupAdd(
                            adapterPosition,
                            "[WD2] No Password"
                        )
                    }
                    txtPwValid.text = ""
                }
                checkBox.apply {
                    text = item.chkBoxText
                    isChecked = item.isChecked
                    setOnCheckedChangeListener { _, isChkBox ->
                        if (item.isChecked != isChkBox) {
                            onCheckBoxChecked(
                                adapterPosition,
                                item.copy(isChecked = isChkBox)
                            )
                        }
                    }
                }
            }
        }
    }

    class ImageViewHolder(
        private val binding: GroupAddImageItemBinding,
        private val onCreateGroupAdd: (Int, String) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = with(binding) {
            if (item is GroupAddGetItem.Image) {
                imgImage.load(item.image) {
                    error(R.drawable.group_ic_no_image)
                }
                imgImage.setOnClickListener {
                    onCreateGroupAdd(
                        adapterPosition,
                        "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg" //임시데이터
                    )
                }
            }
        }
    }

    class DividerViewHolder(private val binding: GroupAddDividerItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = Unit
    }

    class UnknownViewHolder(private val binding: GroupAddUnknownItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddGetItem) = Unit
    }
}