package com.goldmedal.crm.common

import android.content.Context
import android.os.Build
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatImageView
import com.zhpan.bannerview.provider.ViewStyleSetter


class CornerImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {

    fun setRoundCorner(radius: Int) {
        //val viewStyleSetter = ViewStyleSetter()
        ViewStyleSetter.applyRoundCorner(this, radius.toFloat())
    }
}