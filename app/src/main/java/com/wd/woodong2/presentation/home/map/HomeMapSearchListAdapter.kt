package com.wd.woodong2.presentation.home.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.R

import com.wd.woodong2.databinding.HomeMapSearchListItemBinding

class HomeMapSearchListAdapter(
    private val onClickItem: (Int, HomeMapSearchItem.MapSearchItem) -> Unit
    ): ListAdapter<HomeMapSearchItem, HomeMapSearchListAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<HomeMapSearchItem>(){
            override fun areItemsTheSame(
                oldItem: HomeMapSearchItem,
                newItem: HomeMapSearchItem
            ): Boolean = if(oldItem is HomeMapSearchItem.MapSearchItem && newItem is HomeMapSearchItem.MapSearchItem) {
                oldItem.address == newItem.address
            }else{
                oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: HomeMapSearchItem,
                newItem: HomeMapSearchItem
            ): Boolean = oldItem==newItem
        }
    ) {
    enum class SearchItemViewType {
        Map
    }
    abstract class ViewHolder(
        root: View
    ) : RecyclerView.ViewHolder(root) {
        abstract fun onBind(item: HomeMapSearchItem)
    }
    override fun getItemViewType(position: Int): Int = when(getItem(position)) {
        is HomeMapSearchItem.MapSearchItem -> SearchItemViewType.Map.ordinal
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            SearchItemViewType.Map.ordinal -> MapViewHolder(
                HomeMapSearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
    class MapViewHolder(
        private val binding: HomeMapSearchListItemBinding,
        private val onClickItem: (Int, HomeMapSearchItem.MapSearchItem) -> Unit
    ) : ViewHolder(binding.root) {
        override fun onBind(item: HomeMapSearchItem) {
            if (item is HomeMapSearchItem.MapSearchItem) {
                binding.homeMapSearchAddressItem.text = item.address
                binding.container.setOnClickListener {
                    binding.container.setBackgroundResource(R.drawable.home_map_search_list_item_click_shape)
                    onClickItem(adapterPosition, item)
                }
            }
        }
    }
}