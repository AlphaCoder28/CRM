package com.goldmedal.crm.data.model.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.goldmedal.crm.R;
import com.goldmedal.crm.common.CornerImageView;
import com.goldmedal.crm.data.model.GetAllAssignedTicketsData;

import com.zhpan.bannerview.BaseViewHolder;


public class NoDataBannerHolder extends BaseViewHolder<GetAllAssignedTicketsData> {

    public NoDataBannerHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
      CornerImageView imageView = findView(R.id.banner_image);
       imageView.setRoundCorner(roundCorner);

    }


    @Override
    public void bindData(GetAllAssignedTicketsData data, int position, int pageSize) {


        AppCompatTextView txtInfo = findView(R.id.txtInfo);

        txtInfo.setText("No Assigned Tickets");

    }
}