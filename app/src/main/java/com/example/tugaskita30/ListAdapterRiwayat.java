package com.example.tugaskita30;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterRiwayat extends RecyclerView.Adapter<ListAdapterRiwayat.MyViewHolder> {
    private Context context;

    private List<Riwayat> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView rwyl_image;
        public TextView rwyl_header, rwyl_paragraph, rwyl_subheader;

        public MyViewHolder(View view) {
            super(view);
            rwyl_image = view.findViewById(R.id.tgsl_image);
            rwyl_paragraph = view.findViewById(R.id.tgsl_paragraph);
            rwyl_header = view.findViewById(R.id.tgsl_header);
            rwyl_subheader = view.findViewById(R.id.tgsl_subheader);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterRiwayat(Context context, List<Riwayat> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterRiwayat.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_tugas, parent, false);

        return new ListAdapterRiwayat.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterRiwayat.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Riwayat recycler = recyclerList.get(position);
        holder.rwyl_image.setImageResource(recycler.getImage());
        holder.rwyl_header.setText(recycler.getHeaderPreview());
        holder.rwyl_subheader.setText(recycler.getDeadlinePreview());
        holder.rwyl_paragraph.setText(recycler.getParagraphPreview());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
