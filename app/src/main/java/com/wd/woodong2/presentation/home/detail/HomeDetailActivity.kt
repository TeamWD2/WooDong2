package com.wd.woodong2.presentation.home.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeDetailActivityBinding
import com.wd.woodong2.presentation.home.content.HomeItem
import java.text.SimpleDateFormat
import java.util.*

class HomeDetailActivity : AppCompatActivity() {
    private lateinit var binding: HomeDetailActivityBinding
    private lateinit var viewModel: HomeDetailViewModel
    private lateinit var homeItem: HomeItem
    private lateinit var commentsAdapter: CommentListAdapter

    companion object {
        private const val EXTRA_HOME_ITEM = "extra_home_item"
        fun homeDetailActivityNewIntent(context: Context, homeItem: HomeItem): Intent =
            Intent(context, HomeDetailActivity::class.java).apply {
                putExtra(EXTRA_HOME_ITEM, homeItem)
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initView()
    }


    private fun initView() {
        homeItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_HOME_ITEM, HomeItem::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_HOME_ITEM)
        }
            ?: throw IllegalArgumentException("데이터를 가져오지 못했습니다")
        displayData(homeItem)

        initLikeButton(homeItem)
        initCommentButton(homeItem)
        setupCommentsRecyclerView()

        viewModel.fetchComments(homeItem) { updatedComments ->
            commentsAdapter.updateComments(updatedComments)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[HomeDetailViewModel::class.java]
    }

    private fun setupCommentsRecyclerView() {
        commentsAdapter = CommentListAdapter(homeItem, viewModel)
        binding.recyclerviewComment.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewComment.adapter = commentsAdapter
    }


    private fun initCommentButton(homeItem: HomeItem) {
        binding.btnSave.setOnClickListener {
            val commentContent = binding.editComment.text.toString()
            if (commentContent.isNotBlank()) {
                viewModel.postComment(homeItem, commentContent)
                binding.editComment.text.clear()
                viewModel.updateChatCount(homeItem)
            } else {
                Toast.makeText(this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initLikeButton(homeItem: HomeItem) = with(binding) {
        updateLikeButton(homeItem)

        imgHomeUnlike.setOnClickListener {
            viewModel.toggleThumbCount(homeItem)
            updateLikeButton(homeItem)
        }
    }

    private fun updateLikeButton(homeItem: HomeItem) {
        val likeButtonResource = if (homeItem.isLiked) R.drawable.home_detail_like
        else R.drawable.home_detail_unlike
        binding.imgHomeUnlike.setImageResource(likeButtonResource)
    }

    private fun displayData(homeItem: HomeItem) = with(binding) {
        txtHomeTitle.text = homeItem.title
        txtHomeDescription.text = homeItem.description
        txtHomeTag.text = homeItem.tag
        imgHomeThumnail.load(homeItem.thumbnail) {
            crossfade(true)
        }
        txtHomeTimestamp.text = formatTimestamp(homeItem.timeStamp)
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

