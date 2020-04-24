package com.management.tugaskita;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterTugas extends RecyclerView.Adapter<ListAdapterTugas.MyViewHolder> {

    private List<Tugas> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView tgsl_image;
        public TextView tgsl_header, tgsl_paragraph, tgsl_subheader, tgsl_subsubheader;

        public MyViewHolder(View view) {
            super(view);
            tgsl_image = view.findViewById(R.id.tgsl_image);
            tgsl_paragraph = view.findViewById(R.id.tgsl_paragraph);
            tgsl_header = view.findViewById(R.id.tgsl_header);
            tgsl_subheader = view.findViewById(R.id.tgsl_subheader);
            tgsl_subsubheader = view.findViewById(R.id.tgsl_subsubheader);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterTugas(Context context, List<Tugas> recyclerList) {
        this.recyclerList = recyclerList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterTugas.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_tugas, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Tugas recycler = recyclerList.get(position);
        holder.tgsl_image.setImageResource(recycler.getImage());
        holder.tgsl_header.setText(recycler.getHeaderPreview());
        holder.tgsl_subheader.setText(recycler.getDeadlinePreview());
        holder.tgsl_subsubheader.setText(recycler.getPrioritas().toUpperCase());
        holder.tgsl_paragraph.setText(recycler.getParagraphPreview());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
