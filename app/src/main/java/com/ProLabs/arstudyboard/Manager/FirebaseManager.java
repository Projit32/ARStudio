package com.ProLabs.arstudyboard.Manager;

import android.net.Uri;

import com.ProLabs.arstudyboard.MainActivity;
import com.ProLabs.arstudyboard.RenderableItems.GraphItem;
import com.ProLabs.arstudyboard.RenderableItems.ImageItem;
import com.ProLabs.arstudyboard.RenderableItems.ModelItem;
import com.ProLabs.arstudyboard.RenderableItems.TextItem;
import com.ProLabs.arstudyboard.Utility.LiveObject;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FirebaseManager {

    private MainActivity mainActivity;
    private FirebaseFirestore firestoreReference;
    private DocumentReference documentReference;
    private CollectionReference collectionReferenceImage,collectionReferenceText,collectionReferenceModel,collectionReferenceGraph,temp;
    private String roomNumber;
    private StorageReference storageReference=FirebaseStorage.getInstance().getReference();
    private Queue<LiveObject> liveObjects= new LinkedList<>();
    private ListenerRegistration ImageListener,TextListener,ModelListener,GraphListner;

    public FirebaseManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        firestoreReference=FirebaseFirestore.getInstance();
    }

    public void destoryReferences()
    {
        ModelListener.remove();
        ImageListener.remove();
        TextListener.remove();
        GraphListner.remove();
        firestoreReference.waitForPendingWrites();
        firestoreReference.terminate();
    }

    public boolean isRoomInitialized(){return roomNumber!=null;}

    public void initializeRoom(String room) throws InterruptedException {
        roomNumber="Room_"+room;

        documentReference= firestoreReference.document("ARS/"+roomNumber);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if(!documentSnapshot.exists())
            {
                Map<String,Object> roomMetaData= new HashMap<>();
                roomMetaData.put("Created On",new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date()));
                documentReference.set(roomMetaData)
                        .addOnSuccessListener(aVoid -> {
                            showLiveFlashbar("Room Created! Press the live button to start a live session.");
                        })

                        .addOnFailureListener(e -> {
                            showErrorFlashbar("Error in Room Creation");
                        });
            }
            else {showLiveFlashbar("Room joined! Press the live  button to start a live session.");}
        })
        .addOnFailureListener(documentReference->showErrorFlashbar("Error fetching the room."));

        //Initializing References
        collectionReferenceModel=documentReference.collection("Models");
        collectionReferenceText=documentReference.collection("Texts");
        collectionReferenceGraph=documentReference.collection("Graphs");
        collectionReferenceImage=documentReference.collection("Images");
        getLiveElementQueue();
    }

    public void insertModel(ModelItem modelItem)
    {
        collectionReferenceModel.add(modelItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.toString());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceModel.document(docId);
                    documentReference.update("documentID",docId);
                    modelItem.setdocumentID(task.getResult().getId());
                });
    }

    public void insertText(TextItem textItem)
    {
        collectionReferenceText.add(textItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.toString());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceText.document(docId);
                    documentReference.update("documentID",docId);
                    textItem.setdocumentID(docId);
                });
        //setOnChangeListeners();

    }

    public void insertGraph(GraphItem graphItem)
    {
        collectionReferenceGraph.add(graphItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.toString());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceGraph.document(docId);
                    documentReference.update("documentID",docId);
                    graphItem.setdocumentID(docId);
                });
        //setOnChangeListeners();
    }

    public void insertImage(ImageItem imageItem)
    {
        collectionReferenceImage.add(imageItem)
                .addOnSuccessListener(aVoid -> {
                    showLiveFlashbar("Anchor Hosted!");
                })
                .addOnFailureListener(e -> {
                    showErrorFlashbar(e.toString());
                })
                .addOnCompleteListener(task -> {
                    String docId=task.getResult().getId();
                    DocumentReference documentReference=collectionReferenceImage.document(docId);
                    documentReference.update("documentID",docId);
                    imageItem.setdocumentID(docId);
                });
        //setOnChangeListeners();
    }

    public void uploadImage(byte[] imagebytes, ImageItem imageItem)
    {
        String filename=Long.toHexString(System.currentTimeMillis()+(long)Math.random()*10000000)+".png";
        StorageReference childref=storageReference.child(roomNumber+"/"+filename);
        UploadTask uploadTask=childref.putBytes(imagebytes);
        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return childref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                imageItem.setDownloadURL(downloadUri.toString());
                imageItem.setFileURL(roomNumber+"/"+filename);
                insertImage(imageItem);

            } else {
                showErrorFlashbar("Error in image uploading");
            }
        });
    }

    private void showLiveFlashbar(String message)
    {
            mainActivity.showLiveFlashbar(message,false);
    }
    private void showErrorFlashbar(String message)
    {
            mainActivity.showErrorFlashbar(message);
    }


    public void getLiveElementQueue() throws InterruptedException {

        Thread image= new Thread(()->{
            collectionReferenceImage.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    ImageItem item=documentSnapshot.toObject(ImageItem.class);
                    liveObjects.add(new LiveObject(MainActivity.AnchorType.PICTURE,item));
                }
            });
        });image.start();
        Thread graph= new Thread(()->{
            collectionReferenceGraph.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    GraphItem item=documentSnapshot.toObject(GraphItem.class);
                    liveObjects.add(new LiveObject(MainActivity.AnchorType.GRAPH,item));
                }
            });
        });graph.start();
        Thread text= new Thread(()->{
            collectionReferenceText.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    TextItem item=documentSnapshot.toObject(TextItem.class);
                    liveObjects.add(new LiveObject(MainActivity.AnchorType.TEXT,item));
                }
            });

        });text.start();
        Thread model= new Thread(()->{
            collectionReferenceModel.get().addOnSuccessListener(queryDocumentSnapshots -> {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    ModelItem item=documentSnapshot.toObject(ModelItem.class);
                    liveObjects.add(new LiveObject(MainActivity.AnchorType.MODEL,item));
                }
            });

        });model.start();
        image.join();
        graph.join();
        text.join();
        model.join();
    }

    public Queue<LiveObject> getLiveObjects() {
        return liveObjects;
    }

    public void setOnChangeListeners()
    {
        ModelListener=collectionReferenceModel.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                ModelItem item=documentSnapshot.toObject(ModelItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.MODEL,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        TextListener=collectionReferenceText.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                TextItem item=documentSnapshot.toObject(TextItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.TEXT,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        ImageListener=collectionReferenceImage.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                ImageItem item=documentSnapshot.toObject(ImageItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.PICTURE,item);
                        if(!presentNode(item.getcloudAnchorID()) && dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
        GraphListner=collectionReferenceGraph.addSnapshotListener(mainActivity,(queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = dc.getDocument();
                GraphItem item=documentSnapshot.toObject(GraphItem.class);

                switch (dc.getType()) {
                    case ADDED:
                        LiveObject obj=new LiveObject(MainActivity.AnchorType.GRAPH,item);
                        if(!presentNode(item.getcloudAnchorID())&& dc.getOldIndex()==-1)
                            mainActivity.liveObjects.add(obj);
                        break;
                    case REMOVED:
                        if(presentNode(item.getcloudAnchorID()))
                            mainActivity.deleteLiveNodeFromScreen(item.getcloudAnchorID());
                        break;
                }
            }
        });
    }

    public void deleteAnchor(String anchorID, MainActivity.AnchorType type)
    {
        //delete with query
        switch (type)
        {
            case MODEL:
                temp=collectionReferenceModel;
                break;
            case PICTURE:
                temp=collectionReferenceImage;
                break;
            case TEXT:
                temp=collectionReferenceText;
                break;
            case GRAPH:
                temp=collectionReferenceGraph;
                break;
        }

        temp.whereEqualTo("cloudAnchorID",anchorID).get().addOnSuccessListener(queryDocumentSnapshots -> { for (DocumentSnapshot document : queryDocumentSnapshots) {
            if(type== MainActivity.AnchorType.PICTURE)
            {
                StorageReference childref=storageReference.child(document.toObject(ImageItem.class).getFileURL());
                childref.delete();
            }
            temp.document(document.getId()).delete();}});

        /**/
    }

    private boolean presentNode(String cloudAnchorID)
    {
        return mainActivity.placedNode.containsKey(cloudAnchorID);
    }

    private void deleteOldRoom()
    {

    }


}
