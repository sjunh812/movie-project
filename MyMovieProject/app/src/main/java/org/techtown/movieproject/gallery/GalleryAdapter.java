package org.techtown.movieproject.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.movieproject.helper.ImageLoadTask;
import org.techtown.movieproject.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    private ArrayList<GalleryInfo> items = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public GalleryAdapter(Context context) {
        this.context = context;
    }

    public static interface OnItemClickListener {
        public void onItemClick(ViewHolder holder, View view, int position);
    }

    public void addItem(GalleryInfo item) {
        items.add(item);
    }

    public ArrayList<GalleryInfo> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.gallery_layout, parent, false);

        return new ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryInfo item = items.get(position);
        holder.setImage(item.getId(), item.getThumbnailUrl(), item.isYoutube());
        holder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView isYoutube;
        private ProgressBar progressBar;

        private ImageLoadTask task;
        private Context context;
        private OnItemClickListener listener;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;

            image = (ImageView)itemView.findViewById(R.id.galleryImageView);
            isYoutube = (ImageView)itemView.findViewById(R.id.youtubeImageView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);

            isYoutube.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null) {
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setImage(int movieId, String imageUrl, boolean isYoutube) {
            task = new ImageLoadTask(context, movieId, "gallery", imageUrl, image, progressBar);
            task.execute();

            if(isYoutube) {
                this.isYoutube.setVisibility(View.VISIBLE);
            } else {
                this.isYoutube.setVisibility(View.INVISIBLE);
            }
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
