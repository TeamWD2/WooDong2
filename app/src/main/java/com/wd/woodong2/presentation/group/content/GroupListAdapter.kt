package com.wd.woodong2.presentation.group.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupListItemBinding

class GroupListAdapter(
    val itemClickListener: (GroupItem.GroupMain) -> Unit
) : ListAdapter<GroupItem.GroupMain, GroupListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupItem.GroupMain>() {
        override fun areItemsTheSame(
            oldItem: GroupItem.GroupMain,
            newItem: GroupItem.GroupMain
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: GroupItem.GroupMain,
            newItem: GroupItem.GroupMain
        ): Boolean =
            oldItem == newItem
    }
) {
    inner class ViewHolder(private val binding: GroupListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupItem.GroupMain) = with(binding) {
            imgGroupProfile.load(item.mainImage) {
                error(R.drawable.group_ic_no_profile)
            }
            txtName.text = item.groupName
            txtIntroduce.text = item.introduce
            txtGroupTag.text = item.groupTag
            txtAgeLimit.text = item.ageLimit
            txtMemberLimit.text = item.memberLimit

            root.setOnClickListener {
                itemClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroupListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}