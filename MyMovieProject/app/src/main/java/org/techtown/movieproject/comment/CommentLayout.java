package org.techtown.movieproject.comment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.techtown.movieproject.FragmentCallback;
import org.techtown.movieproject.R;

public class CommentLayout extends LinearLayout {
    // UI
    private TextView userId;
    private TextView time;
    private TextView comment;
    private RatingBar ratingBar;
    private TextView recommend;

    // Interface
    private FragmentCallback callback;

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

        if(context instanceof FragmentCallback) {
            callback = (FragmentCallback)context;
        }

        userId = (TextView)findViewById(R.id.commentId);
        time = (TextView)findViewById(R.id.commentTime);
        comment = (TextView)findViewById(R.id.commentView);
        ratingBar = (RatingBar)findViewById(R.id.commentRatingBar);
        recommend = (TextView)findViewById(R.id.recommendCountView);

/*        Button recommendButton = (Button)findViewById(R.id.recommendButton);    // 추천버튼
        recommendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.increaseRecommend(userId.getText().toString());
            }
        });*/

        Button reportButton = (Button)findViewById(R.id.reportButton);      // 신고버튼
        reportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "신고", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUserId(String id) {
        userId.setText(id);
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public void setComment(String comment) {
        this.comment.setText(comment);
    }

    public void setRatingBar(float rating) {
        ratingBar.setRating(rating);
    }

    public void setRecommend(String recommend) {
        this.recommend.setText(recommend);
    }
}