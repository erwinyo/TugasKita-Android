package com.management.tugaskita;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapterUndanganMasuk extends RecyclerView.Adapter<ListAdapterUndanganMasuk.MyViewHolder>{
    Global global = Global.getInstance();
    private Context context;

    private List<UndanganMasuk> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView udm_image1, udm_image2;
        public TextView udm_text;

        public MyViewHolder(View view) {
            super(view);
            udm_image1 = view.findViewById(R.id.udm_image1);
            udm_image2 = view.findViewById(R.id.udm_image2);
            udm_text = view.findViewById(R.id.udm_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterUndanganMasuk(Context context, List<UndanganMasuk> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterUndanganMasuk.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_undangan_masuk, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterUndanganMasuk.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        UndanganMasuk recycler = recyclerList.get(position);
        Picasso.get().load(recycler.getInviteFromImageURL())
                .placeholder(R.drawable.placeholder_image)
                .error(global.getDefaultProfilePicture(recycler.getInviteFromImageURLAlternative()))
                .into(holder.udm_image1);
        Picasso.get().load(recycler.getInviteToGrupImageURL())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.group)
                .into(holder.udm_image2);
        holder.udm_text.setText(recycler.getInviteFrom() + " Invited You");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
