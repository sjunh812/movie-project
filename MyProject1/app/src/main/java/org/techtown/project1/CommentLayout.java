package org.techtown.project1;

import android.content.Context;
import android.media.Rating;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CommentLayout extends LinearLayout {

    TextView userId;
    TextView comment;
    RatingBar ratingBar;

    public CommentLayout(Context context) {
        super(context);
        init(context);
    }

    public CommentLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate.inflate(R.layout.comment_layout, this, true);

        userId = (TextView)findViewById(R.id.commentId);
        comment = (TextView)findViewById(R.id.commentView);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
    }

    public void setUserId(String id) {
        userId.setText(id);
    }

    public void setComment(String comment) {
        this.comment.setText(comment);
    }
    /*
    public void setRatingBar() {

    }
     */
}
