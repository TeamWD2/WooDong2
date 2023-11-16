package com.wd.woodong2.presentation.home.content



import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeListItemBinding
import com.wd.woodong2.domain.repository.UserPreferencesRepository
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeListAdapter(
    context: Context,
    private val currentUser: UserItem?,
    private val onClickItem: (HomeItem) -> Unit,
    private val onDeleteItem: (HomeItem) -> Unit
) : ListAdapter<HomeItem, HomeListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<HomeItem>() {
        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
        }
    }
) {

    class ViewHolder(
        private val binding: HomeListItemBinding,
        private val currentUser: UserItem?,
        private val onClickItem: (HomeItem) -> Unit,
        private val onDeleteItem: (HomeItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem) = with(binding) {
            homeListItemBtnTag.text = item.tag
            if (item.thumbnail.isNullOrEmpty()) { //이미지 없을때, 카드뷰 숨김처리
                cardView3.visibility = View.GONE
            } else {
                cardView3.visibility = View.VISIBLE
                homeListItemThumbnail.load(item.thumbnail){
                    error(R.drawable.public_default_wd2_ivory)
                }
            }
            homeListItemTvTitle.text = item.title
            homeListItemTvDescription.text = item.description
            homeListItemUser.text = item.name

            homeListItemTvLocation.text = HomeMapActivity.extractLocationInfo(item.location)
            homeListItemTvTimeStamp.text = formatTimestamp(item.timeStamp)

            homeListItemTvThumbCount.text = item.thumbCount.toString()
            homeListItemTvChatCount.text = item.chatCount.toString()

            homeListItem.setOnClickListener {
                onClickItem(
                    item
                )
            }

            if (currentUser?.id == item.userId) {
                homeListItemDelete.visibility = View.VISIBLE
                homeListItemDelete.setOnClickListener {
                    showDeleteConfirmationDialog(item)
                }
            } else {
                homeListItemDelete.visibility = View.GONE
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

        private fun showDeleteConfirmationDialog(item: HomeItem) {
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("삭제 확인")
            builder.setMessage("정말로 이 항목을 삭제하시겠습니까?")
            builder.setPositiveButton("예") { _, _ ->
                // 사용자가 "예"를 클릭한 경우 항목을 삭제합니다.
                onDeleteItem(item)
            }
            builder.setNegativeButton("아니오") { _, _ ->
                // 사용자가 "아니오"를 클릭한 경우 아무 작업도 수행하지 않습니다.
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            currentUser,
            onClickItem,
            onDeleteItem

        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}