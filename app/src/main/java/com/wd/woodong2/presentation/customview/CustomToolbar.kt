package com.wd.woodong2.presentation.customview

import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.wd.woodong2.R
import com.wd.woodong2.databinding.CustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    val toolbarBinding = CustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            getAttrs(it, defStyleAttr)
        }
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar, defStyle, 0)
        setTypeArray(typedArray)
    }

    //디폴트 설정
    private fun setTypeArray(typedArray: TypedArray) {
        toolbarBinding.txtToolbarLocation.text = typedArray.getString(R.styleable.CustomToolbar_toolbar_txt_title)
        toolbarBinding.imgToolbarLocation.isVisible = typedArray.getBoolean(R.styleable.CustomToolbar_toolbar_visible_ic_down, false)
        toolbarBinding.imgToolbarRight.setBackgroundResource(typedArray.getResourceId(R.styleable.CustomToolbar_toolbar_img_ic_right, R.drawable.public_ic_search))
        toolbarBinding.imgToolbarRight.isVisible = typedArray.getBoolean(R.styleable.CustomToolbar_toolbar_visible_ic_right, false)
        toolbarBinding.constraintSearch.isVisible = typedArray.getBoolean(R.styleable.CustomToolbar_toolbar_visible_constraint_search, false)
        toolbarBinding.edtToolbarSearch.hint = typedArray.getString(R.styleable.CustomToolbar_toolbar_hint_edt_search)
        typedArray.recycle()
    }

    // 저장 되어있는 위치 정보 보여주기
    fun setToolbarTitleText(text: String) {
        toolbarBinding.txtToolbarLocation.text = text
    }

    // layout(위치 + down icon) 클릭 리스너
    fun setLocationOnClickListener(listener: OnClickListener?) {
        toolbarBinding.linearLayoutLocation.setOnClickListener(listener)
    }

    // 우측 아이콘(검색 or 로그아웃) 클릭 리스너
    fun setRightIcOnClickListener(listener: OnClickListener?) {
        toolbarBinding.imgToolbarRight.setOnClickListener(listener)
    }

    // 우측 아이콘(검색 or 로그아웃) 표시 여부
    fun setRightIcVisible(isVisible: Boolean) {
        toolbarBinding.imgToolbarRight.isVisible = isVisible
    }

    // 검색 아이콘 클릭 시 검색창 표시 여부
    fun setConstraintSearchVisible(isVisible: Boolean) {
        toolbarBinding.constraintSearch.isVisible = isVisible
    }

    // 검색창 현재 표시 여부
    fun isVisibleConstraintSearch(): Boolean {
        return toolbarBinding.constraintSearch.isVisible
    }

    // 검색창 포커싱
    fun setRequestFocusEditText() {
        toolbarBinding.edtToolbarSearch.requestFocus()
    }

    // 검색 데이터 가져오기
    fun getSearchEditText(): String {
        return toolbarBinding.edtToolbarSearch.text.toString()
    }

    // 검색 데이터 지우기
    fun setClearEditText() {
        toolbarBinding.edtToolbarSearch.text.clear()
    }

    // 검색 취소 아이콘 클릭 리스너
    fun setCancelOnClickListener(listener: OnClickListener?) {
        toolbarBinding.imgToolbarCancel.setOnClickListener(listener)
    }

    // 검색 취소 아이콘 표시 여부
    fun setCancelIcVisible(isVisible: Boolean) {
        toolbarBinding.imgToolbarCancel.isVisible = isVisible
    }
}