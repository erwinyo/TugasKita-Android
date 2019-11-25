package com.example.tugaskita30;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapterUndangTeman extends RecyclerView.Adapter<ListAdapterUndangTeman.MyViewHolder> {
    Global global = Global.getInstance();
    private Context context;

    private List<UndangTeman> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView undtmnl_profile;
        public TextView undtmnl_header, undtmnl_paragraph;

        public MyViewHolder(View view) {
            super(view);
            undtmnl_profile = view.findViewById(R.id.undtmnl_profile);
            undtmnl_header = view.findViewById(R.id.undtmnl_header);
            undtmnl_paragraph = view.findViewById(R.id.undtmnl_paragraph);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterUndangTeman(Context context, List<UndangTeman> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterUndangTeman.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_undang_teman, parent, false);

        return new ListAdapterUndangTeman.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterUndangTeman.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        UndangTeman recycler = recyclerList.get(position);
        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(100).crop("fill")).secure(true).generate("tugaskita/profile/".concat(recycler.getImage()));
        Picasso.with(context).load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(global.getDefaultProfilePicture(recycler.getImageAlternative()))
                .into(holder.undtmnl_profile);
        holder.undtmnl_header.setText(recycler.getHeader());
        holder.undtmnl_paragraph.setText(recycler.getParagraph());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
