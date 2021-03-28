package org.techtown.movieproject;

import android.graphics.Bitmap;

import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.MovieInfo;

import java.util.ArrayList;

public interface FragmentCallback {
    public void onMoiveDetailsFragment(int index, int networkStatus);
    public void returnToWriteCommentActivity(MovieInfo movieInfo);
    public void returnToAllCommentsActivity(ArrayList<CommentInfo> items, MovieInfo movieInfo, int totalCount);
    public void changeToolbarTitle(int code);
    public void setisDetail(Boolean bool);

    // 내부저장소 관련
    public Bitmap getBitmapFromCacheDir(String name);
    public void saveBitmapToJpeg(Bitmap bitmap, String name);
}
