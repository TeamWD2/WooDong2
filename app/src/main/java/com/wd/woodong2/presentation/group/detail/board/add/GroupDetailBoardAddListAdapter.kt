package com.wd.woodong2.presentation.group.detail.board.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.databinding.GroupDetailBoardAddItemBinding

class GroupDetailBoardAddListAdapter(
    private val onClickPlusImage: (GroupDetailBoardAddImageItem) -> Unit,
    private val onClickRemoveImage: (Int) -> Unit
) : ListAdapter<GroupDetailBoardAddImageItem, GroupDetailBoardAddListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GroupDetailBoardAddImageItem>() {
        override fun areItemsTheSame(
            oldItem: GroupDetailBoardAddImageItem,
            newItem: GroupDetailBoardAddImageItem
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: GroupDetailBoardAddImageItem,
            newItem: GroupDetailBoardAddImageItem
        ): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GroupDetailBoardAddItemBinding.inflate(
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
        private val binding: GroupDetailBoardAddItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupDetailBoardAddImageItem) = with(binding) {
            imgPhoto.load(item.uri)
            imgCancelPhoto.isVisible = item.isCancelBtn
            imgPlusPhoto.isVisible = item.isPlusBtn

            imgPhoto.setOnClickListener {
                onClickPlusImage(item)
            }

            imgCancelPhoto.setOnClickListener {
                onClickRemoveImage(bindingAdapterPosition)
            }
        }
    }
}