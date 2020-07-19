package com.ProLabs.arstudyboard.RenderableItems;

import com.google.ar.core.Anchor;

public class ModelItem extends CloudAnchorItem{

    private String assetURL;
    private boolean animated;

    public ModelItem(String cloudAnchorID, Anchor anchor, String assetURL,Boolean animated) {
        super(anchor,cloudAnchorID);
        this.assetURL = assetURL;
        this.animated=animated;
    }
    public ModelItem()
    {

    }

    public String getAssetURL() {
        return assetURL;
    }

    public boolean isAnimated() {
        return animated;
    }
}
