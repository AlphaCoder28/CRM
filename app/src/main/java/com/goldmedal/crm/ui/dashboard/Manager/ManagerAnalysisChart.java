package com.goldmedal.crm.ui.dashboard.Manager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.goldmedal.crm.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ManagerAnalysisChart extends LinearLayout {

    BarChart chartManagerAnalysis;

    public ManagerAnalysisChart(Context context, List<MonthwiseData> list) {
        super(context);

//        View view = LayoutInflater.from(context).inflate(R.layout.manager_analysis_chart, this, true);
//
//        chartManagerAnalysis = view.findViewById(R.id.chartManagerAnalysis);

//        divisionWisePerformance();
    }


    private void divisionWisePerformance() {

        float barWidth;
        float barSpace;
        float groupSpace;

        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;

        int start = 0;
        int end = 2;

        chartManagerAnalysis.setDescription(null);
        chartManagerAnalysis.setDrawGridBackground(false);

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Wiring Devices");
        xVals.add("Wires,Lights & Pipes");

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        yVals1.add(new BarEntry(1, 100000.0f));
        yVals2.add(new BarEntry(1, 200000.0f));
        yVals1.add(new BarEntry(2,  300000.0f));
        yVals2.add(new BarEntry(2, 400000.0f));

        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "Previous Year Sales");
        set1.setColor(Color.RED);
        set1.setValueFormatter(new MyValueFormatter());
        set2 = new BarDataSet(yVals2, "Current Year Sales");
        set2.setColor(Color.BLUE);
        set2.setValueFormatter(new MyValueFormatter());
        BarData data = new BarData(set1, set2);
        //data.setValueFormatter(new LargeValueFormatter());
        data.setValueTextSize(12f);
        chartManagerAnalysis.setData(data);

        chartManagerAnalysis.getBarData().setBarWidth(barWidth);

        chartManagerAnalysis.getXAxis().setAxisMinimum(start);
        chartManagerAnalysis.getXAxis().setAxisMaximum(end);
        chartManagerAnalysis.groupBars(start, groupSpace, barSpace);
        chartManagerAnalysis.getData().setHighlightEnabled(false);
        chartManagerAnalysis.invalidate();

        Legend l = chartManagerAnalysis.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(10f);
        l.setYEntrySpace(5f);
        l.setTextSize(10f);

        //x-axis
        XAxis xAxis = chartManagerAnalysis.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        //Y-axis
        chartManagerAnalysis.getAxisRight().setEnabled(false);
        YAxis leftAxis = chartManagerAnalysis.getAxisLeft();
        //  leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
    }

    public static class MyValueFormatter extends ValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("##,##,##,##0.00"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }

}
