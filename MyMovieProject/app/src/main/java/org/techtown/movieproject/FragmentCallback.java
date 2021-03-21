package org.techtown.movieproject;

import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.MovieInfo;

import java.util.ArrayList;

public interface FragmentCallback {
    public void onMoiveDetailsFragment(int index);
    public void returnToWriteCommentActivity(MovieInfo movieInfo);
    public void returnToAllCommentsActivity(ArrayList<CommentInfo> items, MovieInfo movieInfo, int totalCount);
    public void changeToolbarTitle(int code);
    public void setDetailBool(Boolean bool);
}
