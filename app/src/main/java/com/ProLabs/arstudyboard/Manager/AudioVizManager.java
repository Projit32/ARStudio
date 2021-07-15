package com.ProLabs.arstudyboard.Manager;

import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import com.ProLabs.arstudyboard.R;
import com.gauravk.audiovisualizer.base.BaseVisualizer;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.gauravk.audiovisualizer.visualizer.HiFiVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;

import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class AudioVizManager {
    private static final String TAG = "AudioViz";
    private BaseVisualizer visualizer;
    private View view;
    private Map<Integer, VizCreation> visualizerMap;
    private int randomViewId;

    private void prepareMap(){
        visualizerMap = new HashMap<>();

        // Data insert
            visualizerMap.put(R.id.CircleViz, createCircleViz);
            visualizerMap.put(R.id.WaveViz, createWaveViz);
            visualizerMap.put(R.id.BlobViz, createBlobViz);
            visualizerMap.put(R.id.BarViz, createBarViz);
            visualizerMap.put(R.id.BlastViz, createBlastViz);

    }

    private void setRandomViz(){
        prepareMap();
        randomViewId=(int)visualizerMap.keySet().toArray()[new Random().nextInt(visualizerMap.size())];
        visualizer=visualizerMap.get(randomViewId).createViz(randomViewId);
    }

    public AudioVizManager(View view) {
        this.view = view;

        setRandomViz();
    }

    private VizCreation createCircleViz= (id) ->{
        CircleLineVisualizer circleLineVisualizer = view.findViewById(id);
        circleLineVisualizer.setVisibility(View.VISIBLE);
        circleLineVisualizer.setDrawLine(true);
        return circleLineVisualizer;
    };

    private VizCreation createWaveViz= (id) ->{
        WaveVisualizer waveVisualizer= view.findViewById(id);
        waveVisualizer.setVisibility(View.VISIBLE);
        return waveVisualizer;
    };
    private VizCreation createBlobViz= (id) ->{
        BlobVisualizer blobVisualizer= view.findViewById(id);
        blobVisualizer.setVisibility(View.VISIBLE);
        return blobVisualizer;
    };
    private VizCreation createBarViz= (id) ->{
        BarVisualizer barVisualizer= view.findViewById(id);
        barVisualizer.setVisibility(View.VISIBLE);
        return barVisualizer;
    };
    private VizCreation createBlastViz= (id) ->{
        BlastVisualizer blastVisualizer= view.findViewById(id);
        blastVisualizer.setVisibility(View.VISIBLE);
        return blastVisualizer;
    };



    public void init(int audioSessionId)
    {
        if(audioSessionId!= AudioManager.ERROR) {
            Log.v(TAG, "Audio Tag is " + audioSessionId);
            visualizer.setAudioSessionId(audioSessionId);
        }
        else
            Log.v(TAG, "Audio Tag is -1");
    }

    public void release()
    {
        if(visualizer!=null) {
            view.findViewById(randomViewId).setVisibility(View.GONE);
            visualizer.release();
        }
        else
            Log.v(TAG, "Audio view is null");
    }

    public void hide()
    {
        if(visualizer!=null)
            visualizer.hide();
        else
            Log.v(TAG, "Audio view is null");
    }

    public void show()
    {
        if(visualizer!=null)
            visualizer.show();
        else
            Log.v(TAG, "Audio view is null");
    }


}
