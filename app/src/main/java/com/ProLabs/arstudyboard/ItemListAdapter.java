package com.ProLabs.arstudyboard;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ProLabs.arstudyboard.Utility.ItemList;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder> {
    ArrayList<ItemList> itemLists = new ArrayList<>();
    MainActivity mainActivity;

    public ItemListAdapter(ArrayList<ItemList> itemLists, MainActivity context) {
        this.itemLists = itemLists;
        this.mainActivity= context;
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =LayoutInflater.from(viewGroup.getContext());
        View view=layoutInflater.inflate(R.layout.itemlist,viewGroup,false);
        return new ItemListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListViewHolder itemListViewHolder, int i) {
        itemListViewHolder.itemName.setText(itemLists.get(i).getName());
        itemListViewHolder.relativeLayout.setOnClickListener((view)->{
            mainActivity.Asset=mainActivity.BaseURL+itemLists.get(i).getName();
            mainActivity.itemRecyclerView.setVisibility(View.GONE);
            mainActivity.showButtons();
        });

    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        RelativeLayout relativeLayout;
        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.itemName);
            relativeLayout=itemView.findViewById(R.id.itemLayout);
        }
    }
}
