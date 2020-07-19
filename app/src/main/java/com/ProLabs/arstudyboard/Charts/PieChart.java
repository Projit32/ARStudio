package com.ProLabs.arstudyboard.Charts;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ProLabs.arstudyboard.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class PieChart extends ChartsUtil {

    com.github.mikephil.charting.charts.PieChart pieChart;
    View view;

    public PieChart(View view) {
        this.pieChart = view.findViewById(R.id.floatingChart);
        this.view = view;
    }


    @Override
    protected void fitData(ArrayList<String> label, ArrayList<ArrayList<String>> value, ArrayList<String> header) {
        //Setup
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.animateY(2000, Easing.EaseInOutCubic);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);

        //Data Filling
        ArrayList<PieEntry> entryValues = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            entryValues.add(new PieEntry(Float.parseFloat(value.get(i).get(0)),label.get(i)));
        }

        PieDataSet dataSet= new PieDataSet(entryValues,header.get(0));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData= new PieData(dataSet);
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setClickable(true);
        pieChart.setTouchEnabled(true);


        pieChart.setData(pieData);
        //Listeners
        pieChart.setOnChartGestureListener(this);
        pieChart.setOnChartValueSelectedListener(this);
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
        if (pieChart.saveToGallery("PieChart_" + System.currentTimeMillis(), 100))
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
