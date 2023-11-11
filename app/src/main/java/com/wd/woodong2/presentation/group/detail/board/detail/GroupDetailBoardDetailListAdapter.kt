package com.wd.woodong2.presentation.group.detail.board.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.databinding.GroupDetailBoardDetailCommentItemBinding
import com.wd.woodong2.databinding.GroupDetailBoardDetailContentItemBinding
import com.wd.woodong2.databinding.GroupDetailBoardDetailDividerItemBinding
import com.wd.woodong2.databinding.GroupDetailBoardDetailImageItemBinding
import com.wd.woodong2.databinding.GroupDetailBoardDetailTitleItemBinding
import com.wd.woodong2.databinding.GroupDetailBoardDetailUnknownItemBinding
import java.text.SimpleDateFormat
import java.util.Date

class GroupDetailBoardDetailListAdapter(
    private val onClickDeleteComment: (Int) -> Unit
) : ListAdapter<GroupDetailBoardDetailItem, GroupDetailBoardDetailListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<GroupDetailBoardDetailItem>() {
            override fun areItemsTheSame(
                oldItem: GroupDetailBoardDetailItem,
                newItem: GroupDetailBoardDetailItem
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: GroupDetailBoardDetailItem,
                newItem: GroupDetailBoardDetailItem
            ): Boolean =
                oldItem == newItem
        }
    ) {
    enum class BoardDetailItemViewType {
        CONTENT,
        TITLE,
        COMMENT,
        DIVIDER
    }

    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun bind(item: GroupDetailBoardDetailItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is GroupDetailBoardDetailItem.BoardContent -> BoardDetailItemViewType.CONTENT.ordinal
        is GroupDetailBoardDetailItem.BoardTitle -> BoardDetailItemViewType.TITLE.ordinal
        is GroupDetailBoardDetailItem.BoardComment -> BoardDetailItemViewType.COMMENT.ordinal
        is GroupDetailBoardDetailItem.BoardDivider -> BoardDetailItemViewType.DIVIDER.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            BoardDetailItemViewType.CONTENT.ordinal ->
                BoardContentViewHolder(
                    GroupDetailBoardDetailContentItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            BoardDetailItemViewType.TITLE.ordinal ->
                BoardTitleViewHolder(
                    GroupDetailBoardDetailTitleItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            BoardDetailItemViewType.COMMENT.ordinal ->
                BoardCommentViewHolder(
                    GroupDetailBoardDetailCommentItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClickDeleteComment
                )

            BoardDetailItemViewType.DIVIDER.ordinal ->
                BoardDividerViewHolder(
                    GroupDetailBoardDetailDividerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )

            else ->
                UnknownViewHolder(
                    GroupDetailBoardDetailUnknownItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BoardContentViewHolder(
        private val binding: GroupDetailBoardDetailContentItemBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupDetailBoardDetailItem) = with(binding) {
            if (item is GroupDetailBoardDetailItem.BoardContent) {
                txtBoardContent.text = item.content
                linearLayoutImage.removeAllViews()
                item.images?.forEach { image ->
                    val imageBinding = GroupDetailBoardDetailImageItemBinding.inflate(
                        LayoutInflater.from(linearLayoutImage.context),
                        linearLayoutImage,
                        false
                    )
                    imageBinding.imgPhoto.load(image)
                    linearLayoutImage.addView(imageBinding.root)
                }
            }
        }
    }

    class BoardTitleViewHolder(
        private val binding: GroupDetailBoardDetailTitleItemBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupDetailBoardDetailItem) = with(binding) {
            if (item is GroupDetailBoardDetailItem.BoardTitle) {
                txtTitle.text = item.title
                txtCount.text = item.boardCount
            }
        }
    }

    class BoardCommentViewHolder(
        private val binding: GroupDetailBoardDetailCommentItemBinding,
        private val onClickDeleteComment: (Int) -> Unit
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupDetailBoardDetailItem) = with(binding) {
            if (item is GroupDetailBoardDetailItem.BoardComment) {
                imgProfile.load(item.userProfile)
                txtName.text = item.userName
                txtLocation.text = item.userLocation?.split(" ")?.last()
                txtDate.text = item.timestamp?.let { Date(it) }
                    ?.let { SimpleDateFormat("yyyy년 MM월 dd일").format(it) }
                txtComment.text = item.comment
                txtDelete.isVisible = item.isWriteOwner ?: false
                txtDelete.setOnClickListener {
                    onClickDeleteComment(bindingAdapterPosition)
                }
            }
        }
    }

    class BoardDividerViewHolder(
        private val binding: GroupDetailBoardDetailDividerItemBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupDetailBoardDetailItem) = with(binding) {

        }
    }

    class UnknownViewHolder(
        private val binding: GroupDetailBoardDetailUnknownItemBinding
    ) : ViewHolder(binding.root) {
        override fun bind(item: GroupDetailBoardDetailItem) {

        }
    }
}