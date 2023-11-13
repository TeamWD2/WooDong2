package com.wd.woodong2.presentation.group.detail.home

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
import com.wd.woodong2.databinding.GroupDetailHomeAlbumItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeBoardItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeIntroduceItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeMemberItemBinding
import com.wd.woodong2.databinding.GroupDetailHomeUnknownItemBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import java.text.SimpleDateFormat
import java.util.Date

class GroupDetailHomeListAdapter(
    private val onClickBoardItem: (String, GroupItem.Board) -> Unit,
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
                    onClickBoardItem,
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
                val memberComments = listOf(txtMember1Comment, txtMember2Comment)
                item.memberList?.let { member ->
                    for (i in member.indices) {
                        if (i < memberLayouts.size) {
                            memberLayouts[i].visibility = View.VISIBLE
                            memberProfiles[i].load(member[i].profile) {
                                error(R.drawable.public_default_wd2_ivory)
                            }
                            memberNames[i].text = member[i].name
                            memberLocations[i].text = member[i].location?.split(" ")?.last()
                            memberComments[i].text = member[i].comment
                        }
                    }
                    viewLine.isVisible = member.size > 1
                    btnMore.isVisible = member.size > 2
                }
                btnMore.setOnClickListener {
                    onClickMoreBtn(R.string.group_detail_tab_member_title)
                }
            }
        }
    }

    class HomeBoardViewHolder(
        private val binding: GroupDetailHomeBoardItemBinding,
        private val onClickBoardItem: (String, GroupItem.Board) -> Unit,
        private val onClickMoreBtn: (Int) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupItem) = with(binding) {
            if (item is GroupItem.GroupBoard) {
                txtBoardTitle.text = item.title
                txtBoardCount.text = item.boardList?.size?.toString() ?: "0"
                btnMore.isVisible = item.boardList.isNullOrEmpty().not()
                val boardLayouts = listOf(constraintBoard1, constraintBoard2)
                val boardProfiles = listOf(imgBoard1Profile, imgBoard2Profile)
                val boardNames = listOf(txtBoard1Name, txtBoard2Name)
                val boardDates = listOf(txtBoard1Date, txtBoard2Date)
                val boardDescriptions = listOf(txtBoard1Description, txtBoard2Description)
                val cardViewPhoto = listOf(cardViewBoard1Photo, cardViewBoard2Photo)
                val boardPhotos = listOf(imgBoard1Photo, imgBoard2Photo)
                item.boardList?.let { board ->
                    for (i in board.indices) {
                        if (i < boardLayouts.size) {
                            boardLayouts[i].visibility = View.VISIBLE
                            boardProfiles[i].load(board[i].profile) {
                                error(R.drawable.public_default_wd2_ivory)
                            }
                            boardNames[i].text = board[i].name
                            boardDates[i].text =
                                SimpleDateFormat("yyyy년 MM월 dd일").format(Date(board[i].timestamp))
                            boardDescriptions[i].text = board[i].content
                            boardPhotos[i].load(board[i].images?.firstOrNull()) {
                                error(R.drawable.public_default_wd2_ivory)
                            }
                            cardViewPhoto[i].isVisible = board[i].images.isNullOrEmpty().not()
                            boardLayouts[i].setOnClickListener {
                                item.id?.let { id -> onClickBoardItem(id, board[i]) }
                            }
                        }
                    }
                    viewLine.isVisible = board.size > 1
                    btnMore.isVisible = board.size > 2
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
                txtAlbumCount.text = item.images?.size?.toString() ?: "0"
                btnMore.isVisible = item.images.isNullOrEmpty().not()
                if(item.images.isNullOrEmpty()) {
                    cardViewPhoto1.visibility = View.GONE
                    cardViewPhoto2.visibility = View.GONE
                    cardViewPhoto3.visibility = View.GONE
                }
                val albumCardView = listOf(cardViewPhoto1, cardViewPhoto2, cardViewPhoto3)
                val albumPhotos = listOf(imgPhoto1, imgPhoto2, imgPhoto3)
                item.images?.let { image ->
                    for (i in image.indices) {
                        if (i < albumPhotos.size) {
                            albumCardView[i].visibility = View.VISIBLE
                            albumPhotos[i].load(image[i]) {
                                error(R.drawable.public_default_wd2_ivory)
                            }
                        }
                    }
                    btnMore.isVisible = image.size > 3
                }
                btnMore.setOnClickListener {
                    onClickMoreBtn(R.string.group_detail_tab_album_title)
                }
            }
        }
    }

    class UnknownViewHolder(
        private val binding: GroupDetailHomeUnknownItemBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupItem) {
            Unit
        }
    }
}