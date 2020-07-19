package com.ProLabs.arstudyboard.RenderableItems;

import com.google.ar.core.Anchor;
import com.google.firebase.firestore.Exclude;

public abstract class CloudAnchorItem {

    public String cloudAnchorID;
    public String documentID;
    @Exclude
    public Anchor anchor;

    public CloudAnchorItem()
    {

    }

    public CloudAnchorItem( Anchor anchor,String cloudAnchorID) {
        this.cloudAnchorID = cloudAnchorID;
        this.anchor=anchor;
    }




    public String getcloudAnchorID() {
        return cloudAnchorID;
    }

    public String getdocumentID() {
        return documentID;
    }

    public void setdocumentID(String documentID) {
        this.documentID = documentID;
    }


    @Exclude
    public Anchor getAnchor() {
        return anchor;
    }

}
