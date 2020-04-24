package com.management.tugaskita;

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

public class ListAdapterAbsent extends RecyclerView.Adapter<ListAdapterAbsent.MyViewHolder> {
    Global global = Global.getInstance();
    private Context context;

    private List<Absent> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView absent_profile;
        public TextView absent_name;

        public MyViewHolder(View view) {
            super(view);
            absent_profile = view.findViewById(R.id.profile_absent);
            absent_name = view.findViewById(R.id.name_absent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterAbsent(Context context, List<Absent> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterAbsent.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_absent, parent, false);

        return new ListAdapterAbsent.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterAbsent.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Absent recycler = recyclerList.get(position);
        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(150).crop("fill")).secure(true).generate("tugaskita/profile/".concat(recycler.getImage()));
        Picasso.get().load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(global.getDefaultProfilePicture(recycler.image_alternative))
                .into(holder.absent_profile);
        holder.absent_name.setText(recycler.getNamaLengkap());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
