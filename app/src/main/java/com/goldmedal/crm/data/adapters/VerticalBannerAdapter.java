package com.goldmedal.crm.data.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldmedal.crm.R;
import com.goldmedal.crm.data.model.GetAllAssignedTicketsData;
import com.goldmedal.crm.data.network.GlobalConstant;
import com.goldmedal.crm.util.interfaces.AcceptRejectTicketsListener;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

import java.util.Arrays;
import java.util.Locale;

public class VerticalBannerAdapter extends BaseBannerAdapter<GetAllAssignedTicketsData> {

    private int roundCorner;
      private final AcceptRejectTicketsListener listener;
      private Context mContext;

    public VerticalBannerAdapter(int roundCorner, AcceptRejectTicketsListener listener, Context context) {
        this.roundCorner = roundCorner;
        this.listener = listener;
        this.mContext = context;
    }

    @Override
    protected void bindData(BaseViewHolder<GetAllAssignedTicketsData> holder, GetAllAssignedTicketsData data, int position, int pageSize) {
        if (getViewType(position) == GlobalConstant.TYPE_NO_DATA) {
            TextView txtInfo = holder.findViewById(R.id.txtInfo);
            txtInfo.setText("No Assigned Tickets");
        } else {
            TextView txtTicketNo = holder.findViewById(R.id.txt_ticket_no);
            TextView txtTicketUrgent = holder.findViewById(R.id.txt_ticket_urgent);
            TextView txtTimeSlot = holder.findViewById(R.id.txt_time_slot);
            TextView txtCustName = holder.findViewById(R.id.txt_cust_name);
            TextView txtProductIssue = holder.findViewById(R.id.txt_product_issue);
            TextView txtCustAddress = holder.findViewById(R.id.txt_cust_address);
            ImageView imvAccept = holder.findViewById(R.id.imv_accept);
            ImageView imvDecline = holder.findViewById(R.id.imv_decline);

            txtTicketNo.setSelected(true);
            txtTicketNo.setText(data.getTktno());

            if (Arrays.asList("high", "urgent").contains(data.getTktPriority().toLowerCase(Locale.getDefault()))) {
                txtTicketUrgent.setVisibility(View.VISIBLE);
                txtTicketUrgent.setText(mContext.getString(R.string.str_urgent));
            } else {
                txtTicketUrgent.setVisibility(View.GONE);
                txtTicketUrgent.setText("");
            }

            txtTimeSlot.setText(data.getAppointmentDate() + " | " + data.getTimeSlot());
            txtCustName.setText(data.getCustName());
            txtProductIssue.setText(data.getProductIssues());
            txtCustAddress.setText(data.getCustAddress());

            imvAccept.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onAcceptTicket(data);
                }
            });

            imvDecline.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRejectTicket(data);
                }
            });
        }
        //holder.bindData(data, position, pageSize);
    }

    @Override
    public int getLayoutId(int viewType) {
        if (viewType == GlobalConstant.TYPE_NO_DATA) {
            return R.layout.info_view;
        }
        return R.layout.item_vertical_slide;
    }
}

