package com.ProLabs.arstudyboard.Charts;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.ProLabs.arstudyboard.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;


import java.util.ArrayList;
import java.util.HashMap;

public class LineChart extends ChartsUtil
{
    com.github.mikephil.charting.charts.LineChart lineChart;
    View view;
    public LineChart(View view) {
        this.lineChart = view.findViewById(R.id.floatingChart);
        this.view=view;
    }

    @Override
    protected void fitData(ArrayList<String> label, ArrayList<ArrayList<String>> value, ArrayList<String> header) {
        //Chart Listeners

        //Setters
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.animateY(2000, Easing.EaseInOutCubic);
        lineChart.getDescription().setEnabled(false);

        //Data Filling
        ArrayList<ArrayList<Entry>> entryValues = new ArrayList<>();
        for(int i=0;i<value.get(0).size();i++)
        {
            entryValues.add(new ArrayList<>());
            for(int j=0;j<value.size();j++)
            {
                entryValues.get(i).add(new Entry(j,Float.parseFloat(value.get(j).get(i))));
            }

        }
        ArrayList<ILineDataSet> dataset = new ArrayList<>();


        for(int i=0;i<entryValues.size();i++)
        {
            LineDataSet set = new LineDataSet(entryValues.get(i), header.get(i));
            set.setFillAlpha(110);
            set.setColor(generateRandomColor());
            set.setLineWidth(3f);
            set.setValueTextSize(10f);
            set.setValueTextColor(Color.WHITE);
            dataset.add(set);
        }

        LineData data = new LineData(dataset);
        lineChart.setData(data);

        lineChart.setDragEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setScaleEnabled(true);

        //formatting values
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float val) {
                return label.get((int) val);
            }
        });
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        //Listeners
        lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);

        //lineChart.invalidate();


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
        if (lineChart.saveToGallery("LineChart_" + System.currentTimeMillis(), 100))
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
