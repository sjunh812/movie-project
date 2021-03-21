package org.techtown.movieproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.net.URL;
import java.util.HashMap;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
    private String urlStr;
    private ImageView imageView;
    private ProgressBar progressBar;

    private HashMap<String, Bitmap> bitmapHash = new HashMap<>();

    public ImageLoadTask(String urlStr, ImageView imageView, ProgressBar progressBar) {
        this.urlStr = urlStr;
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    // Thread 영역
    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            if(bitmapHash.containsKey(urlStr)) {
                Bitmap oldBitmap = bitmapHash.remove(urlStr);
                oldBitmap.recycle();
            }

            URL url = new URL(urlStr);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            bitmapHash.put(urlStr, bitmap);

        }catch(Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        progressBar.setVisibility(View.INVISIBLE);
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }
}
