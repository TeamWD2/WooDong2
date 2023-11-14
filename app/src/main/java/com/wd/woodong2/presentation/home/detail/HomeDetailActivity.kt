package com.wd.woodong2.presentation.home.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeDetailActivityBinding
import com.wd.woodong2.presentation.home.add.HomeAddViewModel
import com.wd.woodong2.presentation.home.add.HomeAddViewModelFactory
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeDetailActivity : AppCompatActivity() {
    private lateinit var binding: HomeDetailActivityBinding
    //private lateinit var viewModel: HomeDetailViewModel
    private lateinit var homeItem: HomeItem
    private lateinit var commentsAdapter: CommentListAdapter

    companion object {
        private const val EXTRA_HOME_ITEM = "extra_home_item"
        fun homeDetailActivityNewIntent(context: Context, homeItem: HomeItem): Intent =
            Intent(context, HomeDetailActivity::class.java).apply {
                putExtra(EXTRA_HOME_ITEM, homeItem)
            }
    }
    private val viewModel: HomeDetailViewModel by viewModels{
        HomeDetailViewModelFactory(this)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initViewModel()

        initView()
    }


    private fun initView() {

        viewModel.commentsLiveData.observe(this) { comments ->
            commentsAdapter.updateComments(comments)
            updateCommentCount(comments.size) // 댓글 수를 업데이트하는 메소드
            binding.textViewNoComments.visibility = if (comments.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerviewComment.visibility = if (comments.isEmpty()) View.GONE else View.VISIBLE
        }
        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this@HomeDetailActivity, R.color.egg_yellow_toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

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
            updateCommentCount(updatedComments.size)
        }

        viewModel.thumbCountLiveData.observe(this) { thumbCount ->
            updateLikeCountUI(thumbCount)
        }
        initLikeButton(homeItem)

    }

//    private fun initViewModel() {
//        viewModel = ViewModelProvider(this)[HomeDetailViewModel::class.java]
//    }

    private fun updateCommentCount(count: Int) {
        binding.txtCommentCount.text = count.toString()
        binding.textViewNoComments.visibility = if (count == 0) View.VISIBLE else View.GONE
        binding.recyclerviewComment.visibility = if (count == 0) View.GONE else View.VISIBLE
    }

    private fun updateLikeCountUI(thumbCount: Int) {
        binding.txtDetailThumbCount.text = thumbCount.toString()
    }
    private fun setupCommentsRecyclerView() {
        commentsAdapter = CommentListAdapter(homeItem, currentUser = viewModel.getUserInfo(),viewModel)
        binding.recyclerviewComment.layoutManager = NoScrollLinearLayoutManager(this)
        binding.recyclerviewComment.adapter = commentsAdapter
    }


    private fun initCommentButton(homeItem: HomeItem) {
        binding.btnSave.setOnClickListener {
            val commentContent = binding.editComment.text.toString()
            if (commentContent.isNotBlank()) {
                viewModel.postComment(homeItem, commentContent)
                binding.editComment.text.clear()
                viewModel.updateChatCount(homeItem)
                hideKeyboard()
            } else {
                Toast.makeText(this, "댓글을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
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
        val likeButtonResource = if (homeItem.isLiked) R.drawable.home_detail_favorite_filled
        else R.drawable.home_list_favorite
        binding.imgHomeUnlike.setImageResource(likeButtonResource)
    }

    private fun displayData(homeItem: HomeItem) = with(binding) {
        txtHomeTitle.text = homeItem.title
        imgDetailProfile.load(R.drawable.public_default_wd2_ivory)
        txtHomeDescription.text = homeItem.description
        txtHomeTag.text = homeItem.tag
        if (homeItem.thumbnail.isNullOrEmpty()) {
            // 이미지 URL이 없는 경우, 이미지 뷰를 숨깁니다.
            imgHomeThumnail.visibility = View.GONE
            thumbnailCradview.visibility = View.GONE
        } else {
            // 이미지 URL이 있는 경우, 이미지 뷰를 보이게 하고 이미지를 로드합니다.
            imgHomeThumnail.visibility = View.VISIBLE
            thumbnailCradview.visibility = View.VISIBLE
            imgHomeThumnail.load(homeItem.thumbnail) {
                crossfade(true)
            }
        }
        txtHomeTimestamp.text = formatTimestamp(homeItem.timeStamp)
        txtCommentCount.text = homeItem.chatCount.toString()
        txtDetailThumbCount.text = homeItem.thumbCount.toString()
        txtDetailUser.text = homeItem.name
        txtDetailLocation.text = HomeMapActivity.extractLocationInfo(homeItem.location)


        imgBack.setOnClickListener {
            finish()
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

