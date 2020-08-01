package com.ProLabs.arstudyboard;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ProLabs.arstudyboard.Charts.BarChart;
import com.ProLabs.arstudyboard.Charts.ChartsUtil;
import com.ProLabs.arstudyboard.Charts.HorizontalBarChart;
import com.ProLabs.arstudyboard.Charts.LineChart;
import com.ProLabs.arstudyboard.Charts.PieChart;
import com.ProLabs.arstudyboard.Charts.RadarChart;
import com.ProLabs.arstudyboard.RenderableItems.GraphItem;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FloatingGraphCreator {

    private int layouts[]={R.layout.barchart,R.layout.linechart,R.layout.radarchart,R.layout.piechart,R.layout.horizontalbarchart};
    private String layoutNames[]={"Bar Chart","Line Chart","Radar Chart","Pie Chart","Horizontal Bar Chart"};
    private volatile int choice=0;
    AlertDialog.Builder alertDialog;
    View AlertLayout;
    private Spinner LayoutList;
    private EditText label,value;
    private CheckBox includesHeader;
    int l;
    ArrayList<Integer> v= new ArrayList<>();
    String ID="";
    boolean ResolvedAnchor=false;

    MainActivity mainActivity;
    private volatile ArrayList<ArrayList<String>> ExcelData= new ArrayList<>();
    private Anchor anchor;

    public FloatingGraphCreator(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }

    ArrayList<String> header= new ArrayList<>();
    HashMap<String, ArrayList<String>> chartData= new HashMap<>();

    public void build(ArrayList<ArrayList<String>> excelData, Anchor anchor)
    {
        clearExelDatas();
        ExcelData = excelData;
        this.anchor=anchor;
        this.ID=anchor.getCloudAnchorId();
        this.ResolvedAnchor=false;

        //Alert Dialog Box

        AlertLayout= LayoutInflater.from(mainActivity).inflate(R.layout.graphselectoralert,null);
        alertDialog= new AlertDialog.Builder(mainActivity).setView(AlertLayout);
        LayoutList=AlertLayout.findViewById(R.id.showGraphOptions);
        label=AlertLayout.findViewById(R.id.LabelInput);
        value=AlertLayout.findViewById(R.id.ValueInput);
        includesHeader=AlertLayout.findViewById(R.id.HeadersCheckBox);
        LayoutList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choice=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                choice=0;
            }
        });

        ArrayAdapter arrayAdapter= new ArrayAdapter(mainActivity, R.layout.layoutlistitems,layoutNames);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        LayoutList.setAdapter(arrayAdapter);


        alertDialog.setCancelable(false)
                .setPositiveButton("OK",(dialog, which) -> {
                    try {
                        l = Integer.parseInt(label.getText().toString()) - 1;
                        new ArrayList<String>(Arrays.asList(value.getText().toString().split(",")))
                                .forEach(element -> {
                                    v.add(Integer.parseInt(element));
                                });
                        for (int i = (includesHeader.isChecked()) ? 1 : 0; i < ExcelData.size(); i++) {
                            ArrayList<String> rowData = new ArrayList<>(ExcelData.get(i));
                            ArrayList<String> columnData = new ArrayList<>();
                            v.forEach(columnNumber -> {
                                columnData.add(rowData.get(columnNumber - 1));
                            });
                            chartData.put(rowData.get(l),columnData);
                        }
                        if (includesHeader.isChecked()) {
                            v.forEach(columnNumber -> {
                                header.add(ExcelData.get(0).get(columnNumber - 1));
                            });
                        } else {
                            for (int i = 0; i < v.size(); i++) {
                                header.add("Dataset " + (i + 1));
                            }
                        }
                        CreateRenderable();
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        mainActivity.showErrorFlashbar("One of the label or value number you have given doesn't exist");
                        build(this.ExcelData,this.anchor);
                    }
                    catch (NumberFormatException e)
                    {
                        mainActivity.showErrorFlashbar("Incorrect format for multiple value columns. Format : Single column --> just use number like 1 or 2, for multiple, use comma -->  1,2,3");
                        build(this.ExcelData,this.anchor);
                    }
                    catch (Exception e)
                    {
                        mainActivity.showErrorFlashbar("There was a problem with reading the data. Please check all the fields again. Problem :"+e.toString());
                        build(this.ExcelData,this.anchor);
                    }
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    mainActivity.arFragment.setOnTapArPlaneListener(mainActivity.addObjects);
                    dialog.cancel();
                });

        //showing
        alertDialog.create().show();
    }
    public void CreateRenderable()
    {
        ViewRenderable.builder()
                .setView(mainActivity,layouts[choice])
                .build()
                .thenAccept(viewRenderable -> {
                    addLayoutToScreen(anchor,viewRenderable);
                });
    }

    public int getLayoutId()
    {
        return choice;
    }

    public ArrayList<String> getHeader() {
        return header;
    }

    public HashMap<String, ArrayList<String>> getChartData() {
        return chartData;
    }

    private void addLayoutToScreen(Anchor anchor, ViewRenderable viewRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        mainActivity.addNodeToMap(ID,anchorNode);
        TransformableNode transformableNode = new TransformableNode(mainActivity.arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(viewRenderable);
        mainActivity.arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
        View view=viewRenderable.getView();
        view.setOnClickListener(v1 -> {
            if(mainActivity.delete) {
                //anchorNode.removeChild(nodeToRemove);
                mainActivity.deleteNodeFromScreen(anchorNode,anchor.getCloudAnchorId(), MainActivity.AnchorType.GRAPH);
                //nodeToRemove.setParent(null);//works both ways
            }
        });

        ChartsUtil mychart;
        switch (choice){
            case 0:
                mychart=new BarChart(view);
                break;
            case 1:
                mychart=new LineChart(view);
                break;
            case 2:
                mychart=new RadarChart(view);
                break;
            case 3:
                mychart=new PieChart(view);
                break;
            case 4:
                mychart=new HorizontalBarChart(view);
                break;
            default:
                mychart=new LineChart(view);
        }

        try {
            mychart.generateChart(chartData, header);
            Toast.makeText(mainActivity,"Double tap on the chart to save the chart in Gallery",Toast.LENGTH_LONG).show();
            if(!ResolvedAnchor)
                mainActivity.saveToFireBase(getChartData(),getHeader(),getLayoutId(),anchor);
        }
        catch (NumberFormatException e)
        {
            mainActivity.showErrorFlashbar("The first row contains header names, you need to check that option during column selection.");
            build(this.ExcelData,this.anchor);
        }
        catch (Exception e)
        {
            mainActivity.showErrorFlashbar("Problem With graph "+ e.getMessage());
        }
    }


    public void buildFromGraohItem(GraphItem graphItem,Anchor anchor)
    {
        clearExelDatas();
        this.anchor=anchor;
        this.ID=graphItem.getcloudAnchorID();
        this.choice=graphItem.getLayoutID();
        this.chartData=graphItem.getChartData();
        this.header=graphItem.getHeaders();
        this.ResolvedAnchor=true;
        CreateRenderable();

    }


    public void clearExelDatas()
    {
        header.clear();
        v.clear();
        chartData.clear();
    }
}
