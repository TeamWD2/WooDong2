package com.wd.woodong2.presentation.customview

import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import com.wd.woodong2.R
import com.wd.woodong2.databinding.CustomRegularBtnBinding

class CustomRegularButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = CustomRegularBtnBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        attrs?.let {
            getAttrs(it, defStyleAttr)
        }
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRegularButton, defStyle, 0)
        setTypeArray(typedArray)
    }

    //디폴트 설정
    private fun setTypeArray(typedArray: TypedArray) {
        binding.btnCustom.text = typedArray.getString(R.styleable.CustomRegularButton_btn_text)
        typedArray.recycle()
    }

    fun setBtnText(text: String) {
        binding.btnCustom.text = text
    }

    fun setBtnOnClickListener(listener: OnClickListener?) {
        binding.btnCustom.setOnClickListener(listener)
    }

    fun setBtnClickable(isClickable: Boolean) {
        binding.btnCustom.isClickable = isClickable
    }
}