package com.goldmedal.crm.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldmedal.crm.R;

public class TimelineSegment extends LinearLayout {

    View rootView;
    TextView upperText;
    TextView startLine;
    TextView endLine;
    ImageView circleView;
    TextView bottomText;

    public TimelineSegment(Context context) {
        super(context);
        init(context);
    }

    public TimelineSegment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimelineSegment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {

        rootView=inflate(context, R.layout.timeline_segment, this );

        upperText= rootView.findViewById(R.id.top_data);
        bottomText= rootView.findViewById(R.id.bottom_data);


    }

    public  void setUpperText(String string)
    {
        upperText.setText(string);
    }

    public void setBottomText(String string)
    {
        bottomText.setText(string);
    }

}