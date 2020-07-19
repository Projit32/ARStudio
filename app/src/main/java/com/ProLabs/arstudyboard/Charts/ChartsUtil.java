package com.ProLabs.arstudyboard.Charts;

import android.graphics.Color;

import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class ChartsUtil implements OnChartGestureListener, OnChartValueSelectedListener {
    protected abstract void fitData(ArrayList<String> label, ArrayList<ArrayList<String>> value, ArrayList<String> header);

    protected int generateRandomColor()
    {
        Random rnd = new Random();
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public void generateChart(HashMap<String,ArrayList<String>> chartData,ArrayList<String> headers)
    {
        fitData(new ArrayList<>(chartData.keySet()),new ArrayList<>(chartData.values()),headers);
    }
}
