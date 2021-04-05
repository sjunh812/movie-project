package org.techtown.movieproject.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.techtown.movieproject.callback.FragmentCallback;
import org.techtown.movieproject.callback.GalleryCallback;

import java.net.URL;
import java.util.HashMap;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
    // 상수
    public static final String IMAGE = "movieImage";
    public static final String THUMB = "movieThumb";

    private FragmentCallback callback;
    private GalleryCallback callback2;

    private int id;     // 영화 id
    private String col;     // 이미지 정보 (ex) image, thumb..)
    private String urlStr;      // 이미지 url

    private ImageView imageView;
    private ProgressBar progressBar;

    private HashMap<String, Bitmap> bitmapHash = new HashMap<>();       // 메모리 관리를 위한 HashMap

    public ImageLoadTask(Context context, int id, String col, String urlStr, ImageView imageView, ProgressBar progressBar) {
        this.id = id;
        this.col = col;
        this.urlStr = urlStr;
        this.imageView = imageView;
        this.progressBar = progressBar;

        if(context instanceof FragmentCallback) {
            callback = (FragmentCallback)context;
        }
        if(context instanceof GalleryCallback) {
            callback2 = (GalleryCallback)context;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    // Thread 영역 (UI 접근 X)
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
        } catch(Exception e) {
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

        if(col.equals("image")) {
            callback.saveBitmapToJpeg(bitmap, IMAGE + id);
        } else if(col.equals("thumb")) {
            callback.saveBitmapToJpeg(bitmap, THUMB + id);
        }

        progressBar.setVisibility(View.INVISIBLE);
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();

        if(callback2 != null){
            callback2.init();
        }
    }
}
