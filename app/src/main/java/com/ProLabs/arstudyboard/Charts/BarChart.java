package com.ProLabs.arstudyboard.Charts;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ProLabs.arstudyboard.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;

import java.util.ArrayList;

public class BarChart extends ChartsUtil
{
    private View view;
    private com.github.mikephil.charting.charts.BarChart barChart;
    private float groupSpace;
    private float barSpace = 0.05f;
    private float barWidth=0.28f;

    public BarChart(View view) {
        this.view = view;
        barChart=view.findViewById(R.id.floatingChart);
    }

    @Override
    protected void fitData(ArrayList<String> label, ArrayList<ArrayList<String>> value, ArrayList<String> header) {
        int numberOfBars=value.get(0).size();
        groupSpace=1-((barWidth+barSpace)*numberOfBars);

        //Adding Dummy
        label.add("");
        ArrayList<String> dummy= new ArrayList<>();
        for (int i=0;i<numberOfBars;i++)
        {
            dummy.add("0");
        }
        value.add(dummy);
        //Setup
        barChart.setDrawBarShadow(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setDrawGridBackground(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(true);



        //Fitting Data
        ArrayList<ArrayList<BarEntry>> barEntries= new ArrayList<>();

        for(int i=0;i<value.get(0).size();i++)
        {
            barEntries.add(new ArrayList<>());
            for(int j=0;j<value.size();j++)
            {
                float val=Float.parseFloat(value.get(j).get(i));
                barEntries.get(i).add(new BarEntry(j,val));
            }

        }

        //House Keeping

        ArrayList<IBarDataSet> barDataSets= new ArrayList<>();
        for(int i=0;i<barEntries.size();i++)
        {
            BarDataSet barDataSet= new BarDataSet(barEntries.get(i),header.get(i));
            barDataSet.setColors(generateRandomColor());
            barDataSets.add(barDataSet);
        }

        BarData barData= new BarData(barDataSets);
        barChart.setData(barData);
        barData.setBarWidth(barWidth);
        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.animateY(2000, Easing.EaseInOutCubic);

        XAxis xAxis= barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setCenterAxisLabels(true);

        if (numberOfBars>1)
            barChart.groupBars(0,groupSpace,barSpace);
        barChart.setVisibleXRangeMaximum(3);
        barChart.setDragEnabled(true);
        barChart.invalidate();
        //Listeners
        barChart.setOnChartGestureListener(this);
        barChart.setOnChartValueSelectedListener(this);
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        if (barChart.saveToGallery("BarChart_" + System.currentTimeMillis(), 100))
            Toast.makeText(view.getContext(), "Saving SUCCESSFUL!",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(view.getContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                    .show();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


}
