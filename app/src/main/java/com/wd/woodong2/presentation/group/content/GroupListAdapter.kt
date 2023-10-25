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
    val itemClickListener: (GroupItem) -> Unit
): ListAdapter<GroupItem, GroupListAdapter.ViewHolder>(
    object: DiffUtil.ItemCallback<GroupItem>() {
        override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class ViewHolder(private val binding: GroupListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupItem) = with(binding) {
            if(item is GroupItem.GroupMain) {
                imgGroupProfile.load(item.mainImage) {
                    error(R.drawable.group_ic_no_image)
                }
                txtName.text = item.groupName
                txtTagCategory.text = item.groupTag
                txtTagAge.text = item.ageLimit
                txtTagMemberLimit.text = "${item.memberCount} / ${item.memberLimit.toString()}"

                root.setOnClickListener {
                    itemClickListener(item)
                }
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