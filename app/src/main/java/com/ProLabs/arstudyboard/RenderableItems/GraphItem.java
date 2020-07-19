package com.ProLabs.arstudyboard.RenderableItems;

import com.google.ar.core.Anchor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphItem extends CloudAnchorItem{
    int layoutID;

    HashMap<String,ArrayList<String>> chartData;
    ArrayList<String> headers;

    public GraphItem(Anchor anchor, String cloudAnchorID, int layoutID, HashMap<String,ArrayList<String>> chartData, ArrayList<String> headers) {
        super(anchor, cloudAnchorID);
        this.layoutID = layoutID;
        this.chartData=chartData;
        this.headers=headers;
    }
    public GraphItem()
    {

    }

    public HashMap<String, ArrayList<String>> getChartData() {
        return chartData;
    }

    public int getLayoutID() {
        return layoutID;
    }

    public ArrayList<String> getHeaders() {
        return headers;
    }
}
