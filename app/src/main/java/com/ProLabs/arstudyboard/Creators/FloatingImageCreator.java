package com.ProLabs.arstudyboard.Creators;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.ProLabs.arstudyboard.MainActivity;
import com.ProLabs.arstudyboard.R;
import com.ProLabs.arstudyboard.RenderableItems.ImageItem;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class FloatingImageCreator extends FragmentActivity {

    MainActivity mainActivity;
    private Anchor anchor;
    float rotation=90f;
    private volatile Bitmap image;
    String ID;

    public FloatingImageCreator(MainActivity mainActivity)
    {
        this.mainActivity=mainActivity;
    }



    public void Build(Anchor anchor,Bitmap image)
    {
        this.anchor=anchor;
        this.image=image;
        this.ID=anchor.getCloudAnchorId();

        ViewRenderable.builder()
                .setView(mainActivity, LayoutInflater.from(mainActivity).inflate(R.layout.floatingimage,null))
                .build()
                .thenAccept(viewRenderable -> {
                    addLayoutToScreen(anchor,viewRenderable);
                });
    }

    private Bitmap imageScaler(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                 maxImageSize / realImage.getWidth(),
                 maxImageSize / realImage.getHeight()
        );

        if (ratio >= 1.0){ return realImage;}

        int width = Math.round( ratio * realImage.getWidth());
        int height = Math.round( ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,height, filter);
        return newBitmap;
    }


    public byte[] getImageByte()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    String DownloadURL;
    public void buildFromImageItem(ImageItem imageItem,Anchor anchor)
    {
       DownloadURL=imageItem.getDownloadURL();
       this.ID=imageItem.getcloudAnchorID();
       this.anchor=anchor;
       createRenderable();
    }

    private void createRenderable()
    {
        ViewRenderable.builder()
                .setView(mainActivity,R.layout.floatingimage)
                .build()
                .thenAccept(viewRenderable -> {
                    addLayoutWithPicasso(anchor,viewRenderable);
                });
    }

    private void addLayoutWithPicasso(Anchor anchor, ViewRenderable viewRenderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        mainActivity.addNodeToMap(anchor.getCloudAnchorId(),anchorNode);
        TransformableNode transformableNode = new TransformableNode(mainActivity.arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(viewRenderable);
        mainActivity.arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();

        View view=viewRenderable.getView();
        ImageView imageView=view.findViewById(R.id.floatingImage);
        Toast.makeText(mainActivity,"Press and Hold on the image to rotate it clockwise",Toast.LENGTH_LONG).show();

        //Glide
        Picasso.get()
                .load(DownloadURL)
                .into(imageView);


        imageView.setOnLongClickListener(v -> {
            //Toast.makeText(mainActivity, "rotating", Toast.LENGTH_SHORT).show();
            imageView.setRotation(rotation);
            rotation=(rotation+90)%360;
            return true;
        });

        imageView.setOnClickListener(v -> {
            if(mainActivity.delete) {
                mainActivity.deleteNodeFromScreen(anchorNode,anchor.getCloudAnchorId(), MainActivity.AnchorType.PICTURE);
            }
        });

    }

    private void addLayoutToScreen(Anchor anchor, ViewRenderable viewRenderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        mainActivity.addNodeToMap(anchor.getCloudAnchorId(),anchorNode);
        TransformableNode transformableNode = new TransformableNode(mainActivity.arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(viewRenderable);
        mainActivity.arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();

        View view=viewRenderable.getView();
        ImageView imageView=view.findViewById(R.id.floatingImage);
        Toast.makeText(mainActivity,"Press and Hold on the image to rotate it clockwise",Toast.LENGTH_LONG).show();
        image=imageScaler(image,1500,true);
        imageView.setImageBitmap(image);
        imageView.setOnClickListener(v -> {
            if(mainActivity.delete) {
                mainActivity.deleteNodeFromScreen(anchorNode,anchor.getCloudAnchorId(), MainActivity.AnchorType.PICTURE);
            }
        });
        imageView.setOnLongClickListener(v -> {
            //Toast.makeText(mainActivity, "rotating", Toast.LENGTH_SHORT).show();
            imageView.setRotation(rotation);
            rotation=(rotation+90)%360;
            return true;
        });

        mainActivity.saveToFireBase(getImageByte(),anchor);
    }
}
