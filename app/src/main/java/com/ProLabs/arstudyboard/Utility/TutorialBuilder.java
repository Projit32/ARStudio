package com.ProLabs.arstudyboard.Utility;
import android.util.Pair;
import android.view.View;

import com.ProLabs.arstudyboard.MainActivity;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class TutorialBuilder {

    private MainActivity context;
    private ArrayList<Pair<View,String>>tutorialArray=new ArrayList<>();

    public TutorialBuilder(MainActivity context) {
        this.context = context;
    }

    public TutorialBuilder with(ArrayList<Pair<View,String>>tutorialArray)
    {
        this.tutorialArray=tutorialArray;
        return TutorialBuilder.this;
    }

    public void show(){
        buildShowCase("");
    }

    public void buildShowCase(String id){
        if(id.equals(""))
        {
            id=Double.toString(1+Math.random()*100);
        }
        ShowcaseConfig showcaseConfig= new ShowcaseConfig();
        showcaseConfig.setRenderOverNavigationBar(true);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(context,id);
        sequence.setConfig(showcaseConfig);

        tutorialArray.forEach(item->{
            sequence.addSequenceItem(item.first,item.second,"GOT IT");
        });
        sequence.start();
    }



}
