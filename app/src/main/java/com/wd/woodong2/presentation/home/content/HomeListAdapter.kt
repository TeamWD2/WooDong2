package com.wd.woodong2.presentation.home.content



import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.databinding.HomeListItemBinding

class HomeListAdapter(
    context: Context,
    private val onClickItem: (HomeItem) -> Unit
) : ListAdapter<HomeItem, HomeListAdapter.ViewHolder> (
    object : DiffUtil.ItemCallback<HomeItem>(){
        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
        }
    }
){
    class ViewHolder(
        private val binding: HomeListItemBinding,
        private val onClickItem: (HomeItem) -> Unit
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(item: HomeItem) = with(binding){
            homeListItemBtnTag.text = item.tag
            homeListItemThumbnail.load(item.thumbnail)
            homeListItemThumbnailCount.text = item.thumbnailCount.toString()
            homeListItemTvTitle.text = item.title
            homeListItemTvDescription.text = item.description
            homeListItemTvLocation.text = item.firstLocation
            homeListItemTvTimeStamp.text = item.timeStamp.toString()
            homeListItemTvViews.text = item.view
            homeListItemTvThumbCount.text = item.thumbCount.toString()
            homeListItemTvChatCount.text = item.chatCount.toString()

            homeListItem.setOnClickListener{
                onClickItem(
                    item
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false),
            onClickItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}