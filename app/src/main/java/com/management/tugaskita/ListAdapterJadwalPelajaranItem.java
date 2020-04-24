package com.management.tugaskita;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapterJadwalPelajaranItem extends RecyclerView.Adapter<ListAdapterJadwalPelajaranItem.MyViewHolder> {

    private List<JadwalPelajaranItem> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView matapelajaran_logo;
        public TextView matapelajaran;

        public MyViewHolder(View view) {
            super(view);
            matapelajaran_logo = view.findViewById(R.id.matapelajaran_logo_tgsact_image);
            matapelajaran = view.findViewById(R.id.matapelajaran_tgsact_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterJadwalPelajaranItem(Context context, List<JadwalPelajaranItem> recyclerList) {
        this.recyclerList = recyclerList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterJadwalPelajaranItem.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_jadwalpelajaran_item, parent, false);

        return new ListAdapterJadwalPelajaranItem.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterJadwalPelajaranItem.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        JadwalPelajaranItem recycler = recyclerList.get(position);
        holder.matapelajaran.setText(recycler.getAcademic());
        holder.matapelajaran_logo.setImageResource(recycler.getLogo());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
