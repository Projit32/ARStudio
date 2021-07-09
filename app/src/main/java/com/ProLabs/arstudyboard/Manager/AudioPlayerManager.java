package com.ProLabs.arstudyboard.Manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;

import com.ProLabs.arstudyboard.Manager.AudioActionListeners.OnPauseListener;
import com.ProLabs.arstudyboard.Manager.AudioActionListeners.OnPlayListener;
import com.ProLabs.arstudyboard.Manager.AudioActionListeners.OnStopListener;
import com.ProLabs.arstudyboard.R;

import java.io.IOException;

public class AudioPlayerManager {

    private MediaPlayer mediaPlayer;
    private boolean paused=false,stop=false;
    private AudioVizManager audioVizManager;
    private View view;
    private Uri uri;
    private Context context;
    private String url="";
    private OnPauseListener onPauseListener;
    private OnPlayListener onPlayListener;
    private OnStopListener onStopListener;
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener;

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener) {
        this.onBufferingUpdateListener = onBufferingUpdateListener;
    }

    private void init()
    {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setOnCompletionListener(mp->{
            stop=true;
            stop();
        });


        audioVizManager = new AudioVizManager(view);
        audioVizManager.init(mediaPlayer.getAudioSessionId());
    }
    public void Build(Context context, Uri audioUri, View audioViz) throws IOException {
        this.context = context;
        this.uri = audioUri;
        this.view=audioViz;
        init();
        this.mediaPlayer.setDataSource(context.getApplicationContext(), audioUri);
        this.mediaPlayer.setOnBufferingUpdateListener(this.onBufferingUpdateListener);
    }

    public void Build(String audioUrl, View audioViz) throws IOException {
        this.url=audioUrl;
        this.view=audioViz;
        init();
        this.mediaPlayer.setDataSource(audioUrl);
        this.mediaPlayer.setOnBufferingUpdateListener(this.onBufferingUpdateListener);
        stop=true;
    }

    public void rebuild() throws IOException {
        if (url.equals(""))
        {
            Build(context,uri,view);
        }
        else
        {
            Build(url,view);
        }
        setOnPrepare(mp->{this.play();});
        prepare();
    }

    public void setOnPrepare(MediaPlayer.OnPreparedListener onPreparedListener)
    {
        this.mediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    private void releaseAudioPlayer()
    {
        if(audioVizManager!=null)
        {
            audioVizManager.release();
            audioVizManager=null;
        }

    }

    public void prepare() {
        if(this.mediaPlayer!=null)
            this.mediaPlayer.prepareAsync();
    }

    public void release()
    {
        onStopListener.actionOnStop(this.mediaPlayer);
        paused=false;
        stop=false;
        if(mediaPlayer!=null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        releaseAudioPlayer();
    }
    public void reset()
    {
        this.mediaPlayer.reset();
    }

    public void stop()
    {
        if(this.mediaPlayer!=null) {
            this.mediaPlayer.stop();
        }
        onStopListener.actionOnStop(this.mediaPlayer);
        audioVizManager.hide();
    }
    public void play()
    {
        if(this.mediaPlayer!=null) {
            this.mediaPlayer.start();
        }
        audioVizManager.show();
        onPlayListener.actionOnPlay(this.mediaPlayer);
    }
    public void readyToPlay()
    {
        if (this.mediaPlayer!=null)
        {
            //stop flag is set for future state to be applied
            if(stop)
            {
                stop();
            }
            else
            {
                play();
            }
        }
    }
    public void togglePlayPause() throws IOException {
        if(this.mediaPlayer ==null)
        {
            rebuild();
        }
        else {
            if (stop)
                return;
            if (!paused) {
                mediaPlayer.pause();
                onPauseListener.actionOnPause(this.mediaPlayer);
            } else {
                play();
            }
            paused = !paused;
            stop = false;
        }
    }

    public void toggleStartStop() throws IOException {
        if(this.mediaPlayer ==null)
        {
            rebuild();
        }
        else {
            if (!stop) {
                stop();
                stop = true;
            } else {
                stop = false;
                prepare();
            }
        }

    }


    public void changeSeek(int seekValue)
    {
        mediaPlayer.seekTo(seekValue);
    }

    public void setOnPauseListener(OnPauseListener onPauseListener) {
        this.onPauseListener = onPauseListener;
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    public void setOnStopListener(OnStopListener onStopListener) {
        this.onStopListener = onStopListener;
    }

}
