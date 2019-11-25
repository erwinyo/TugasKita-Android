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

public class ListAdapterNews extends RecyclerView.Adapter<ListAdapterNews.MyViewHolder> {
    private Context context;

    private List<News> recyclerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView thumbnail;
        public TextView header, paragraph;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail_news_layout_image);
            header = view.findViewById(R.id.header_news_layout_text);
            paragraph = view.findViewById(R.id.paragraph_news_layout_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapterNews(Context context, List<News> recyclerList) {
        this.recyclerList = recyclerList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapterNews.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_news, parent, false);

        return new ListAdapterNews.MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ListAdapterNews.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        News recycler = recyclerList.get(position);
        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1").width(300).crop("fill")).secure(true).generate("tugaskita/news/".concat(recycler.getImageUrl().concat(".jpg")));
        Picasso.with(context).load(url)
                .placeholder(R.drawable.placeholder_mountain)
                .error(R.drawable.user)
                .into(holder.thumbnail);
        holder.header.setText(recycler.getHeader());
        holder.paragraph.setText(recycler.getParagraph());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
