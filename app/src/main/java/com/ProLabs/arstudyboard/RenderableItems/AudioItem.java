package com.ProLabs.arstudyboard.RenderableItems;

import com.google.ar.core.Anchor;

public class AudioItem extends CloudAnchorItem{
    String DownloadURL;
    String FileURL;

    public AudioItem(Anchor anchor, String cloudAnchorID, String downloadURL, String fileURL) {
        super(anchor, cloudAnchorID);
        DownloadURL = downloadURL;
        this.FileURL=fileURL;
    }
    public AudioItem()
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
