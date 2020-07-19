package com.ProLabs.arstudyboard.RenderableItems;

import com.google.ar.core.Anchor;

public class TextItem extends CloudAnchorItem {
    public String content;
    int layoutId;

    public TextItem(String cloudAnchorID, Anchor anchor, String content, int layoutId) {
        super(anchor, cloudAnchorID);
        this.content = content;
        this.layoutId = layoutId;
    }
    public TextItem()
    {

    }


    public String getContent() {
        return content;
    }

    public int getLayoutId() {
        return layoutId;
    }

}
