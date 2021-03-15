package org.techtown.movieproject;

import java.util.ArrayList;

public interface FragmentCallback {
    public void onMoiveDetailsFragment(int index);
    public void returnToWriteCommentActivity();
    public void returnToAllCommentsActivity(ArrayList<CommentItem> items);
    public void changeToolbarTitle(int code);
}
