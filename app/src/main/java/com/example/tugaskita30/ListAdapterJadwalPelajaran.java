package com.example.tugaskita30;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterJadwalPelajaran extends RecyclerView.Adapter<ListAdapterJadwalPelajaran.MyViewHolder> {
    private Context context;

    private List<JadwalPelajaran> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView background;

        public MyViewHolder(View view) {
            super(view);
            background = view.findViewById(R.id.background_jdwplj_layout_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterJadwalPelajaran(Context context, List<JadwalPelajaran> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterJadwalPelajaran.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_jadwal_pelajaran, parent, false);

        return new ListAdapterJadwalPelajaran.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterJadwalPelajaran.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        JadwalPelajaran recycler = recyclerList.get(position);
        holder.background.setImageResource(recycler.getImage());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
