package com.goldmedal.crm.data.model.viewholder;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.goldmedal.crm.R;
import com.goldmedal.crm.data.model.CustomBean;
import com.zhpan.bannerview.BaseViewHolder;

public class CustomPageViewHolder extends BaseViewHolder<CustomBean> {

   // private OnSubViewClickListener mOnSubViewClickListener;
private Context context;


    public CustomPageViewHolder(@NonNull View itemView,@NonNull Context context) {
        super(itemView);
        this.context = context;
    }

    @Override
    public void bindData(CustomBean data, int position, int pageSize) {
        ImageView imageStart = findView(R.id.iv_logo);
        LottieAnimationView lottieView = findView(R.id.banner_image);
        RelativeLayout backgroundView = findView(R.id.rl_background);


        //backgroundView.setBackgroundResource(data.getImageRes());

        lottieView.setAnimation(data.getImageRes());
        lottieView.playAnimation();
        lottieView.setBackgroundColor(ContextCompat.getColor(context, data.getBackgroundRes()));
        //setImageResource(R.id.banner_image, data.getImageRes());
        setOnClickListener(R.id.iv_logo, view -> {
          //  if (null != mOnSubViewClickListener)
              //  mOnSubViewClickListener.onViewClick(view, getAdapterPosition());
        });
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(imageStart, "alpha", 0, 1);
        alphaAnimator.setDuration(1500);
        alphaAnimator.start();
    }

//    public void setOnSubViewClickListener(OnSubViewClickListener subViewClickListener) {
//        mOnSubViewClickListener = subViewClickListener;
//    }
//
//    public interface OnSubViewClickListener {
//        void onViewClick(View view, int position);
//    }
}
