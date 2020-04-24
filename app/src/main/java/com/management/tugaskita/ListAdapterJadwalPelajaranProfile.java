package com.management.tugaskita;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterJadwalPelajaranProfile extends RecyclerView.Adapter<ListAdapterJadwalPelajaranProfile.MyViewHolder> {

    private List<JadwalPelajaranProfile> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView thumbnail;
        public TextView header, content;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail_gprfact_layout_image);
            header = view.findViewById(R.id.header_gprfact_layout_text);
            content = view.findViewById(R.id.content_gprfact_layout_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterJadwalPelajaranProfile(Context context, List<JadwalPelajaranProfile> recyclerList) {
        this.recyclerList = recyclerList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterJadwalPelajaranProfile.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_jadwal_pelajaran_profile, parent, false);

        return new ListAdapterJadwalPelajaranProfile.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterJadwalPelajaranProfile.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        JadwalPelajaranProfile recycler = recyclerList.get(position);
        holder.header.setText(recycler.getDay());
        holder.content.setText(recycler.getData());
        holder.thumbnail.setImageResource(recycler.getImage());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
