package com.wd.woodong2.presentation.mypage.content.group


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.wd.woodong2.presentation.group.content.GroupItem
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupListItemBinding

class MyPageGroupListAdapter(
    private val onClickItem: (Int, GroupItem.GroupMain) -> Unit,
) : ListAdapter<GroupItem.GroupMain, MyPageGroupListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupItem.GroupMain>() {
        override fun areItemsTheSame(
            oldItem: GroupItem.GroupMain,
            newItem: GroupItem.GroupMain,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GroupItem.GroupMain,
            newItem: GroupItem.GroupMain,
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    class ViewHolder(
        private val binding: GroupListItemBinding,
//        private val onClickItem: (Int, GroupItem) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        // 아이템 기본 설정
        // 모든 아이템을 갖고 오는게 아니라 가입한 그룹의 데이터만 갖고오기 설정
        // 사용자 아이디로 판단하기
        fun bind(item: GroupItem.GroupMain) = with(binding) {
            imgGroupProfile.load(item.mainImage) {
                error(R.drawable.group_ic_no_image)
            }
            txtName.text = item.groupName
            txtGroupTag.text = item.groupTag
            txtAgeLimit.text = item.ageLimit
            txtMemberLimit.text = "${item.memberCount} / ${item.memberLimit.toString()}"

            root.setOnClickListener {
//                onClickItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroupListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
//            onClickItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}