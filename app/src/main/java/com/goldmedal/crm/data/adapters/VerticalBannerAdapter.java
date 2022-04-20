package com.goldmedal.crm.data.adapters;

import android.view.View;

import com.goldmedal.crm.R;
import com.goldmedal.crm.data.model.GetAllAssignedTicketsData;
import com.goldmedal.crm.data.model.viewholder.VerticalSlideViewHolder;
import com.goldmedal.crm.data.model.viewholder.NoDataBannerHolder;
import com.goldmedal.crm.data.network.GlobalConstant;
import com.goldmedal.crm.util.interfaces.AcceptRejectTicketsListener;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

public class VerticalBannerAdapter extends BaseBannerAdapter<GetAllAssignedTicketsData, BaseViewHolder<GetAllAssignedTicketsData>> {

    private int roundCorner;
      private final AcceptRejectTicketsListener listener;

    public VerticalBannerAdapter(int roundCorner,AcceptRejectTicketsListener listener) {
        this.roundCorner = roundCorner;
        this.listener = listener;
    }


    @Override
    protected void onBind(BaseViewHolder<GetAllAssignedTicketsData> holder, GetAllAssignedTicketsData data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public BaseViewHolder<GetAllAssignedTicketsData> createViewHolder(View itemView, int viewType) {

        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return new NoDataBannerHolder(itemView, roundCorner);
        }
        return new VerticalSlideViewHolder(itemView, roundCorner,listener);


    }


//    @Override
//    public int getViewType(int position) {
//        return mList.get(position).getViewType();
//    }


    @Override
    public int getLayoutId(int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return R.layout.info_view;
        }
        return R.layout.item_vertical_slide;
    }
}

