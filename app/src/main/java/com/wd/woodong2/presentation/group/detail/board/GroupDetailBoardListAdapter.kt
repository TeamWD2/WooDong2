package com.wd.woodong2.presentation.group.detail.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardItemBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import java.text.SimpleDateFormat
import java.util.Date

class GroupDetailBoardListAdapter(
    private val onClickBoardItem: (GroupItem.Board) -> Unit,
) :
    ListAdapter<GroupItem.Board, GroupDetailBoardListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<GroupItem.Board>() {
            override fun areItemsTheSame(
                oldItem: GroupItem.Board,
                newItem: GroupItem.Board
            ): Boolean =
                oldItem.boardId == newItem.boardId

            override fun areContentsTheSame(
                oldItem: GroupItem.Board,
                newItem: GroupItem.Board
            ): Boolean =
                oldItem == newItem
        }
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroupDetailBoardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClickBoardItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: GroupDetailBoardItemBinding,
        private val onClickBoardItem: (GroupItem.Board) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(board: GroupItem.Board) = with(binding) {
            imgProfile.load(board.profile) {
                error(R.drawable.group_ic_no_profile)
            }
            txtName.text = board.name
            txtDate.text =
                SimpleDateFormat("yyyy년 MM월 dd일").format(Date(board.timestamp))
            txtDescription.text = board.content
            imgPhoto.load(board.images?.firstOrNull())
            cardViewPhoto.isVisible = board.images.isNullOrEmpty().not()
            itemView.setOnClickListener {
                onClickBoardItem(board)
            }
        }
    }
}