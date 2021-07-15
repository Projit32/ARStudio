package com.ProLabs.arstudyboard.Creators;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ProLabs.arstudyboard.MainActivity;
import com.ProLabs.arstudyboard.Manager.AudioPlayerManager;
import com.ProLabs.arstudyboard.R;
import com.ProLabs.arstudyboard.RenderableItems.AudioItem;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class FloatingAudioCreator {

    MainActivity mainActivity;
    private Anchor anchor;
    private Uri uri;
    private String audioUrl="";
    private ArrayList<AudioPlayerManager> audioPlayerManagers;
    private Map<String, AudioPlayerManager> cloudAudio;
    private final int[] musicImages={R.drawable.word_note,R.drawable.note,R.drawable.staff_notation,R.drawable.beats};


    public FloatingAudioCreator(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        audioPlayerManagers = new ArrayList<>();
        cloudAudio = new HashMap<>();
    }

    public void build(Anchor anchor, Uri uri)
    {
        this.anchor = anchor;
        this.uri =uri;
        createRenderables(false, anchor.getCloudAnchorId());
    }

    public void buildFromAudioItem(AudioItem audioItem, Anchor anchor)
    {
        this.anchor=anchor;
        this.audioUrl=audioItem.getDownloadURL();
        createRenderables(true, audioItem.cloudAnchorID);
    }

    private void createRenderables(Boolean isAudioItem, String cloudAnchorID)
    {
        ViewRenderable.builder()
                .setView(mainActivity,R.layout.mediaplayer_layout)
                .build()
                .thenAccept(viewRenderable -> {
                    addModelToScreen(anchor, viewRenderable, isAudioItem,cloudAnchorID);
                })
                .exceptionally(throwable -> null);

    }
    private void addModelToScreen(Anchor anchor, ViewRenderable viewRenderable, Boolean isAudioItem, String cloudAnchorID)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        mainActivity.addNodeToMap(anchor.getCloudAnchorId(),anchorNode);
        TransformableNode transformableNode = new TransformableNode(mainActivity.arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(viewRenderable);
        mainActivity.arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();

        setupView(viewRenderable.getView(),anchorNode,isAudioItem, cloudAnchorID);

    }


    public void release()
    {
        Thread deleteTask = new Thread(()->{
            audioPlayerManagers.forEach(AudioPlayerManager::release);
            cloudAudio.clear();
            audioPlayerManagers.clear();
        });
        deleteTask.start();
        try {
            deleteTask.join();
        } catch (InterruptedException e) {
            mainActivity.showErrorFlashbar(e.toString());
        }

    }

    public void deleteAudioById(String id)
    {
        try {
            cloudAudio.get(id).stop();
            cloudAudio.get(id).release();
            audioPlayerManagers.remove(cloudAudio.get(id));
            cloudAudio.remove(id);
        }
        catch (Exception e)
        {
            mainActivity.showErrorFlashbar(e.getMessage());
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setupView(View view, AnchorNode anchorNode, Boolean isAudioItem, String cloudAnchorID)
    {
        ImageButton pausePlayButton = view.findViewById(R.id.AudioPauseButton);
        ImageButton stopPlayButton = view.findViewById(R.id.AudioPlayButton);
        ImageView audioImage = view.findViewById(R.id.AudioImage);
        audioImage.setBackgroundResource(getRandomImage());
        AudioPlayerManager audioPlayerManager = new AudioPlayerManager();
        try {
            //TODO: check buffering logic
            audioPlayerManager.setOnBufferingUpdateListener((mp, percent) -> {
                Toast.makeText(mainActivity, "Buffer Percent : "+percent+"%", Toast.LENGTH_SHORT).show();
            });
            if(isAudioItem) {
                pausePlayButton.setVisibility(View.INVISIBLE);
                stopPlayButton.setVisibility(View.INVISIBLE);
                audioPlayerManager.Build(audioUrl,view);
                audioPlayerManager.setOnPrepare(mp -> {
                    audioPlayerManager.readyToPlay();
                });
            }
            else
            {

                audioPlayerManager.Build(mainActivity, uri, view);
                audioPlayerManager.setOnPrepare(mediaPlayer -> {
                   audioPlayerManager.play();
                });
            }

            audioPlayerManager.setOnPauseListener(mediaPlayer -> {
                pausePlayButton.post(()->{
                    pausePlayButton.setImageDrawable(mainActivity.getDrawable(R.drawable.play));
                });
                stopPlayButton.post(()->{
                    stopPlayButton.setImageDrawable(mainActivity.getDrawable(R.drawable.stop));
                });
            });
            audioPlayerManager.setOnPlayListener(mediaPlayer -> {
                pausePlayButton.post(()->{
                    pausePlayButton.setVisibility(View.VISIBLE);
                    pausePlayButton.setImageDrawable(mainActivity.getDrawable(R.drawable.pause));
                });
                stopPlayButton.post(()->{
                    stopPlayButton.setVisibility(View.VISIBLE);
                    stopPlayButton.setImageDrawable(mainActivity.getDrawable(R.drawable.stop));
                });

            });
            audioPlayerManager.setOnStopListener(mediaPlayer -> {
                pausePlayButton.post(()->{
                    pausePlayButton.setVisibility(View.INVISIBLE);
                    pausePlayButton.setImageDrawable(mainActivity.getDrawable(R.drawable.pause));
                });
                stopPlayButton.post(()->{
                    stopPlayButton.setVisibility(View.VISIBLE);
                    stopPlayButton.setImageDrawable(mainActivity.getDrawable(R.drawable.play));
                });

            });


            //View Settings
            view.setOnClickListener(v->{
                if(mainActivity.delete) {
                    mainActivity.deleteNodeFromScreen(anchorNode, anchor.getCloudAnchorId(), MainActivity.AnchorType.AUDIO);
                    audioPlayerManager.stop();
                    audioPlayerManager.release();
                    audioPlayerManagers.remove(audioPlayerManager);
                    cloudAudio.remove(cloudAnchorID);
                }
            });
            pausePlayButton.setOnClickListener(v->{
                try {
                    audioPlayerManager.togglePlayPause();
                } catch (Exception e) {
                    mainActivity.showErrorFlashbar("Something went wrong: "+e.toString());
                    e.printStackTrace();
                }
            });
            stopPlayButton.setOnClickListener(v->{
                try {
                    audioPlayerManager.toggleStartStop();
                } catch (Exception e) {
                    mainActivity.showErrorFlashbar("Something went wrong: "+e.toString());
                    e.printStackTrace();
                }
            });

            audioPlayerManager.prepare();
            audioPlayerManagers.add(audioPlayerManager);
        }
        catch (Exception e)
        {
            mainActivity.showErrorFlashbar(e.getMessage());
        }
        if (!isAudioItem)
        {
            mainActivity.saveToFireBase(uri, anchor);
        }
        if (!cloudAnchorID.isEmpty()) {
            cloudAudio.put(cloudAnchorID, audioPlayerManager);
        }

    }
    private int getRandomImage() {
        return  musicImages[new Random().nextInt(musicImages.length)];
    }




}
