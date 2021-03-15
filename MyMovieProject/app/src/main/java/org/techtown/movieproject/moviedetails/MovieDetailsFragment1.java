package org.techtown.movieproject.moviedetails;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.techtown.movieproject.adapter.CommentAdapter;
import org.techtown.movieproject.CommentItem;
import org.techtown.movieproject.FragmentCallback;
import org.techtown.movieproject.R;

import java.util.ArrayList;

public class MovieDetailsFragment1 extends Fragment {
    private FragmentCallback callback;

    Button likeButton;
    Button dislikeButton;

    TextView likeCountView;
    TextView dislikeCountView;

    private boolean like = false;
    private boolean dislike = false;

    // 좋아요 싫어요 수 데이터
    public int likeCount = 15;
    public int dislikeCount = 1;

    CommentAdapter commentAdapter;
    ArrayList<CommentItem> items;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FragmentCallback) {
            callback = (FragmentCallback)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        callback.changeToolbarTitle(0);     // 영화 목록 프래그먼트로 복귀, 툴바 타이틀 변경

        if(callback != null) {
            callback = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_movie_details1, container, false);

        // 액티비티로부터 가져온 데이터(argument)
        if(getArguments() != null) {
            items = (ArrayList<CommentItem>)getArguments().getSerializable("commentData");
        }

        // 한줄평 리스트뷰 정의 및 어뎁터 생성
        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        commentAdapter = new CommentAdapter(getContext()) {
            @Override
            public int getCount() {
                if(commentAdapter.getItems().size() < 2) {
                    return commentAdapter.getItems().size();
                }

                return 2;
            }
        };
        commentAdapter.setItems(items);
        listView.setAdapter(commentAdapter);

        likeCountView = (TextView)rootView.findViewById(R.id.likeCountView);
        dislikeCountView = (TextView)rootView.findViewById(R.id.dislikeCountView);

        likeButton = (Button)rootView.findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dislike) {
                    descDislikeCount();
                    dislike = !dislike;
                }

                if(like) {
                    descLikeCount();
                }
                else {
                    incrLikeCount();
                }

                like = !like;
            }
        });

        dislikeButton = (Button)rootView.findViewById(R.id.dislikeButton);
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like) {
                    descLikeCount();
                    like = !like;
                }

                if(dislike) {
                    descDislikeCount();
                }
                else {
                    incrDislikeCount();
                }

                dislike = !dislike;
            }
        });

        Button writeButton = (Button)rootView.findViewById(R.id.writeButton);    // 작성하기 버튼
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.returnToWriteCommentActivity();
            }
        });

        Button allSeeButton = (Button)rootView.findViewById(R.id.allSeeButton);  // 모두보기 버튼
        allSeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.returnToAllCommentsActivity(items);
            }
        });

        return rootView;
    }

    // 프래그먼트 변경사항을 업데이트 (액티비티로부터 호출)
    public void update(CommentItem item) {
        commentAdapter.addItem(item);
        commentAdapter.notifyDataSetChanged();
    }

    private void incrLikeCount() {
        likeCount++;
        likeCountView.setText(String.valueOf(likeCount));
        likeButton.setBackgroundResource(R.drawable.ic_thumb_up_selected);
    }

    private void descLikeCount() {
        likeCount--;
        likeCountView.setText(String.valueOf(likeCount));
        likeButton.setBackgroundResource(R.drawable.thumb_up);
    }

    private void incrDislikeCount() {
        dislikeCount++;
        dislikeCountView.setText(String.valueOf(dislikeCount));
        dislikeButton.setBackgroundResource(R.drawable.ic_thumb_down_selected);
    }

    private void descDislikeCount() {
        dislikeCount--;
        dislikeCountView.setText(String.valueOf(dislikeCount));
        dislikeButton.setBackgroundResource(R.drawable.thumb_down);
    }
}
