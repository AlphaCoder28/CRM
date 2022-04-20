package com.goldmedal.crm.ui.ticket

import androidx.annotation.ColorInt
import com.goldmedal.crm.R
import com.goldmedal.crm.common.HeaderItemDecoration

class HeaderDecoration(@ColorInt background: Int, sidePaddingPixels: Int)
    : HeaderItemDecoration(
    background, sidePaddingPixels, R.layout.expandable_item)