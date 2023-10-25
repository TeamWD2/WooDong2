package com.wd.woodong2.presentation.mypage.content.thumb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.databinding.HomeListItemBinding
import com.wd.woodong2.presentation.home.content.HomeItem


class MyPageThumbListAdapter (
    private val onClickItem: (Int, HomeItem) -> Unit,
    //private val chatIds : String?     -> map 형식
): ListAdapter<HomeItem, MyPageThumbListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<HomeItem>() {
        override fun areItemsTheSame(
            oldItem: HomeItem,
            newItem: HomeItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: HomeItem,
            newItem: HomeItem
        ): Boolean {
            return oldItem == newItem
        }
    }
){
    class ViewHolder(
        private val binding: HomeListItemBinding,
        private val onClickItem: (Int, HomeItem) -> Unit,
        //private val chatId : String?      ->map 형식
    ): RecyclerView.ViewHolder(binding.root){

        //아이템 기본 설정
        // 모든 아이템을 갖고 오는게 아니라 가입한 그룹의 데이터만 갖고오기 설정

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

            root.setOnClickListener {
                onClickItem(adapterPosition,item)
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