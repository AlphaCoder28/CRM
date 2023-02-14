package com.goldmedal.crm.data.model.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.goldmedal.crm.R;
import com.goldmedal.crm.common.CornerImageView;
import com.goldmedal.crm.data.model.GetAllAssignedTicketsData;

import com.zhpan.bannerview.BaseViewHolder;


public class NoDataBannerHolder extends BaseViewHolder<GetAllAssignedTicketsData> {

    public NoDataBannerHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
      CornerImageView imageView = findViewById(R.id.banner_image);
       imageView.setRoundCorner(roundCorner);

    }


    @Override
    public void bindData(GetAllAssignedTicketsData data, int position, int pageSize) {


        TextView txtInfo = findViewById(R.id.txtInfo);

        txtInfo.setText("No Assigned Tickets");

    }
}