package com.wd.woodong2.presentation.group.detail.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailMemberItemBinding
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailMemberListAdapter :
    ListAdapter<GroupItem.Member, GroupDetailMemberListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<GroupItem.Member>() {
            override fun areItemsTheSame(
                oldItem: GroupItem.Member,
                newItem: GroupItem.Member
            ): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: GroupItem.Member,
                newItem: GroupItem.Member
            ): Boolean =
                oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroupDetailMemberItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: GroupDetailMemberItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: GroupItem.Member) = with(binding) {
            imgProfile.load(member.profile) {
                error(R.drawable.public_default_wd2_ivory)
            }
            txtName.text = member.name
            txtLocation.text = findUserLocation(member.location)
            txtComment.text = member.comment
        }

        private fun findUserLocation(userLocation: String?): String {
            val parts = userLocation?.split(" ")
            parts?.let {
                for(part in it) {
                    if(part.endsWith("동")) {
                        return part
                    }
                }
            }
            return ""
        }
    }
}