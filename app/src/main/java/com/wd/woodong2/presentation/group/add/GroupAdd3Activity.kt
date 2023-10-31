package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAdd3ActivityBinding

class GroupAdd3Activity : AppCompatActivity() {

    companion object {
        private const val ADD_ITEM_GROUP_TAG = "add_item_group_tag"
        private const val ADD_ITEM_GROUP_NAME = "add_item_group_name"
        private const val ADD_ITEM_GROUP_INTRO = "add_item_group_intro"
        private const val ADD_ITEM_AGE_LIMIT = "add_item_age_limit"
        private const val ADD_ITEM_MEMBER_LIMIT = "add_item_member_limit"
        private const val ADD_ITEM_PASSWORD = "add_item_password"

        fun newAdd3Intent(
            context: Context,
            groupTag: String?,
            groupName: String?,
            groupIntro: String?,
            ageLimit: String?,
            memberLimit: String?,
            password: String?
        ): Intent =
            Intent(context, GroupAdd3Activity::class.java).apply {
                putExtra(ADD_ITEM_GROUP_TAG, groupTag)
                putExtra(ADD_ITEM_GROUP_NAME, groupName)
                putExtra(ADD_ITEM_GROUP_INTRO, groupIntro)
                putExtra(ADD_ITEM_AGE_LIMIT, ageLimit)
                putExtra(ADD_ITEM_MEMBER_LIMIT, memberLimit)
                putExtra(ADD_ITEM_PASSWORD, password)
            }
    }

    private lateinit var binding: GroupAdd3ActivityBinding

    private val viewModel: GroupAddSharedViewModel by viewModels {
        GroupAddSharedViewModelFactory()
    }

    private val groupTag by lazy {
        intent.getStringExtra(ADD_ITEM_GROUP_TAG)
    }
    private val groupName by lazy {
        intent.getStringExtra(ADD_ITEM_GROUP_NAME)
    }
    private val groupIntro by lazy {
        intent.getStringExtra(ADD_ITEM_GROUP_INTRO)
    }
    private val ageLimit by lazy {
        intent.getStringExtra(ADD_ITEM_AGE_LIMIT)
    }
    private val memberLimit by lazy {
        intent.getStringExtra(ADD_ITEM_MEMBER_LIMIT)
    }
    private val password by lazy {
        intent.getStringExtra(ADD_ITEM_PASSWORD)
    }

    private lateinit var currentItem: String
    private var mainImage: String? = null
    private var backgroundImage: String? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    when (currentItem) {
                        "imgMainImage" -> {
                            binding.imgMainImage.setImageURI(uri)
                            binding.imgMainImageInit.isVisible = false
                            mainImage = uri.toString()
                        }

                        "imgBackgroundImage" -> {
                            binding.imgBackgroundImage.setImageURI(uri)
                            binding.imgBackgroundImageInit.isVisible = false
                            backgroundImage = uri.toString()
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAdd3ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        imgBack.setOnClickListener {
            finish()
        }

        imgMainImage.setOnClickListener {
            currentItem = "imgMainImage"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        imgBackgroundImage.setOnClickListener {
            currentItem = "imgBackgroundImage"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        btnAddGroup.setOnClickListener {
            if (
                groupTag.isNullOrEmpty()
                || groupName.isNullOrEmpty()
                || groupIntro.isNullOrEmpty()
                || ageLimit.isNullOrEmpty()
                || memberLimit.isNullOrEmpty()
                || password.isNullOrEmpty()
                || mainImage.isNullOrEmpty()
                || backgroundImage.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this@GroupAdd3Activity,
                    R.string.group_add_toast_no_info,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.setGroupAddItem(combineGroupItem())
                setResult(RESULT_OK, Intent())
                finish()
            }
        }
    }

    private fun combineGroupItem(): List<GroupAddSetItem> =
        mutableListOf<GroupAddSetItem>().apply {
            add(
                GroupAddSetItem.GroupAddMain(
                    groupName = groupName,
                    groupTag = groupTag,
                    ageLimit = ageLimit,
                    memberLimit = memberLimit,
                    password = password,
                    mainImage = mainImage,
                    backgroundImage = backgroundImage
                )
            )
            add(
                GroupAddSetItem.GroupAddIntroduce(
                    title = "소개",
                    introduce = groupIntro,
                    groupTag = groupTag,
                    ageLimit = ageLimit,
                    memberLimit = memberLimit
                )
            )
            add(
                GroupAddSetItem.GroupAddMember(
                    title = "멤버",
                    memberList = listOf(
                        GroupAddSetItem.AddMember(
                            "-NhImSiData",
                            "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
                            "sinw",
                            "권선동"
                        )
                    )
                )
            )
        }
}