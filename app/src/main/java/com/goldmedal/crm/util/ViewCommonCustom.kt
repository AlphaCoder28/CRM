package com.goldmedal.crm.util

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView
import com.goldmedal.crm.R
import com.goldmedal.crm.common.RotateLoading


class ViewCommonCustom @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var rlMain: RelativeLayout? = null
    private var progressBar: RotateLoading? = null
    private var lottieImage: LottieAnimationView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.common_view, this, true)

        rlMain = findViewById(R.id.rlMain)
        progressBar = findViewById(R.id.progress_bar)

        lottieImage = findViewById(R.id.lottie_image)
    }


    fun showProgressBar() {
        progressBar?.start()

        lottieImage?.visibility = View.GONE
    }

    fun showNoData() {
        progressBar?.stop()

        lottieImage?.visibility = View.VISIBLE

        lottieImage?.setAnimation(R.raw.no_data)
        lottieImage?.playAnimation()

    }

    fun showServerError() {
        progressBar?.stop()

        lottieImage?.visibility = View.VISIBLE

        lottieImage?.setAnimation(R.raw.server_error)
        lottieImage?.playAnimation()
    }

    fun showNoInternet() {
        progressBar?.stop()

        lottieImage?.visibility = View.VISIBLE

        lottieImage?.setAnimation(R.raw.no_connection)
        lottieImage?.playAnimation()
    }

    fun hide() {
        progressBar?.stop()
        lottieImage?.cancelAnimation()
        lottieImage?.visibility = View.GONE
    }

}