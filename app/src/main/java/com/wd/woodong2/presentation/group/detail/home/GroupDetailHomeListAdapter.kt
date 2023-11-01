package com.wd.woodong2.presentation.group.detail.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailHomeAlbumItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeBoardItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeIntroduceItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeMemberItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeUnknownItemBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import java.text.SimpleDateFormat
import java.util.Date

class GroupDetailHomeListAdapter(
    private val onClickMoreBtn: (Int) -> Unit
) : ListAdapter<GroupItem, GroupDetailHomeListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupItem>() {
        override fun areItemsTheSame(
            oldItem: GroupItem,
            newItem: GroupItem
        ): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: GroupItem,
            newItem: GroupItem
        ): Boolean =
            oldItem == newItem
    }
) {
    enum class GroupItemViewType {
        INTRODUCE,
        MEMBER,
        BOARD,
        ALBUM
    }

    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun bind(item: GroupItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupItem.GroupIntroduce -> GroupItemViewType.INTRODUCE.ordinal
        is GroupItem.GroupMember -> GroupItemViewType.MEMBER.ordinal
        is GroupItem.GroupBoard -> GroupItemViewType.BOARD.ordinal
        is GroupItem.GroupAlbum -> GroupItemViewType.ALBUM.ordinal
        else -> -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            GroupItemViewType.INTRODUCE.ordinal ->
                HomeIntroduceViewHolder(
                    GroupDetailHomeIntroduceItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            GroupItemViewType.MEMBER.ordinal ->
                HomeMemberViewHolder(
                    GroupDetailHomeMemberItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClickMoreBtn
                )

            GroupItemViewType.BOARD.ordinal ->
                HomeBoardViewHolder(
                    GroupDetailHomeBoardItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClickMoreBtn
                )

            GroupItemViewType.ALBUM.ordinal ->
                HomeAlbumViewHolder(
                    GroupDetailHomeAlbumItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClickMoreBtn
                )

            else ->
                UnknownViewHolder(
                    GroupDetailHomeUnknownItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HomeIntroduceViewHolder(
        private val binding: GroupDetailHomeIntroduceItemBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupItem) = with(binding) {
            if (item is GroupItem.GroupIntroduce) {
                txtIntroduceTitle.text = item.title
                txtIntroduceDes.text = item.introduce
                txtGroupTag.text = item.groupTag
                txtAgeLimit.text = item.ageLimit
                txtMemberLimit.text = item.memberLimit
            }
        }
    }

    class HomeMemberViewHolder(
        private val binding: GroupDetailHomeMemberItemBinding,
        private val onClickMoreBtn: (Int) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupItem) = with(binding) {
            if (item is GroupItem.GroupMember) {
                txtMemberTitle.text = item.title
                txtMemberCount.text = item.memberList?.size.toString()
                val memberLayouts = listOf(constraintMember1, constraintMember2)
                val memberProfiles = listOf(imgMember1Profile, imgMember2Profile)
                val memberNames = listOf(txtMember1Name, txtMember2Name)
                val memberLocations = listOf(txtMember1Location, txtMember2Location)
                item.memberList?.let { member ->
                    for (i in member.indices) {
                        if (i < memberLayouts.size) {
                            memberLayouts[i].visibility = View.VISIBLE
                            memberProfiles[i].load(member[i].profile) {
                                error(R.drawable.group_ic_no_profile)
                            }
                            memberNames[i].text = member[i].name
                            memberLocations[i].text = member[i].location
                        }
                    }
                }
                btnMore.setOnClickListener {
                    onClickMoreBtn(R.string.group_detail_tab_member_title)
                }
            }
        }
    }

    class HomeBoardViewHolder(
        private val binding: GroupDetailHomeBoardItemBinding,
        private val onClickMoreBtn: (Int) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupItem) = with(binding) {
            if (item is GroupItem.GroupBoard) {
                txtBoardTitle.text = item.title
                txtBoardCount.text = item.boardList?.size.toString()
                val boardLayouts = listOf(constraintBoard1, constraintBoard2)
                val boardProfiles = listOf(imgBoard1Profile, imgBoard2Profile)
                val boardNames = listOf(txtBoard1Name, txtBoard2Name)
                val boardLocations = listOf(txtBoard1Location, txtBoard2Location)
                val boardDates = listOf(txtBoard1Date, txtBoard2Date)
                val boardDescriptions = listOf(txtBoard1Description, txtBoard2Description)
                val boardPhotos = listOf(imgBoard1Photo, imgBoard2Photo)
                item.boardList?.let { board ->
                    for (i in board.indices) {
                        if (i < boardLayouts.size) {
                            boardLayouts[i].visibility = View.VISIBLE
                            boardProfiles[i].load(board[i].profile) {
                                error(R.drawable.group_ic_no_profile)
                            }
                            boardNames[i].text = board[i].name
                            boardLocations[i].text = board[i].location
                            boardDates[i].text = SimpleDateFormat("yyyy년 MM월 dd일").format(Date(board[i].timestamp))
                            boardDescriptions[i].text = board[i].content
                            boardPhotos[i].load(board[i].images?.get(0)) {
                                error(R.drawable.group_ic_no_image)
                            }
                        }
                    }
                }
                btnMore.setOnClickListener {
                    onClickMoreBtn(R.string.group_detail_tab_board_title)
                }
            }
        }
    }

    class HomeAlbumViewHolder(
        private val binding: GroupDetailHomeAlbumItemBinding,
        private val onClickMoreBtn: (Int) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupItem) = with(binding) {
            if (item is GroupItem.GroupAlbum) {
                txtAlbumTitle.text = item.title
                txtAlbumCount.text = item.images?.size.toString()
                val albumPhotos = listOf(imgPhoto1, imgPhoto2, imgPhoto3)
                item.images?.let { image ->
                    for (i in image.indices) {
                        if (i < albumPhotos.size) {
                            albumPhotos[i].visibility = View.VISIBLE
                            albumPhotos[i].load(image[i]) {
                                error(R.drawable.group_ic_no_profile)
                            }
                        }
                    }
                }
                btnMore.setOnClickListener {
                    onClickMoreBtn(R.string.group_detail_tab_album_title)
                }
            }
        }
    }

    class UnknownViewHolder(
        private val binding: GroupDetailHomeUnknownItemBinding
    ): ViewHolder(binding.root) {
        override fun bind(item: GroupItem) {
            Unit
        }
    }
}