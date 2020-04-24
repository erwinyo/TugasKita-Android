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

public class ListAdapterGrup extends RecyclerView.Adapter<ListAdapterGrup.MyViewHolder> {

    private Context context;

    private List<Grup> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView gpl_image;
        public TextView gpl_header, gpl_paragraph, gpl_notification;

        public MyViewHolder(View view) {
            super(view);
            gpl_image = view.findViewById(R.id.gpl_image);
            gpl_header = view.findViewById(R.id.gpl_header);
            gpl_paragraph = view.findViewById(R.id.gpl_paragraph);
            gpl_notification = view.findViewById(R.id.gpl_notification);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterGrup(Context context, List<Grup> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterGrup.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_grup, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Grup recycler = recyclerList.get(position);
        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(200).crop("fill")).secure(true).generate("tugaskita/grup_profile/".concat(recycler.getImageUrl()));
        Picasso.get().load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.group)
                .into(holder.gpl_image);
        holder.gpl_header.setText(recycler.format(recycler.getNama(),10).concat(" (").concat(recycler.getJumlahAnggota()).concat(")"));
        holder.gpl_paragraph.setText(recycler.format(recycler.getDeksripsi(), 20));
        holder.gpl_notification.setText(recycler.getJumlahTugas());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}