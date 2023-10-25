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

class MyPageGroupListAdapter (
    private val onClickItem: (Int, GroupItem) -> Unit
): ListAdapter<GroupItem, MyPageGroupListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupItem>() {
        override fun areItemsTheSame(
            oldItem: GroupItem,
            newItem: GroupItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GroupItem,
            newItem: GroupItem
        ): Boolean {
            return oldItem == newItem
        }
    }
){
    class ViewHolder(
        private val binding: GroupListItemBinding,
        private val onClickItem: (Int, GroupItem) -> Unit
    ):RecyclerView.ViewHolder(binding.root){

        // 아이템 기본 설정
        // 모든 아이템을 갖고 오는게 아니라 가입한 그룹의 데이터만 갖고오기 설정
        // 사용자 아이디로 판단하기
        fun bind(item: GroupItem) = with(binding){
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
                onClickItem(adapterPosition,item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroupListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false),
            onClickItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}