package com.project.ovi.liceenta;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.ovi.liceenta.model.DriveItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by Ovi
 */
public class DriveItemsViewAdapter extends RecyclerView.Adapter<DriveItemsViewAdapter.View_Holder> {

    List<DriveItem> list = Collections.emptyList();

    public DriveItemsViewAdapter() {

    }

    public void updatePersons(List<DriveItem> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (list.get(position).isFolder()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Inflate the layout, initialize the View Holder
        View_Holder holder;
        switch (viewType) {
            case 0:
                CardView fileView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.file_row_layout, parent, false);
                holder = new View_Holder(fileView);
                break;
            case 1:
                CardView folderView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_row_layout, parent, false);
                holder = new View_Holder(folderView);
                break;
            default:
                holder = new View_Holder(new CardView(parent.getContext()));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.bindObject(list.get(position));
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class View_Holder_File extends RecyclerView.ViewHolder {
        View_Holder_File(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_row_layout, parent, false));
        }
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        public CardView cardView;

        View_Holder(View rowLayout) {
            super(rowLayout);
            this.cardView = (CardView) rowLayout;
        }

        public void bindObject(DriveItem contentObject){
            TextView folderTextView = (TextView) cardView.getChildAt(1);
            folderTextView.setText(contentObject.getName());
        }
    }


}