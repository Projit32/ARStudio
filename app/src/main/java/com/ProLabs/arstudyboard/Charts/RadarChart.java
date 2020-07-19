package com.ProLabs.arstudyboard.Charts;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ProLabs.arstudyboard.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RadarChart extends ChartsUtil{
    private com.github.mikephil.charting.charts.RadarChart radarChart;
    private View view;

    public RadarChart(View view) {
        radarChart=view.findViewById(R.id.floatingChart);
        this.view = view;
    }


    @Override
    protected void fitData(ArrayList<String> label, ArrayList<ArrayList<String>> value, ArrayList<String> header) {
        //Fitting Datas
        ArrayList<ArrayList<RadarEntry>> entryDatas= new ArrayList<>();
        for(int i=0;i<value.get(0).size();i++)
        {
            entryDatas.add(new ArrayList<>());
            for(int j=0;j<value.size();j++)
            {
                entryDatas.get(i).add(new RadarEntry(Float.parseFloat(value.get(j).get(i))));
                Log.v("RadarEntry",value.get(j).get(i));
            }

        }


        RadarData data = new RadarData();
        for(int i=0;i<entryDatas.size();i++)
        {
            RadarDataSet radarDataSet = new RadarDataSet(entryDatas.get(i),header.get(i));
            Log.v("RadarData","dataset "+i);
            for(RadarEntry entry:entryDatas.get(i)) {
                Log.v("RadarDataValue",Float.toString(entry.getValue()));
            }
            radarDataSet.setColor(generateRandomColor());
            radarDataSet.setLineWidth(1.5f);
            data.addDataSet(radarDataSet);
        }

        radarChart.setData(data);
        radarChart.invalidate();

        XAxis xAxis=radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));

        radarChart.setWebColor(Color.WHITE);
        radarChart.setWebLineWidth(3f);
        radarChart.setWebColorInner(Color.DKGRAY);
        radarChart.setWebLineWidthInner(2f);
        radarChart.getDescription().setEnabled(false);
        radarChart.setClickable(true);
        radarChart.setTouchEnabled(true);

        radarChart.animateY(2000, Easing.EaseInOutCubic);


        //Listeners
        radarChart.setOnChartGestureListener(this);
        radarChart.setOnChartValueSelectedListener(this);
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
        if (radarChart.saveToGallery("RadarChart_" + System.currentTimeMillis(), 100))
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
