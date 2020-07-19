package com.ProLabs.arstudyboard.RenderableItems;

import com.google.ar.core.Anchor;

public class ImageItem extends CloudAnchorItem{
    String DownloadURL;
    String FileURL;

    public ImageItem(Anchor anchor, String cloudAnchorID, String downloadURL,String fileURL) {
        super(anchor, cloudAnchorID);
        DownloadURL = downloadURL;
        this.FileURL=fileURL;
    }
    public ImageItem()
    {

    }

    public String getFileURL() {
        return FileURL;
    }

    public void setFileURL(String fileURL) {
        FileURL = fileURL;
    }

    public String getDownloadURL() {
        return DownloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        DownloadURL = downloadURL;
    }
}
