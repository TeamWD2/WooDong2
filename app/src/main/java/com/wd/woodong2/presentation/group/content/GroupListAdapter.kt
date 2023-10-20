package com.wd.woodong2.presentation.group.content

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class ViewHolder(private val binding: GroupListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupItem) = with(binding) {
            imgGroupProfile.load(item.mainImage) {
                error(R.drawable.group_ic_no_image)
            }
            txtTitle.text = item.title
            txtMemberCount.text = "${item.memberList?.size}명"
            val memberCardViews = listOf(cardViewMember1, cardViewMember2, cardViewMember3)
            val memberProfiles = listOf(imgMemberProfile1, imgMemberProfile2, imgMemberProfile3)
            item.memberList?.let { member ->
                for(i in member.indices) {
                    if(i < memberProfiles.size) {
                        memberCardViews[i].visibility = View.VISIBLE
                        memberProfiles[i].load(member[i].userProfile) {
                            error(R.drawable.group_ic_no_profile)
                        }
                    }
                }
            }
            txtTagCategory.text = item.groupTag
            txtTagAge.text = item.ageLimit
            txtTagMemberLimit.text = item.memberLimit.toString()

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