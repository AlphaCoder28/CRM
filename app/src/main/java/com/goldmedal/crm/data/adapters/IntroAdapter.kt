package com.goldmedal.crm.data.adapters

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.CustomBean
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class IntroAdapter(private val context: Context) : BaseBannerAdapter<CustomBean>() {

    override fun bindData(
        holder: BaseViewHolder<CustomBean>?,
        data: CustomBean?,
        position: Int,
        pageSize: Int
    ) {
        val imageStart: ImageView = holder!!.findViewById(R.id.iv_logo)
        val lottieView: LottieAnimationView = holder.findViewById(R.id.banner_image)
        val backgroundView: RelativeLayout = holder.findViewById(R.id.rl_background)

        lottieView.setAnimation(data!!.imageRes)
        lottieView.playAnimation()
        lottieView.setBackgroundColor(ContextCompat.getColor(context, data.backgroundRes))
        holder.setOnClickListener(R.id.iv_logo, View.OnClickListener { view: View? -> })
        val alphaAnimator = ObjectAnimator.ofFloat(imageStart, "alpha", 0f, 1f)
        alphaAnimator.duration = 1500
        alphaAnimator.start()
        //holder.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_intro_view
    }
}