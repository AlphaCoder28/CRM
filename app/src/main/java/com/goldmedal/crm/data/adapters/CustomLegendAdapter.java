package com.goldmedal.crm.data.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.goldmedal.crm.R;
import com.goldmedal.crm.common.CircleView;
import com.goldmedal.crm.common.HexColors;

import java.util.ArrayList;

/**
 * Created by akshays on 18-04-2018.
 */

public class CustomLegendAdapter extends RecyclerView.Adapter<CustomLegendAdapter.MyViewHolder> {

    private ArrayList<HexColors> colors;
    private ArrayList<String> att_info_list;


    public CustomLegendAdapter(ArrayList<HexColors> colors, ArrayList<String> arylst_names) {
        att_info_list = arylst_names;
        this.colors = colors;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.legend_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//        try {
            holder.legendView.setFillColor(Color.parseColor(colors.get(position).getColorCode()));
            holder.att_info.setText(att_info_list.get(position));
//            String str_color = "";
//            if (str_color.equalsIgnoreCase(holder.att_info.getText().toString())) {
//                holder.att_info.setTypeface(null, Typeface.BOLD);
//            } else {
//                holder.att_info.setTypeface(null, Typeface.NORMAL);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // holder.iv.setImageResource(imageModelArrayList.get(position).getImage_drawable());
        //holder.time.setText(imageModelArrayList.get(position).getName());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return att_info_list.size();
    }


//    @Override
//    public boolean areAllItemsEnabled() {
//        return false;
//    }

//    @Override
//    public boolean isEnabled(int position) {
//        // Return true for clickable, false for not
//        return false;
//    }

//    public void getLable(String str_color) {
//        this.str_color = str_color;
//        notifyDataSetChanged();
//    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleView legendView;
        TextView att_info;

        public MyViewHolder(View itemView) {
            super(itemView);

            legendView = itemView.findViewById(R.id.view_legend_color);
            att_info = itemView.findViewById(R.id.txt_att_info);
        }

    }
}
