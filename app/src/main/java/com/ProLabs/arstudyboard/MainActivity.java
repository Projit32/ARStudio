package com.ProLabs.arstudyboard;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ProLabs.arstudyboard.Creators.FloatingAudioCreator;
import com.ProLabs.arstudyboard.Creators.FloatingGraphCreator;
import com.ProLabs.arstudyboard.Creators.FloatingImageCreator;
import com.ProLabs.arstudyboard.Creators.FloatingTextCreator;
import com.ProLabs.arstudyboard.Drawing.Stroke;
import com.ProLabs.arstudyboard.Manager.ActivityResultManager;
import com.ProLabs.arstudyboard.Manager.AnimationManager;
import com.ProLabs.arstudyboard.Manager.FirebaseManager;
import com.ProLabs.arstudyboard.Manager.URLManager;
import com.ProLabs.arstudyboard.RenderableItems.AudioItem;
import com.ProLabs.arstudyboard.RenderableItems.GraphItem;
import com.ProLabs.arstudyboard.RenderableItems.ImageItem;
import com.ProLabs.arstudyboard.RenderableItems.ModelItem;
import com.ProLabs.arstudyboard.RenderableItems.TextItem;
import com.ProLabs.arstudyboard.Utility.ExcelFileProcessor;
import com.ProLabs.arstudyboard.Utility.ItemList;
import com.ProLabs.arstudyboard.Utility.LiveObject;
import com.ProLabs.arstudyboard.Utility.RetrofitClient;
import com.ProLabs.arstudyboard.Utility.TutorialBuilder;
import com.andrognito.flashbar.Flashbar;
import com.google.android.filament.ColorGrading;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.CameraStream;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.EngineInstance;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener
 {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float DRAW_DISTANCE = 0.13f;
    private static final Color WHITE = new Color(android.graphics.Color.WHITE);
    private static final Color RED = new Color(android.graphics.Color.RED);
    private static final Color GREEN = new Color(android.graphics.Color.GREEN);
    private static final Color BLUE = new Color(android.graphics.Color.BLUE);
    private static final Color BLACK = new Color(android.graphics.Color.BLACK);
    private volatile Bitmap image;
    private volatile ArrayList<ArrayList<String>> ExcelData= new ArrayList<>();
    private volatile Uri AudioUri;
    private AnchorNode anchorNode;
    private final ArrayList<Stroke> strokes = new ArrayList<>();
    private Material material;
    private Stroke currentStroke;
    private VideoRecorder videoRecorder;
    private int camcorderProfile=CamcorderProfile.QUALITY_1080P;
    public CloudARFragment arFragment;
    RetrofitClient retrofitClient= new RetrofitClient();
    FloatingActionButton addBtn,devToggleBtn,drawBtn,deleteBtn,helpBtn;
    String Asset="";
    RecyclerView itemRecyclerView;
    public Boolean delete=false, storagePermission=false,busy=false, micPermission=false;
    volatile Boolean draw =false,ResolvedAnchor=false;
    LinearLayout colorPanel;
    LinearLayout controlPanel;
    ImageButton Record,FloatingText,FloatingImage,graphBtn,FloatingAudio;
    private FloatingImageCreator floatingImageCreator;
    private FloatingTextCreator floatingTextCreator;
    private FloatingGraphCreator floatingGraphCreator;
    private FloatingAudioCreator floatingAudioCreator;
    Boolean Hosting=false;
    public HashMap<String,AnchorNode> placedNode = new HashMap<>();
    private ArrayList<AnchorNode> nodeBook= new ArrayList<>();
    private FirebaseManager firebaseManager = new FirebaseManager(this);
    public volatile Queue<LiveObject> liveObjects= new LinkedList<>();
    public String BaseURL;
    private Button liveBtn;
    volatile Flashbar flashbar=null;
    ArrayList<Pair<View,String>> tutorials;
    //ArrayList<AnimationManager> animationManagers= new ArrayList<>();
    ProgressBar progressBar;
    Handler handler = new Handler();
    public static Boolean NetworkAvailable=true;
    public ExcelFileProcessor excelFileProcessor;
    protected final ActivityResultManager<Intent, ActivityResult> activityLauncher= ActivityResultManager.registerActivityForResult(this);

    // Anchor enum
     public enum AnchorType{
        MODEL,
        ANIMATED,
        GRAPH,
        PICTURE,
        TEXT,
        AUDIO,
        NONE
    }

    AnchorType RenderableItemType=AnchorType.NONE,LiveItemType=AnchorType.NONE;
    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Finders
        addBtn= findViewById(R.id.addbtn);
        itemRecyclerView = findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        devToggleBtn=findViewById(R.id.dev_channel_toggle);
        deleteBtn=findViewById(R.id.deleteBtn);
        drawBtn=findViewById(R.id.drawBtn);
        colorPanel = findViewById(R.id.colorPanel);
        controlPanel = findViewById(R.id.controlsPanel);
        controlPanel.setVisibility(View.GONE);
        Record= findViewById(R.id.record);
        FloatingText=findViewById(R.id.FloatingText);
        FloatingImage=findViewById(R.id.FloatingImage);
        graphBtn= findViewById(R.id.statBtn);
        FloatingAudio = findViewById(R.id.FloatingAudio);
        floatingImageCreator= new FloatingImageCreator(this);
        floatingTextCreator= new FloatingTextCreator(this);
        floatingGraphCreator = new FloatingGraphCreator(this);
        floatingAudioCreator = new FloatingAudioCreator(this);
        liveBtn=findViewById(R.id.test);
        helpBtn=findViewById(R.id.helpBtn);
        progressBar=findViewById(R.id.progressBar);

        handler.postDelayed(()->{showFlashBar("Press the + button to add objects to the screen");},550);
        URLManager.resetDevChannelUrl();


        //AR Fragment
        try {
            arFragment = (CloudARFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
            arFragment.getArSceneView().getPlaneRenderer().setEnabled(true);
            arFragment.setOnViewCreatedListener(onViewCreatedListener);
            arFragment.setOnSessionConfigurationListener(onSessionConfigurationListener);
            arFragment.setOnTapArPlaneListener(addObjects);
            arFragment.getArSceneView().getScene().addOnUpdateListener(this);


            //Drawing Part
            MaterialFactory.makeOpaqueWithColor(this, WHITE)
                    .thenAccept(material1 -> material = material1.makeCopy())
                    .exceptionally(
                            throwable -> {
                                displayError(throwable);
                                throw new CompletionException(throwable);
                            });

            ImageView clearButton = findViewById(R.id.clearButton);
            clearButton.setOnClickListener(
                    v -> {
                        for (Stroke stroke : strokes) {
                            stroke.clear();
                        }
                        strokes.clear();
                    });
            ImageView undoButton = findViewById(R.id.undoButton);
            undoButton.setOnClickListener(
                    v -> {
                        if (strokes.size() < 1) {
                            return;
                        }
                        int lastIndex = strokes.size() - 1;
                        strokes.get(lastIndex).clear();
                        strokes.remove(lastIndex);
                    });

            helpBtn.setOnClickListener(v -> {
                new TutorialBuilder(this).with(tutorials).show();
            });
            helpBtn.setOnLongClickListener(v -> {
                String url = "https://youtu.be/TLXhzyL4WCE";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            });

            //Button Functons
            liveBtn.setOnLongClickListener(v -> {
                if (!NetworkAvailable) {
                    showErrorFlashbar("Internet connection isn't available.");
                    return false;
                }

                View roomView = LayoutInflater.from(this).inflate(R.layout.roomnumberalert, null);
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this).setView(roomView);
                EditText roomNumber = roomView.findViewById(R.id.inputRoomNumber);
                alertDialog.setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            try {
                                if (isValidRoomNumber(roomNumber.getText().toString())) {
                                    showLiveFlashbar("Please wait...", true);
                                    deleteLiveSession();
                                    deleteEveryNode();
                                    firebaseManager.initializeRoom(roomNumber.getText().toString());
                                    firebaseManager.setOnChangeListeners();
                                } else {
                                    showErrorFlashbar("Invalid Room Number. Please try something with alphabets and numbers.");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.cancel();
                        });

                //showing
                alertDialog.create().show();
                return true;
            });

            liveBtn.setOnClickListener(v -> {

                if (!NetworkAvailable) {
                    showErrorFlashbar("Internet connection isn't available.");
                    return;
                }
                checkAllPermissions();
                if (!firebaseManager.isRoomInitialized()) {
                    showLiveFlashbar("Long Press on the button to enter room number", false);
                    return;
                }
                busy = false;
                Hosting = !Hosting;
                if (Hosting) {

                    showLiveFlashbar("Live session started", false);
                } else {
                    showFlashBar("Live session ended");
                    try {
                        deleteLiveSession();
                        deleteEveryNode();
                    } catch (InterruptedException e) {
                        showErrorFlashbar(e.getMessage());
                    }
                }
            });

            addBtn.setOnClickListener(v -> {
                BaseURL = URLManager.getItemFolderUrl();
                if (!NetworkAvailable) {
                    showErrorFlashbar("Internet connection isn't available.");
                    return;
                }
                if (itemRecyclerView.getVisibility() == View.GONE) {
                    refreshItemList();
                    arFragment.setOnTapArPlaneListener(addObjects);
                    hideButtons();
                    itemRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    itemRecyclerView.setVisibility(View.GONE);
                    showButtons();
                }
            });
            addBtn.setOnLongClickListener(v -> {
                BaseURL = URLManager.getAnimatedItemFolderUrl();
                if (!NetworkAvailable) {
                    showErrorFlashbar("Internet connection isn't available.");
                } else {
                    if (itemRecyclerView.getVisibility() == View.GONE) {
                        refreshAnimatedItemList();
                        arFragment.setOnTapArPlaneListener(addAnimatedObjects);
                        hideButtons();
                        itemRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            });


            devToggleBtn.setOnClickListener((view) -> {
                if (!URLManager.DevChannelUrl.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setPositiveButton("Toggle Channel", (dialog, which) -> {
                                toggleChannelURL();
                                dialog.dismiss();
                            })
                            .setNegativeButton("Reset Dev Channel URL", (dialog, which) -> {
                                URLManager.clearDevChannelURL();
                                showFlashBar("Dev Channel URL has been reset");
                                dialog.dismiss();
                            })
                            .setTitle("Choose an action")
                            .setMessage("Your can either toggle between Dev and Stable channels or reset the current Dev Channel URL.")
                            .setCancelable(true)
                            .show();
                } else {
                    View v = getLayoutInflater().inflate(R.layout.url_alert_dialog, null);
                    new AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("OK", (dialog, which) -> {
                                EditText editText = v.findViewById(R.id.dev_url);
                                if (!editText.getText().toString().isEmpty()) {
                                    URLManager.DevChannelUrl = editText.getText().toString();
                                    toggleChannelURL();
                                } else {
                                    showErrorFlashbar("Empty URL");
                                }
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .setCancelable(false)
                            .show();
                }
            });

            devToggleBtn.setOnLongClickListener(view -> {
                String url = "https://github.com/Projit32/ARStudio-Sceneform-SDK-1.16.0/tree/master/Model%20Hosting";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            });

            deleteBtn.setOnClickListener((view) -> {
                if (busy && Hosting) {
                    showBusyMessage();
                    return;
                }
                delete = !delete;
                if (!delete) {
                    showFlashBar("Add Mode Activated");
                } else {
                    showFlashBar("Delete Mode Activated");
                }
            });

            drawBtn.setOnClickListener((view) -> {
                draw = !draw;

                if (draw) {
                    controlPanel.setVisibility(View.VISIBLE);
                    showFlashBar("Draw Mode Enabled");
                    arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
                    arFragment.setOnTapArPlaneListener(null);
                    arFragment.getArSceneView().getScene().addOnPeekTouchListener(Drawing);
                } else {
                    controlPanel.setVisibility(View.GONE);
                    colorPanel.setVisibility(View.GONE);
                    showFlashBar("Draw Mode Disabled");
                    arFragment.setOnTapArPlaneListener(addObjects);
                    arFragment.getArSceneView().getPlaneRenderer().setEnabled(true);
                    arFragment.getArSceneView().getScene().removeOnPeekTouchListener(Drawing);
                    //arFragment.getArSceneView().getScene().addOnPeekTouchListener(deleteObjects);

                }

            });


            Record.setOnClickListener((view) -> {
                storagePermission = checkStoragePermission();
                try {
                    if (videoRecorder == null) {
                        videoRecorder = new VideoRecorder(this);
                        videoRecorder.setSceneView(arFragment.getArSceneView());
                    }
                    if (storagePermission) {
                        int orientation = getResources().getConfiguration().orientation;
                        videoRecorder.setVideoQuality(camcorderProfile, orientation);
                        new Thread(() -> {
                            handler.post(this::toggleRotationLock);
                            if (videoRecorder.onToggleRecord(this)) {
                                Record.post(() -> Record.setBackground(getDrawable(R.drawable.recorderstart)));
                            } else {
                                Record.post(() -> {
                                    Record.setBackground(getDrawable(R.drawable.recorderstop));
                                    videoRecorder=null;
                                });
                            }
                        }).start();

                    } else {
                        showErrorFlashbar("Storage/microphone permission isn't granted");
                    }
                } catch (Exception e) {
                    showErrorFlashbar(e.getMessage());
                }

            });

            Record.setOnLongClickListener(view -> {
                View resolutionSelectorView = getLayoutInflater().inflate(R.layout.resolution_selector, null);
                LinearLayout UHD = resolutionSelectorView.findViewById(R.id.uhd_profile), HD = resolutionSelectorView.findViewById(R.id.hd_profile), HQ = resolutionSelectorView.findViewById(R.id.hq_profile);
                UHD.setOnClickListener(v -> {
                    camcorderProfile = CamcorderProfile.QUALITY_2160P;
                    Toast.makeText(this, "4K profile Selected", Toast.LENGTH_SHORT).show();
                });
                HD.setOnClickListener(v -> {
                    camcorderProfile = CamcorderProfile.QUALITY_1080P;
                    Toast.makeText(this, "1080P profile Selected", Toast.LENGTH_SHORT).show();
                });
                HQ.setOnClickListener(v -> {
                    camcorderProfile = CamcorderProfile.QUALITY_720P;
                    Toast.makeText(this, "720P profile Selected", Toast.LENGTH_SHORT).show();
                });

                new AlertDialog.Builder(this)
                        .setView(resolutionSelectorView)
                        .setCancelable(true)
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveButton("Done", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                return true;
            });

            FloatingText.setOnClickListener(view -> {
                if (busy && Hosting) {
                    showBusyMessage();
                    return;
                }
                arFragment.setOnTapArPlaneListener(addFloatingText);
                showFlashBar("Tap on the plane to add the text.");


            });

            FloatingImage.setOnClickListener(v -> {
                if (busy && Hosting) {
                    showBusyMessage();
                    return;
                }
                storagePermission = checkStoragePermission();
                if (storagePermission) {
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    activityLauncher.launch(gallery, result -> {
                        if(result.getResultCode() == Activity.RESULT_OK)
                        {
                            try {
                                image= MediaStore.Images.Media.getBitmap(getContentResolver(),result.getData().getData());
                                showFlashBar("Tap on the plane to add the image.");
                                arFragment.setOnTapArPlaneListener(addFloatingImage);

                            } catch (IOException e) {
                                showErrorFlashbar(e.getMessage());
                            }
                        }
                        else
                        {
                            showErrorFlashbar("Error fetching File");
                        }
                    });
                } else {
                    showErrorFlashbar("Storage permission isn't granted");
                }
            });

            FloatingAudio.setOnClickListener(v->{
                if (busy && Hosting) {
                    showBusyMessage();
                    return;
                }
                storagePermission = checkStoragePermission();
                micPermission = checkMicPermission();
                if (storagePermission && micPermission) {
                    Intent gallery = new Intent();
                    gallery.setType("audio/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    activityLauncher.launch(gallery, result -> {
                        if(result.getResultCode() == Activity.RESULT_OK)
                        {
                            try {
                                AudioUri = result.getData().getData();
                                //showFlashBar(VideoUri);
                                showFlashBar("Tap on a surface to add the audio player.");
                                arFragment.setOnTapArPlaneListener(addAddFloatingAudio);

                            }
                            catch (Exception e)
                            {
                                showErrorFlashbar(e.toString());
                            }
                        }
                        else
                        {
                            showErrorFlashbar("Error fetching File");
                        }
                    });
                } else {
                    showErrorFlashbar("Storage/Mic permission isn't granted");
                }
            });

            graphBtn.setOnClickListener(v -> {
                if (busy && Hosting) {
                    showBusyMessage();
                    return;
                }
                storagePermission = checkStoragePermission();
                if (storagePermission) {
                    Intent exelFile = new Intent();
                    exelFile.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    exelFile.setAction(Intent.ACTION_GET_CONTENT);
                    activityLauncher.launch(exelFile, result -> {
                        if(result.getResultCode() == Activity.RESULT_OK)
                        {
                            try {
                                Thread readExcel= new Thread(()->{
                                    excelFileProcessor= new ExcelFileProcessor(result.getData().getData(), this);
                                    excelFileProcessor.readExcelFileFromAssets();
                                    ExcelData.clear();
                                    ExcelData = excelFileProcessor.getExelData();
                                });
                                readExcel.start();
                                readExcel.join();
                                showFlashBar("Tap on a surface to place a graph.");
                                arFragment.setOnTapArPlaneListener(addFloatingGraph);
                            }
                            catch (Exception e)
                            {
                                showErrorFlashbar(e.getMessage());
                            }
                        }
                        else
                        {
                            showErrorFlashbar("Error fetching File");
                        }
                    });

                } else {
                    showErrorFlashbar("Storage permission isn't granted");
                }
            });
            setUpColorPickerUi();

            prepareTutorial();
            handler.postDelayed(() -> {
                new TutorialBuilder(this).with(tutorials).buildShowCase("First_Tutorial");
            }, 1000);
        }
        catch(Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isValidRoomNumber(String roomNumber)
    {
        return (roomNumber != null && roomNumber.chars().allMatch(Character::isLetterOrDigit));
    }

    private void toggleChannelURL()
    {
        try {
            URLManager.toggleChannel();
            retrofitClient = new RetrofitClient();
            if (URLManager.isDevChannel) {
                showFlashBar("Switched to Dev Channel");
            } else {
                showFlashBar("Stable Channel Activated");
            }
        }
        catch(Exception e)
        {
            showErrorFlashbar("Error Fetching the URL : "+e.getMessage());
        }
    }

    private void checkAllPermissions()
    {
        if (!micPermission)
            checkMicPermission();
        if (!storagePermission)
            checkStoragePermission();
    }

    private void prepareTutorial()
    {
        tutorials=new ArrayList<>();
        tutorials.add(new Pair<View, String>(addBtn,"Add items to your surroundings.\n\n" +
                "For this, you'll need to scan the area. Start by pointing at some plane non-" +
                "reflective surface. The rotating hand should disappear once the scanning starts" +
                " and white dots should appear on the surface. Then select an asset from the list " +
                "by clicking this + button. If nothing is chosen, it will show an error message. " +
                "You can rotate the model by using two finger twist or resize using pinch zoom.\n\n" +
                "Press to get static assets and long press to get animated assets."));

        tutorials.add(new Pair<View, String>(drawBtn,"This button enables the draw button. You can draw" +
                " on the surface of the screen or hold on a single point and move your phone around." +
                " Choose colors, undo or clear all drawings from the toolbar which will appear at the top " +
                "of the screen when the draw mode is enabled.\n\nWhen draw mode is enabled, you can't add other " +
                "models to screen, so make sure to disable before other things by pressing this button again."));

        tutorials.add(new Pair<View, String>(deleteBtn,"This button enables the Delete mode. During this mode," +
                " you can delete any model from the screen.\n\nDrawings can only be deleted from the Draw mode toolbar." +
                "\n\nJust like Draw mode, you can't add models during Delete mode, you can disable it by pressing it again."));

        tutorials.add(new Pair<View, String>(devToggleBtn,"This button allows you to toggle between Dev Channel and Stable" +
                " Channel.\n\nDev channel allows you to host your own 3D models and use them. All you need to do is host your" +
                " 3D models and the APIs to a hosting service. To learn how to host your own 3D models and to use the Dev" +
                " Channel, press and hold this button."));

        tutorials.add(new Pair<View, String>(FloatingText,"You can add texts on translucent glass on any scanned area. " +
                "Click this button, select any of the templates, add your text and press OK.\n\n" +
                "You have to press this button every time you want to add a text\n\n" +
                "Just like the models, these are can be twisted and resized"));

        tutorials.add(new Pair<View, String>(FloatingImage,"You can add images from the gallery on any scanner area. " +
                "Same process, click this every time you want to add an image, select the image from gallery and then" +
                "tap wherever you want to place the image.\n\nThese can also be twisted and resized like others"));

        tutorials.add(new Pair<View, String>(graphBtn,"You can add Graphs on any scanner area. All you need to have an " +
                "Excel file with .xls or .xlsx extension from your internal storage.\n\nThe excel file" +
                " should have a column with all the names/labels and another with their corresponding numerical values.\n\n" +
                "Check the box if your Excel file has column headers and tap where you want to place it."));

        tutorials.add(new Pair<View, String>(Record,"Record anything and everything. If it's red, its recording, else it's not." +
                "\n\nTap and hold to change resolution of recording. High resolution might cause frame drops."));

        tutorials.add(new Pair<>(FloatingAudio, "You can augmented audio players. Select a song from you gallery, and place it on any scanned area."));

        tutorials.add(new Pair<View, String>(liveBtn,"Creating a Live session allows you to show/involve your friends to do all" +
                " the above mentioned stuff (Except drawing). Make sure that you have scanned the area thoroughly from all sides " +
                "for 30 seconds or more and from all the angles.\n\nCreate/Join a Virtual Room by long pressing the live button " +
                "and then single tap to start/stop the live session. Share the virtual room number to your friends to allow them to join"));

        tutorials.add(new Pair<View, String>(helpBtn,"Press and hold to see the full tutorial on Youtube."));

    }


     private void deleteLiveSession() throws InterruptedException
     {
         Thread destroy=new Thread(()->{
             liveObjects.clear();
             forfitPendingModelDownloads();
             firebaseManager.destroyReferences();
             firebaseManager = null;
             firebaseManager = new FirebaseManager(this);
             floatingAudioCreator.release();
         });
         destroy.start();
         destroy.join();
     }

     // Delete every node

     private void deleteEveryNode()
     {
         nodeBook.forEach(offlineNode -> arFragment.getArSceneView().getScene().removeChild(offlineNode));
         placedNode.clear();
         floatingAudioCreator.release();
     }

     private void showBusyMessage()
    {
        showErrorFlashbar("An Anchor is being Hosted, Please Wait...");
    }

    //Interface
     ArFragment.OnViewCreatedListener onViewCreatedListener = (fragment,arSceneView)->{
        // Currently, the tone-mapping should be changed to FILMIC
        // because with other tone-mapping operators except LINEAR
        // the inverseTonemapSRGB function in the materials can produce incorrect results.
        // The LINEAR tone-mapping cannot be used together with the inverseTonemapSRGB function.
        Renderer renderer = arSceneView.getRenderer();

        if (renderer != null) {
            renderer.getFilamentView().setColorGrading(
                    new ColorGrading.Builder()
                            .toneMapping(ColorGrading.ToneMapping.FILMIC)
                            .build(EngineInstance.getEngine().getFilamentEngine())
            );
        }
        arSceneView.getCameraStream()
                .setDepthOcclusionMode(CameraStream.DepthOcclusionMode
                        .DEPTH_OCCLUSION_ENABLED);
    };

    BaseArFragment.OnSessionConfigurationListener onSessionConfigurationListener = ((session, config) -> {
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        }
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
    });



    //Ar Scene Tap Listener
     public BaseArFragment.OnTapArPlaneListener addObjects= (hitResult,plane,motionEvent)->{
        ResolvedAnchor=false;
        if(busy && Hosting)
        {
            showBusyMessage();
            return;
        }
         if(!delete) {
             if (!Asset.equals("")) {
                 Anchor anchor=hitResult.createAnchor();
                 if(Hosting)
                 {
                     anchor=arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                     cloudanchor(anchor);
                     appAnchorState=AppAnchorState.HOSTING;
                     RenderableItemType=AnchorType.MODEL;
                     showLiveFlashbar("Hosting Anchor.. Please wait till the anchor is hosted.",true);
                     return;
                 }
                 else
                    placeModel(anchor);

             } else
                 showErrorFlashbar("No Asset Chosen");
         }
         else {
             showFlashBar("Delete Mode is on, please turn that off!");
         }
     };

     BaseArFragment.OnTapArPlaneListener addAnimatedObjects= (hitResult,plane,motionEvent)->{
         ResolvedAnchor=false;
         if(busy && Hosting)
         {
             showBusyMessage();
             return;
         }
         if(!delete) {
             if (!Asset.equals("")) {
                 Anchor anchor=hitResult.createAnchor();
                 if(Hosting)
                 {
                     anchor=arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                     cloudanchor(anchor);
                     appAnchorState=AppAnchorState.HOSTING;
                     RenderableItemType=AnchorType.ANIMATED;
                     showLiveFlashbar("Hosting Anchor.. Please wait till the anchor is hosted.",true);
                 }
                 else
                     placeAnimatedModel(anchor);

             } else
                 showErrorFlashbar("No Asset Chosen");
         }
         else {
             showFlashBar("Delete Mode is on, please turn that off!");
         }
     };


     BaseArFragment.OnTapArPlaneListener addFloatingText=(hitResult,plane,motionEvent)->{
         Anchor anchor=hitResult.createAnchor();
         if(busy && Hosting)
         {
             showBusyMessage();
             return;
         }
         if(!delete) {
             if (Hosting) {
                 anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                 cloudanchor(anchor);
                 appAnchorState = AppAnchorState.HOSTING;
                 RenderableItemType = AnchorType.TEXT;
                 showLiveFlashbar("Hosting Anchor.. Please wait till the anchor is hosted.", true);
                 return;
             } else
                 floatingTextCreator.Build(anchor);
             arFragment.setOnTapArPlaneListener(addObjects);
         }
         else
         {
             showFlashBar("Delete Mode is on, please turn that off!");
         }

     };


     BaseArFragment.OnTapArPlaneListener addFloatingImage=(hitResult, plane, motionEvent) -> {
         Anchor anchor=hitResult.createAnchor();
         if(busy && Hosting)
         {
             showBusyMessage();
             return;
         }
         if(!delete) {
             if (Hosting) {
                 anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                 cloudanchor(anchor);
                 appAnchorState = AppAnchorState.HOSTING;
                 RenderableItemType = AnchorType.PICTURE;
                 showLiveFlashbar("Hosting Anchor.. Please wait till the anchor is hosted.", true);
                 return;
             } else
                 floatingImageCreator.Build(anchor, image);
             arFragment.setOnTapArPlaneListener(addObjects);
         }
         else {
             showFlashBar("Delete Mode is on, please turn that off!");
         }
     };


     BaseArFragment.OnTapArPlaneListener addFloatingGraph=(hitResult,plane,motionEvent)->{
         Anchor anchor=hitResult.createAnchor();
         if(busy && Hosting)
         {
             showBusyMessage();
             return;
         }
         if (!delete) {
             if (Hosting) {
                 anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                 cloudanchor(anchor);
                 appAnchorState = AppAnchorState.HOSTING;
                 RenderableItemType = AnchorType.GRAPH;
                 showLiveFlashbar("Hosting Anchor.. Please wait till the anchor is hosted.", true);
                 return;
             } else
                 floatingGraphCreator.build(ExcelData, anchor);
             arFragment.setOnTapArPlaneListener(addObjects);
         }
         else
         {
             showFlashBar("Delete Mode is on, please turn that off!");
         }
     };

     BaseArFragment.OnTapArPlaneListener addAddFloatingAudio=(hitResult,plane,motionEvent)->{
         Anchor anchor=hitResult.createAnchor();
         if(busy && Hosting)
         {
             showBusyMessage();
             return;
         }
         if (!delete) {
             if (Hosting) {
                 anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                 cloudanchor(anchor);
                 appAnchorState = AppAnchorState.HOSTING;
                 RenderableItemType = AnchorType.AUDIO;
                 showLiveFlashbar("Hosting Anchor.. Please wait till the anchor is hosted.", true);
                 return;
             }
             else
                 floatingAudioCreator.build(anchor,AudioUri);
             arFragment.setOnTapArPlaneListener(addObjects);
         }
         else
         {
             showFlashBar("Delete Mode is on, please turn that off!");
         }
     };

    Scene.OnPeekTouchListener Drawing = (hitTestResult, tap) -> {

        int action = tap.getAction();
        Camera camera = arFragment.getArSceneView().getScene().getCamera();
        Ray ray = camera.screenPointToRay(tap.getX(), tap.getY());
        Vector3 drawPoint = ray.getPoint(DRAW_DISTANCE);
        if (action == MotionEvent.ACTION_DOWN) {
            if (anchorNode == null) {
                ArSceneView arSceneView = arFragment.getArSceneView();
                com.google.ar.core.Camera coreCamera = arSceneView.getArFrame().getCamera();
                if (coreCamera.getTrackingState() != TrackingState.TRACKING) {
                    return;
                }
                Pose pose = coreCamera.getPose();
                anchorNode = new AnchorNode(arSceneView.getSession().createAnchor(pose));
                anchorNode.setParent(arSceneView.getScene());
            }
            currentStroke = new Stroke(anchorNode, material);
            strokes.add(currentStroke);
            currentStroke.add(drawPoint);
        } else if (action == MotionEvent.ACTION_MOVE && currentStroke != null) {
            currentStroke.add(drawPoint);
        }};


    public void addNodeToMap(String id,AnchorNode anchorNode)
    {
        nodeBook.add(anchorNode);
        if(Hosting)
            placedNode.put(id, anchorNode);
    }

    volatile ArrayList<Pair<CompletableFuture<ModelRenderable>,Anchor>> modelRenderables= new ArrayList<>();
    volatile ArrayList<Pair<CompletableFuture<ModelRenderable>,Anchor>> animatedAodelRenderables= new ArrayList<>();
    private volatile Boolean isProcessing=false;

    private void placeModel(Anchor anchor)
    {
        CompletableFuture<ModelRenderable> model=ModelRenderable.builder()
                .setSource(
                        this,
                        Uri.parse(Asset)
                )
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .setRegistryId(Asset)
                .build();
        modelRenderables.add(new Pair<>(model,anchor));
        showDownloadStatus();
    }

     private void placeAnimatedModel(Anchor anchor)
     {
         CompletableFuture<ModelRenderable> model=ModelRenderable.builder()
                 .setSource(
                         this,
                         Uri.parse(Asset)
                 )
                 .setIsFilamentGltf(true)
                 .setAsyncLoadEnabled(true)
                 .setRegistryId(Asset)
                 .build();
         animatedAodelRenderables.add(new Pair<>(model,anchor));
         showDownloadStatus();
     }

     private synchronized void checkModelDownloadStatus()
     {
         if (isProcessing || modelRenderables.isEmpty())
         {
             return;
         }


         isProcessing=true;
         ListIterator<Pair<CompletableFuture<ModelRenderable>,Anchor>> iterator= modelRenderables.listIterator();
         ArrayList<Pair<CompletableFuture<ModelRenderable>,Anchor>> toBeDeleted= new ArrayList<>();
         while (iterator.hasNext())
         {
             Pair<CompletableFuture<ModelRenderable>,Anchor> item= iterator.next();
             if(item.first.isDone())
             {
                 item.first.thenAccept(modelRenderable -> {
                     addModelToScreen(item.second,modelRenderable);
                 }).exceptionally(throwable -> {
                     showErrorFlashbar(throwable.getMessage());
                     return null;
                 });
                 toBeDeleted.add(item);
             }
         }
         toBeDeleted.forEach(item->{
             modelRenderables.remove(item);
             showDownloadStatus();
         });
         isProcessing=false;
     }


     private synchronized void checkAnimatedDownloadstatus()
     {

         if (isProcessing || animatedAodelRenderables.isEmpty())
         {
             return;
         }

         isProcessing=true;
         ListIterator<Pair<CompletableFuture<ModelRenderable>,Anchor>> iterator= animatedAodelRenderables.listIterator();
         ArrayList<Pair<CompletableFuture<ModelRenderable>,Anchor>> toBeDeleted= new ArrayList<>();
         while (iterator.hasNext())
         {
             Pair<CompletableFuture<ModelRenderable>,Anchor> item= iterator.next();
             if(item.first.isDone())
             {
                 item.first.thenAccept(modelRenderable -> {
                     addAnimatedModelToScreen(item.second,modelRenderable);
                 }).exceptionally(throwable -> {
                     showErrorFlashbar(throwable.getMessage());
                     return null;
                 });
                 toBeDeleted.add(item);
             }
         }

         toBeDeleted.forEach(item->{
             animatedAodelRenderables.remove(item);
             showDownloadStatus();
         });
         isProcessing=false;
     }


     private void showDownloadStatus()
     {
         if (animatedAodelRenderables.isEmpty() && modelRenderables.isEmpty())
         {
             showFlashBar("Models Downloaded");
         }
         else
         {
             showDownloadFlashbar("Downloading "+animatedAodelRenderables.size()+" Animated Model(s) & "+modelRenderables.size()+" Static Model(s).");
         }

     }


     private void addModelToScreen(Anchor anchor, ModelRenderable modelRenderable) {
         AnchorNode anchorNode = new AnchorNode(anchor);
         addNodeToMap(anchor.getCloudAnchorId(),anchorNode);
         TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
         transformableNode.setParent(anchorNode);
         transformableNode.setRenderable(modelRenderable);
         arFragment.getArSceneView().getScene().addChild(anchorNode);
         transformableNode.select();
         transformableNode.setOnTapListener((HitTestResult hitTestResult, MotionEvent Event) ->
         {
             if(delete) {
                 deleteNodeFromScreen(anchorNode,anchor.getCloudAnchorId(),AnchorType.MODEL);
             }

         });
         if(!ResolvedAnchor)
             saveToFireBase(anchor, Asset,false);
     }

     private void addAnimatedModelToScreen(Anchor anchor, ModelRenderable modelRenderable) {
         AnchorNode anchorNode = new AnchorNode(anchor);
         addNodeToMap(anchor.getCloudAnchorId(),anchorNode);
         TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
         transformableNode.setParent(anchorNode);
         transformableNode.setRenderable(modelRenderable).animate(true).start();
         arFragment.getArSceneView().getScene().addChild(anchorNode);
         transformableNode.select();


         //AnimationManager animationManager= new AnimationManager(transformableNode);
         //animationManager.buildFilamentAnimation();
         //animationManagers.add(animationManager);
         transformableNode.setOnTapListener((HitTestResult hitTestResult, MotionEvent Event) ->
         {
             if(delete) {
                 //animationManagers.remove(animationManager);
                 deleteNodeFromScreen(anchorNode,anchor.getCloudAnchorId(),AnchorType.MODEL);
             }

         });
         if(!ResolvedAnchor)
             saveToFireBase(anchor, Asset,true);
     }


     //Delete Node
     public void deleteNodeFromScreen(AnchorNode anchorNode,String id,AnchorType anchorType)
     {
         arFragment.getArSceneView().getScene().removeChild(anchorNode);
         nodeBook.remove(anchorNode);

         if(!id.equals("") && Hosting)
         {
             deleteLiveNodeFromScreen(id);
             deleteLiveNodeFromFirebase(id,anchorType);
         }

     }


    public void refreshItemList()
    {
        if(!NetworkAvailable){
            showErrorFlashbar("No Internet Available");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Call<ArrayList<ItemList>> call = retrofitClient.getItemListCall();
        try {
            call.enqueue(new Callback<ArrayList<ItemList>>() {
                @Override
                public void onResponse(Call<ArrayList<ItemList>> call, Response<ArrayList<ItemList>> response) {
                    itemRecyclerView.setAdapter(new ItemListAdapter(response.body(), MainActivity.this));
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ArrayList<ItemList>> call, Throwable t) {
                    showErrorFlashbar("Server Unavailable. Error: "+t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        catch (Exception e)
        {
            showErrorFlashbar(e.getMessage());
        }
    }

     public void refreshAnimatedItemList()
     {
         if(!NetworkAvailable){
             showErrorFlashbar("No Internet Available");
             return;
         }
         progressBar.setVisibility(View.VISIBLE);
         Call<ArrayList<ItemList>> call=retrofitClient.getAnimatedItemListCall();
         try {
             call.enqueue(new Callback<ArrayList<ItemList>>() {
                 @Override
                 public void onResponse(Call<ArrayList<ItemList>> call, Response<ArrayList<ItemList>> response) {
                     itemRecyclerView.setAdapter(new ItemListAdapter(response.body(), MainActivity.this));
                     progressBar.setVisibility(View.GONE);
                 }

                 @Override
                 public void onFailure(Call<ArrayList<ItemList>> call, Throwable t) {
                     showErrorFlashbar("Server Unavailable. Error: "+t.getMessage());
                     progressBar.setVisibility(View.GONE);
                 }
             });
         }
         catch (Exception e)
         {
             showErrorFlashbar(e.getMessage());
         }
     }


    public void hideButtons()
    {
        graphBtn.setVisibility(View.GONE);
        Record.setVisibility(View.GONE);
        FloatingText.setVisibility(View.GONE);
        FloatingImage.setVisibility(View.GONE);
        liveBtn.setVisibility(View.GONE);
        FloatingAudio.setVisibility(View.GONE);
        deleteBtn.hide();
        drawBtn.hide();
        devToggleBtn.hide();
        helpBtn.hide();
    }

    public void showButtons()
    {
        graphBtn.setVisibility(View.VISIBLE);
        Record.setVisibility(View.VISIBLE);
        FloatingText.setVisibility(View.VISIBLE);
        FloatingImage.setVisibility(View.VISIBLE);
        liveBtn.setVisibility(View.VISIBLE);
        FloatingAudio.setVisibility(View.VISIBLE);
        deleteBtn.show();
        drawBtn.show();
        devToggleBtn.show();
        helpBtn.show();
    }

    // Drawing functions

    private void setUpColorPickerUi() {
        ImageView colorPickerIcon = (ImageView) findViewById(R.id.colorPickerIcon);
        colorPanel.setVisibility(View.GONE);
        colorPickerIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (controlPanel.getVisibility() == View.VISIBLE) {
                            controlPanel.setVisibility(View.GONE);
                            colorPanel.setVisibility(View.VISIBLE);
                        }
                    }
                });

        ImageView whiteCircle = (ImageView) findViewById(R.id.whiteCircle);
        whiteCircle.setOnClickListener(
                (onClick) -> {
                    setColor(WHITE);
                    colorPickerIcon.setImageResource(R.drawable.ic_selected_white);
                });
        ImageView redCircle = (ImageView) findViewById(R.id.redCircle);
        redCircle.setOnClickListener(
                (onClick) -> {
                    setColor(RED);
                    colorPickerIcon.setImageResource(R.drawable.ic_selected_red);
                });

        ImageView greenCircle = (ImageView) findViewById(R.id.greenCircle);
        greenCircle.setOnClickListener(
                (onClick) -> {
                    setColor(GREEN);
                    colorPickerIcon.setImageResource(R.drawable.ic_selected_green);
                });

        ImageView blueCircle = (ImageView) findViewById(R.id.blueCircle);
        blueCircle.setOnClickListener(
                (onClick) -> {
                    setColor(BLUE);
                    colorPickerIcon.setImageResource(R.drawable.ic_selected_blue);
                });

        ImageView blackCircle = (ImageView) findViewById(R.id.blackCircle);
        blackCircle.setOnClickListener(
                (onClick) -> {
                    setColor(BLACK);
                    colorPickerIcon.setImageResource(R.drawable.ic_selected_black);
                });

        ImageView rainbowCircle = (ImageView) findViewById(R.id.rainbowCircle);
        rainbowCircle.setOnClickListener(
                (onClick) -> {
                    setTexture(R.drawable.rainbow_texture);
                    colorPickerIcon.setImageResource(R.drawable.ic_selected_rainbow);
                });
    }


    @SuppressWarnings({"FutureReturnValueIgnored"})
    private void setTexture(int resourceId) {
        Texture.builder()
                .setSource(arFragment.getContext(), resourceId)
                .setSampler(Texture.Sampler.builder().setWrapMode(Texture.Sampler.WrapMode.REPEAT).build())
                .build()
                .thenCompose(
                        texture -> MaterialFactory.makeOpaqueWithTexture(arFragment.getContext(), texture))
                .thenAccept(material1 -> material = material1.makeCopy())
                .exceptionally(
                        throwable -> {
                            displayError(throwable);
                            throw new CompletionException(throwable);
                        });

        colorPanel.setVisibility(View.GONE);
        controlPanel.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings({"FutureReturnValueIgnored"})
    private void setColor(Color color) {
        MaterialFactory.makeOpaqueWithColor(arFragment.getContext(), color)
                .thenAccept(material1 -> material = material1.makeCopy())
                .exceptionally(
                        throwable -> {
                            displayError(throwable);
                            throw new CompletionException(throwable);
                        });
        colorPanel.setVisibility(View.GONE);
        controlPanel.setVisibility(View.VISIBLE);
    }

    private void animateAvailableAnimatedModels()
    {
        //animationManagers.forEach(AnimationManager::animateModel);
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        checkQueue();
        checkUpdateAnchor();
        //animateAvailableAnimatedModels();
        checkModelDownloadStatus();
        checkAnimatedDownloadstatus();
    }



     ImageItem imageItem;
     TextItem textItem;
     GraphItem graphItem;
     ModelItem modelItem;
     AudioItem audioItem;

     private synchronized void checkQueue() {
        if(Hosting && !liveObjects.isEmpty())
        {
            if(appAnchorState!=AppAnchorState.HOSTING && appAnchorState!=AppAnchorState.RESOLVING)
            {
                LiveObject item=liveObjects.poll();
                LiveItemType=item.getType();
                Object object=item.getRenderableObject();
                switch (LiveItemType){
                    case TEXT:
                         textItem=(TextItem)object;
                         resolveAnchor(textItem.getcloudAnchorID());
                        break;
                    case GRAPH:
                         graphItem=(GraphItem)object;
                         resolveAnchor(graphItem.getcloudAnchorID());
                        break;
                    case PICTURE:
                         imageItem=(ImageItem)object;
                         resolveAnchor(imageItem.getcloudAnchorID());
                         break;
                    case MODEL:
                        modelItem=(ModelItem)object;
                        Asset=modelItem.getAssetURL();
                        resolveAnchor(modelItem.getcloudAnchorID());
                        break;
                    case AUDIO:
                        audioItem=(AudioItem)object;
                        resolveAnchor(audioItem.getcloudAnchorID());
                        break;
                }
            }
        }
     }

     //Resolvers
     //Need to check whether resolved anchor returns the anchorID or not
     private void resolveAnchor(String anchorID)
     {
         Anchor resolvedAnchor=arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorID);
         cloudanchor(resolvedAnchor);
         appAnchorState=AppAnchorState.RESOLVING;
     }

     private void displayError(Throwable throwable) {
        Log.e(TAG, "Unable to create material", throwable);
        showErrorFlashbar("Unable to create material");
    }

     @Override
     protected void onPause() {
         super.onPause();
         storagePermission=checkStoragePermission();
     }

     private final ActivityResultLauncher<String[]> requestPermissionLauncher =
             registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permission ->{
                 boolean allGranted = true;

                 for (Boolean isGranted : permission.values()){
                     if (!isGranted){
                         allGranted = false;
                         break;
                     }
                 }

                 if (allGranted){
                     showFlashBar("Required Permissions are grated");
                 } else {
                     showFlashBar("Required Permissions are not grated");
                 }

             });



     public boolean checkStoragePermission()
     {
         //Write Permission
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

             String[] permissions = new String[]{
                     android.Manifest.permission.READ_MEDIA_IMAGES,
                     android.Manifest.permission.READ_MEDIA_AUDIO,
                     android.Manifest.permission.READ_MEDIA_VIDEO,
                     android.Manifest.permission.CAMERA,
             };


             List<String> permissionsTORequest = new ArrayList<>();
             for (String permission : permissions){
                 if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                     permissionsTORequest.add(permission);
                 }
             }

             if (permissionsTORequest.isEmpty())
                 return true;
             else{
                 requestPermissionLauncher.launch(permissionsTORequest.toArray(new String[0]));
                 return false;
             }


         } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             String[] permissions = new String[]{
                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
             };


             List<String> permissionsTORequest = new ArrayList<>();
             for (String permission : permissions){
                 if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                     permissionsTORequest.add(permission);
                 }
             }

             if (permissionsTORequest.isEmpty())
                 return true;
             else{
                 requestPermissionLauncher.launch(permissionsTORequest.toArray(new String[0]));
                 return false;
             }

         }
         else {
             if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
             }


             return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
         }
     }

     public  boolean checkMicPermission()
     {
//         if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
//         {
//             ActivityCompat.requestPermissions(this, new  String[]{Manifest.permission.RECORD_AUDIO},2);
//         }
//
//         return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED;


         if (ContextCompat.checkSelfPermission(
                 this, Manifest.permission.RECORD_AUDIO) ==
                 PackageManager.PERMISSION_GRANTED) {
             // You can use the API that requires the permission.
             ContextCompat.checkSelfPermission(
                     this, Manifest.permission.RECORD_AUDIO);
         } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                 this, Manifest.permission.RECORD_AUDIO)) {
             // In an educational UI, explain to the user why your app requires this
             // permission for a specific feature to behave as expected, and what
             // features are disabled if it's declined. In this UI, include a
             // "cancel" or "no thanks" button that lets the user continue
             // using your app without granting the permission.
             showFlashBar("Microphone Permissions are required for recording video.");

         } else {
             // You can directly ask for the permission.
             requestPermissions(
                     new String[] { Manifest.permission.RECORD_AUDIO },
                     2);
         }

         return (ContextCompat.checkSelfPermission(
                 this, Manifest.permission.RECORD_AUDIO) ==
                 PackageManager.PERMISSION_GRANTED);
     }


     //Cloud Anchor Methods
     private Anchor cloudAnchor=null;
     enum  AppAnchorState
     {
         NONE,
         HOSTING,
         HOSTED,
         RESOLVING,
         RESOLVED
     }
     AppAnchorState appAnchorState=AppAnchorState.NONE;

     private synchronized void checkUpdateAnchor() {
         if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING) {
             return;
         }
         Anchor.CloudAnchorState cloudAnchorState = cloudAnchor.getCloudAnchorState();
         if (appAnchorState == AppAnchorState.HOSTING) {
             busy=true;
             if (cloudAnchorState.isError()) {
                 showErrorFlashbar(cloudAnchorState.toString());
                 appAnchorState=AppAnchorState.NONE;
                 RenderableItemType=AnchorType.NONE;
                 busy=false;
             }
             else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                 appAnchorState = AppAnchorState.HOSTED;
                 switch (RenderableItemType)
                 {
                     case MODEL:
                         placeModel(cloudAnchor);
                         break;
                     case TEXT:
                         floatingTextCreator.Build(cloudAnchor);
                         break;
                     case PICTURE:
                         floatingImageCreator.Build(cloudAnchor,image);
                         break;
                     case GRAPH:
                         floatingGraphCreator.build(ExcelData,cloudAnchor);
                         break;
                     case ANIMATED:
                         placeAnimatedModel(cloudAnchor);
                         break;
                     case AUDIO:
                         floatingAudioCreator.build(cloudAnchor,AudioUri);
                 }
                 arFragment.setOnTapArPlaneListener(addObjects);
                 RenderableItemType=AnchorType.NONE;
                 busy=false;
             }
         } else if (appAnchorState == AppAnchorState.RESOLVING) {
             busy=true;
             if (cloudAnchorState.isError()) {
                 showErrorFlashbar(cloudAnchorState.toString());
                 appAnchorState = AppAnchorState.NONE;
                 RenderableItemType=AnchorType.NONE;
                 busy=false;
             } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                 showLiveFlashbar("Resolved",false);
                 appAnchorState = AppAnchorState.RESOLVED;
                 switch (LiveItemType){
                     case TEXT:
                         floatingTextCreator.buildFromTextItem(textItem,cloudAnchor);
                         break;
                     case GRAPH:
                         floatingGraphCreator.buildFromGraohItem(graphItem,cloudAnchor);
                         break;
                     case PICTURE:
                         floatingImageCreator.buildFromImageItem(imageItem,cloudAnchor);
                         break;
                     case MODEL:
                         ResolvedAnchor=true;
                         if(!modelItem.isAnimated())
                             placeModel(cloudAnchor);
                         else
                             placeAnimatedModel(cloudAnchor);
                         break;
                     case AUDIO:
                         floatingAudioCreator.buildFromAudioItem(audioItem,cloudAnchor);
                         break;
                 }
                 RenderableItemType=AnchorType.NONE;
                 busy=false;
             }
         }
     }


     private void cloudanchor(Anchor newAnchor)
     {
         cloudAnchor=newAnchor;
         appAnchorState=AppAnchorState.NONE;
     }

     //Firebase Methods
     public void saveToFireBase(Anchor cloudAnchor,String asset, Boolean Animated){
         if(Hosting)
             new  Thread(()->{
                 firebaseManager.insertModel(new ModelItem(cloudAnchor.getCloudAnchorId(),cloudAnchor,asset,Animated));}).start();
     }
     public void saveToFireBase(String inputText, int layoutId, Anchor cloudAnchor) {
         if(Hosting)
             new Thread(()->{
                 firebaseManager.insertText(new TextItem(cloudAnchor.getCloudAnchorId(),cloudAnchor,inputText,layoutId));}).start();
     }
     public void saveToFireBase(HashMap<String,ArrayList<String>> chartData, ArrayList<String> header , int layoutId, Anchor cloudAnchor) {
         if(Hosting)
             new Thread(()->{
                 firebaseManager.insertGraph(new GraphItem(cloudAnchor,cloudAnchor.getCloudAnchorId(),layoutId,chartData,header));}).start();
     }
     public void saveToFireBase(byte[] image,Anchor cloudAnchor){
         if(Hosting)
             new Thread(()->{
                 firebaseManager.uploadImage(image,new ImageItem(cloudAnchor,cloudAnchor.getCloudAnchorId(),"",""));}).start();
     }
     public void saveToFireBase(Uri uri, Anchor cloudAnchor)
     {
         if (Hosting)
             new Thread(()->{
                firebaseManager.uploadAudio(uri, new AudioItem(cloudAnchor, cloudAnchor.getCloudAnchorId(), "", ""));
             }).start();

     }

     //Delete Live Nodes by their IDs

     public void deleteLiveNodeFromScreen(String anchorID)
     {
         try {
             placedNode.get(anchorID).getAnchor().detach();
             nodeBook.remove(placedNode.get(anchorID));
             placedNode.remove(anchorID);
         }
         catch (Exception e)
         {
             Log.v("LiveNode",e.toString());
         }

     }

     public void deleteLiveNodeFromFirebase(String anchorID,AnchorType type)
     {
         if(!anchorID.equals(""))
            new Thread(()-> firebaseManager.deleteAnchor(anchorID,type)).start();
     }

     public void deleteAudioById(String id)
     {
         floatingAudioCreator.deleteAudioById(id);
     }



     //flashbars

     public void showFlashBar(String message)
     {
         if(flashbar!=null){flashbar.dismiss();}
         flashbar=new Flashbar.Builder(this)
                 .title(message)
                 .titleColor(ContextCompat.getColor(this, R.color.white))
                 .message("Swipe to dismiss >>")
                 .messageColor(ContextCompat.getColor(this, R.color.white))
                 .gravity(Flashbar.Gravity.TOP)
                 .duration(5000)
                 .showIcon()
                 .icon(R.drawable.logo)
                 .enableSwipeToDismiss()
                 .castShadow(true, 4)
                 .backgroundDrawable(R.drawable.flashbar)
                 .build();
         handler.post(()->{flashbar.show();});
     }

     public void showLiveFlashbar(String message,Boolean progress)
     {
         if(flashbar!=null){flashbar.dismiss();}
         Flashbar.Builder build=new Flashbar.Builder(this)
                 .title(message)
                 .titleColor(ContextCompat.getColor(this, R.color.white))
                 .gravity(Flashbar.Gravity.TOP)
                 .castShadow(true, 4)
                 .backgroundDrawable(R.drawable.liveflash);
         if(progress)
             build.showProgress(Flashbar.ProgressPosition.LEFT);
         else
             build.duration(5000)
             .enableSwipeToDismiss()
             .message("Swipe to dismiss >>")
             .messageColor(ContextCompat.getColor(this, R.color.white))
             .showIcon()
             .icon(R.drawable.logo);

         flashbar=build.build();
         handler.post(()->{flashbar.show();});
     }

     public void showDownloadFlashbar(String message)
     {
         if(flashbar!=null){flashbar.dismiss();}
         flashbar=new Flashbar.Builder(this)
                 .title(message)
                 .titleColor(ContextCompat.getColor(this, R.color.white))
                 .gravity(Flashbar.Gravity.TOP)
                 .castShadow(true, 4)
                 .backgroundDrawable(R.drawable.downloadflash)
                 .showProgress(Flashbar.ProgressPosition.LEFT)
                 .message("Swipe to dismiss >>")
                 .enableSwipeToDismiss()
                 .messageColor(ContextCompat.getColor(this, R.color.white))
                 .primaryActionText("Cancel All")
                 .primaryActionTapListener(bar->{
                     bar.dismiss();
                     forfitPendingModelDownloads();
                     showFlashBar("Downloads Cancelled");
                 })
                 .build();
         handler.post(()->{flashbar.show();});

     }

     public void showErrorFlashbar(String message)
     {
         if(flashbar!=null){flashbar.dismiss();}
         flashbar=new Flashbar.Builder(this)
                 .title(message)
                 .titleColor(ContextCompat.getColor(this, R.color.white))
                 .message("Swipe to dismiss >>")
                 .duration(5000)
                 .messageColor(ContextCompat.getColor(this, R.color.white))
                 .gravity(Flashbar.Gravity.TOP)
                 .castShadow(true, 4)
                 .vibrateOn(Flashbar.Vibration.SHOW)
                 .enableSwipeToDismiss()
                 .showIcon()
                 .backgroundColorRes(R.color.colorAccent)
                 .build();
         handler.post(()->{flashbar.show();});

     }

     @Override
     protected void onPostResume() {
         super.onPostResume();
         if(firebaseManager.isRoomInitialized() && Hosting)
         {
             firebaseManager.setOnChangeListeners();
         }
     }

     private void registerNetworkCheck()
     {
         ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         NetworkRequest.Builder builder = new NetworkRequest.Builder();

         connectivityManager.registerNetworkCallback(
                 builder.build(),
                 new ConnectivityManager.NetworkCallback() {
                     @Override
                     public void onAvailable(Network network) {
                         NetworkAvailable=true;
                     }
                     @Override
                     public void onLost(Network network) {
                         NetworkAvailable=false;
                     }

                     @Override
                     public void onUnavailable() {
                         NetworkAvailable=false;
                     }
                 }

         );


     }

     @Override
     protected void onStart() {
         super.onStart();
         registerNetworkCheck();
     }

     private void toggleRotationLock()
     {
         if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
         {
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
         }
         else
         {
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
         }
     }

     private void forfitPendingModelDownloads()
     {
         modelRenderables.forEach(item->{
             item.first.cancel(true);

             Log.v("Forfit",item.first.isCancelled()+" "+item.first.isCancelled());
         });
         animatedAodelRenderables.forEach(item->{
             item.first.cancel(true);
             Log.v("Forfit",item.first.isCancelled()+" "+item.first.isCancelled());
         });
         modelRenderables.clear();
         animatedAodelRenderables.clear();
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         floatingAudioCreator.release();
         try {
             if(Hosting)
             {
                 deleteLiveSession();
             }
             deleteEveryNode();
         } catch (InterruptedException e) {
             Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
         }
         forfitPendingModelDownloads();
         Toast.makeText(this, "AR Studio has been closed", Toast.LENGTH_SHORT).show();
         overridePendingTransition(R.anim.fadeout,R.anim.fadein);
     }

     @Override
     protected void onStop() {
         super.onStop();
         floatingAudioCreator.release();
     }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == 1) {
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 showFlashBar("Storage permission granted, click the button again.");
             } else {
                 showErrorFlashbar("Storage access permission wasn't granted");
             }
         }
         if (requestCode == 2) {
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 showFlashBar("Microphone permission granted, click the button again.");
             } else {
                 showErrorFlashbar("Microphone access permission wasn't granted");
             }
         }
     }
 }
