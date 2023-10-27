package com.wd.woodong2.presentation.home.detail

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.databinding.HomeDetailListItemBinding
import com.wd.woodong2.presentation.home.content.HomeItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CommentListAdapter(
    private val homeItem: HomeItem,
    private val comments: MutableList<CommentItem>,
    private val viewModel: HomeDetailViewModel
) :
    RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    class CommentViewHolder(val binding: HomeDetailListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            HomeDetailListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.binding.txtCommentName.text = comment.username
        holder.binding.txtCommentDescription.text = comment.content
        holder.binding.txtCommentLocation.text = comment.location

        holder.binding.txtCommentTimestamp.text = formatTimestamp(comment.timestamp)

        holder.binding.txtCommnetDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("댓글 삭제")
                .setMessage("댓글을 삭제 하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    viewModel.deleteComment(homeItem, comment)
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    fun updateComments(newComments: List<CommentItem>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
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