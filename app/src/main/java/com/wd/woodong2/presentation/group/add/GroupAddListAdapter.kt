package com.wd.woodong2.presentation.group.add

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
import com.wd.woodong2.databinding.GroupAddTitleItemBinding
import com.wd.woodong2.databinding.GroupAddUnknownItemBinding

class GroupAddListAdapter : ListAdapter<GroupAddItem, GroupAddListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupAddItem>() {
        override fun areItemsTheSame(oldItem: GroupAddItem, newItem: GroupAddItem): Boolean {
            //수정
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GroupAddItem, newItem: GroupAddItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    enum class GroupAddItemViewType {
        TITLE,
        DESCRIPTION,
        CHIP_GROUP,
        EDITTEXT,
        IMAGE,
        DIVIDER
    }

    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun bind(item: GroupAddItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupAddItem.Title -> GroupAddItemViewType.TITLE.ordinal
        is GroupAddItem.Description -> GroupAddItemViewType.DESCRIPTION.ordinal
        is GroupAddItem.ChipGroup -> GroupAddItemViewType.CHIP_GROUP.ordinal
        is GroupAddItem.EditText -> GroupAddItemViewType.EDITTEXT.ordinal
        is GroupAddItem.Image -> GroupAddItemViewType.IMAGE.ordinal
        is GroupAddItem.Divider -> GroupAddItemViewType.DIVIDER.ordinal
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
                )
            )

            GroupAddItemViewType.EDITTEXT.ordinal -> EditTextViewHolder(
                GroupAddEditTextItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            GroupAddItemViewType.IMAGE.ordinal -> ImageViewHolder(
                GroupAddImageItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
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
        override fun bind(item: GroupAddItem) = with(binding) {
            if (item is GroupAddItem.Title) {
                txtTitle.text = item.title
            }
        }
    }

    class DescriptionViewHolder(private val binding: GroupAddDescriptionItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddItem) = with(binding) {
            if (item is GroupAddItem.Description) {
                txtDescription.text = item.description
            }
        }
    }

    class ChipGroupViewHolder(private val binding: GroupAddChipGroupItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddItem) = with(binding) {
            if (item is GroupAddItem.ChipGroup) {
                item.chipButtons?.forEach { chipButton ->
                    val chip = Chip(chipGroup.context)
                    chip.text = chipButton
                    chip.isCheckable = true
                    chipGroup.addView(chip)
                }
            }
        }
    }

    class EditTextViewHolder(private val binding: GroupAddEditTextItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddItem) = with(binding) {
            if (item is GroupAddItem.EditText) {
                edtText.setText(item.editText)
            }
        }
    }

    class ImageViewHolder(private val binding: GroupAddImageItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddItem) = with(binding) {
            if (item is GroupAddItem.Image) {
                imgImage.load(item.image) {
                    error(R.drawable.group_ic_no_image)
                }
            }
        }
    }

    class DividerViewHolder(private val binding: GroupAddDividerItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddItem) = with(binding) {
            if (item is GroupAddItem.Divider) {
            }
        }
    }

    class UnknownViewHolder(private val binding: GroupAddUnknownItemBinding) :
        ViewHolder(binding.root) {
        override fun bind(item: GroupAddItem) = Unit
    }
}