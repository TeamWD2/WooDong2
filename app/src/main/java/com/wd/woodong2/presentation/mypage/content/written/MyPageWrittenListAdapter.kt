package com.wd.woodong2.presentation.mypage.content.written

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeListItemBinding
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyPageWrittenListAdapter (
    private val onClickItem: (HomeItem) -> Unit,
    //private val chatIds : String?     -> map 형식
): ListAdapter<HomeItem, MyPageWrittenListAdapter.ViewHolder>(
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
        private val onClickItem: (HomeItem) -> Unit,
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(item: HomeItem) = with(binding){
            homeListItemBtnTag.text = item.tag
            homeListItemThumbnail.load(item.thumbnail) {
                error(R.drawable.public_default_wd2_ivory)
            }
            homeListItemTvTitle.text = item.title
            homeListItemTvDescription.text = item.description

            homeListItemTvLocation.text = HomeMapActivity.extractLocationInfo(item.location)
            homeListItemTvTimeStamp.text = formatTimestamp(item.timeStamp)

            homeListItemTvThumbCount.text = item.thumbCount.toString()
            homeListItemTvChatCount.text = item.chatCount.toString()

            homeListItem.setOnClickListener{
                onClickItem(
                    item
                )
            }

        }
        private fun formatTimestamp(timestamp: Long?): String {
            if (timestamp == null) return "정보 없음"

            val currentTimeMillis = System.currentTimeMillis()

            val currentTime = Date(currentTimeMillis)
            val messageTime = Date(timestamp)

            val diff = currentTime.time - messageTime.time
            val minute = 60 * 1000
            val hour = minute * 60
            val day = hour * 24

            val calendar = Calendar.getInstance()
            calendar.time = messageTime
            val messageYear = calendar.get(Calendar.YEAR)
            calendar.time = Date(currentTimeMillis)
            val currentYear = calendar.get(Calendar.YEAR)

            return when {
                diff < minute -> "방금 전"
                diff < 2 * minute -> "1분 전"
                diff < hour -> "${diff / minute}분 전"
                diff < 2 * hour -> "1시간 전"
                diff < day -> "${diff / hour}시간 전"
                diff < 2 * day -> "어제"
                messageYear == currentYear -> SimpleDateFormat("MM월 dd일", Locale.KOREA).format(
                    messageTime
                )

                else -> SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(messageTime)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false),
            onClickItem,
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}