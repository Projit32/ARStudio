package com.ProLabs.arstudyboard;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ProLabs.arstudyboard.RenderableItems.TextItem;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

public class FloatingTextCreator {
    MainActivity mainActivity;
    String layoutsNames[]={"Milestone","Pink Ribbon","Blue Ribbon", "Black Glass","AR Studio Theme"};
    int layouts[]={R.layout.milestone,R.layout.pink,R.layout.blue,R.layout.blackglass,R.layout.arstheme};
    AlertDialog.Builder alertDialog;
    View AlertLayout;
    Spinner LayoutList;
    EditText text;
    String content;
    volatile int choice=0;
    Anchor anchor;
    String ID="";
    boolean ResolvedAnchor=false;

    public FloatingTextCreator(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void Build(Anchor anchor)
    {
        this.anchor=anchor;
        this.ID=anchor.getCloudAnchorId();
        this.ResolvedAnchor=false;
        AlertLayout= LayoutInflater.from(mainActivity).inflate(R.layout.alert,null);
        alertDialog= new AlertDialog.Builder(mainActivity).setView(AlertLayout);
        LayoutList=AlertLayout.findViewById(R.id.showLayout);
        text=AlertLayout.findViewById(R.id.inputFloatingText);
        LayoutList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choice=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                choice=0;
            }
        });
        ArrayAdapter arrayAdapter= new ArrayAdapter(mainActivity, R.layout.layoutlistitems,layoutsNames);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        LayoutList.setAdapter(arrayAdapter);

        alertDialog.setCancelable(false)
                .setPositiveButton("OK",(dialog, which) -> {
                    content=text.getText().toString();
                    CreateRenderable();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    mainActivity.arFragment.setOnTapArPlaneListener(mainActivity.addObjects);
                    dialog.cancel();
                });

        //showing
        AlertDialog alert =alertDialog.create();
        alert.show();

    }

    public String getInputText()
    {
        return text.getText().toString();
    }

    public int getLayoutId()
    {
        return choice;
    }

    public void buildFromTextItem(TextItem textItem,Anchor anchor)
    {
        this.anchor=anchor;
        this.ID=textItem.getcloudAnchorID();
        choice=textItem.getLayoutId();
        content=textItem.getContent();
        this.ResolvedAnchor=true;
        CreateRenderable();

    }

    public void CreateRenderable()
    {
        ViewRenderable.builder()
                .setView(mainActivity,layouts[choice])
                .build()
                .thenAccept(viewRenderable -> {
                    addLayoutToScreen(anchor,viewRenderable);
                });
    }

    private void addLayoutToScreen(Anchor anchor, ViewRenderable viewRenderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        mainActivity.addNodeToMap(ID,anchorNode);
        TransformableNode transformableNode = new TransformableNode(mainActivity.arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(viewRenderable);
        mainActivity.arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
        transformableNode.setOnTapListener((HitTestResult hitTestResult, MotionEvent Event) ->
        {
            if(mainActivity.delete) {
                mainActivity.deleteNodeFromScreen(anchorNode,anchor.getCloudAnchorId(), MainActivity.AnchorType.TEXT);
            }

        });
        View view=viewRenderable.getView();
        TextView floatingText=view.findViewById(R.id.FloatingInnerText);
        floatingText.setText(content);
        if(!ResolvedAnchor)
            mainActivity.saveToFireBase(getInputText(),getLayoutId(),anchor);
    }



}
