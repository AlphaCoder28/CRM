package com.goldmedal.crm.data.model.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.goldmedal.crm.R;
import com.goldmedal.crm.data.model.GetAllAssignedTicketsData;
import com.goldmedal.crm.util.interfaces.AcceptRejectTicketsListener;
import com.zhpan.bannerview.BaseViewHolder;

import java.util.Arrays;
import java.util.Locale;

public class VerticalSlideViewHolder extends BaseViewHolder<GetAllAssignedTicketsData> {


    private final AcceptRejectTicketsListener listener;
    private final Context context;

    public VerticalSlideViewHolder(@NonNull View itemView, int roundCorner, AcceptRejectTicketsListener listener) {
        super(itemView);
        this.listener = listener;
        this.context = itemView.getContext();
    }

//    @Override
//    public void bindData(Integer data, int position, int pageSize) {
//        setImageResource(R.id.banner_image, data);
//    }

    @Override
    public void bindData(GetAllAssignedTicketsData data, int position, int pageSize) {
//        CornerImageView imageView = findView(R.id.banner_image);

        TextView txtTicketNo = findViewById(R.id.txt_ticket_no);
        TextView txtTicketUrgent = findViewById(R.id.txt_ticket_urgent);
        TextView txtTimeSlot = findViewById(R.id.txt_time_slot);
        TextView txtCustName = findViewById(R.id.txt_cust_name);
        TextView txtProductIssue = findViewById(R.id.txt_product_issue);
        TextView txtCustAddress = findViewById(R.id.txt_cust_address);
        ImageView imvAccept = findViewById(R.id.imv_accept);
        ImageView imvDecline = findViewById(R.id.imv_decline);
  //      TextView txtTicketReschedule = findView(R.id.txt_ticket_reschedule);
//        TextView txtTicketClose = findView(R.id.txt_ticket_close);

        txtTicketNo.setSelected(true);
        txtTicketNo.setText(data.getTktno());

        if (Arrays.asList("high", "urgent").contains(data.getTktPriority().toLowerCase(Locale.getDefault()))) {
//        if (data.getTktPriority().equalsIgnoreCase("high")) {
            txtTicketUrgent.setVisibility(View.VISIBLE);
            txtTicketUrgent.setText(context.getString(R.string.str_urgent));
        } else {
            txtTicketUrgent.setVisibility(View.GONE);
            txtTicketUrgent.setText("");
        }

        txtTimeSlot.setText(data.getAppointmentDate() + " | " + data.getTimeSlot());
        txtCustName.setText(data.getCustName());
        txtProductIssue.setText(data.getProductIssues());
        txtCustAddress.setText(data.getCustAddress());

//        txtTicketReschedule.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onRescheduleTicket(data);
//            }
//        });
//
//        txtTicketClose.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onCloseTicket(data);
//            }
//        });


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
}

