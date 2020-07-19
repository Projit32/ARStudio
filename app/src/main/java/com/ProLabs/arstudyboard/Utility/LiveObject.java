package com.ProLabs.arstudyboard.Utility;


import com.ProLabs.arstudyboard.MainActivity;

public class LiveObject {

    MainActivity.AnchorType type;
    Object renderableObject;


    public LiveObject(MainActivity.AnchorType type, Object renderableObject) {
        this.type = type;
        this.renderableObject = renderableObject;
    }

    public MainActivity.AnchorType getType() {
        return type;
    }

    public void setType(MainActivity.AnchorType type) {
        this.type = type;
    }

    public Object getRenderableObject() {
        return renderableObject;
    }

    public void setRenderableObject(Object renderableObject) {
        this.renderableObject = renderableObject;
    }
}
