package org.techtown.movieproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.techtown.movieproject.AppHelper;
import org.techtown.movieproject.ImageLoadTask;
import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.CommentList;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.comment.CommentAdapter;
import org.techtown.movieproject.FragmentCallback;
import org.techtown.movieproject.R;
import org.w3c.dom.Comment;

import java.util.ArrayList;

public class MovieDetailsFragment extends Fragment{
    // UI
    private Button likeButton;
    private Button dislikeButton;
    private TextView likeCountView;
    private TextView dislikeCountView;
    private ImageView image;
    private ProgressBar progressBar;
    private TextView title;
    private ImageView grade;
    private TextView date;
    private TextView genreAndDuration;
    private TextView reserGrade;
    private TextView reserRate;
    private RatingBar ratingBar;
    private TextView rate;
    private TextView audienceCount;
    private TextView synopsis;
    private TextView director;
    private TextView actor;

    //
    private ImageLoadTask task;     // 이미지로딩에 필요한 AsyncTask 를 상속한 커스텀클래스
    private FragmentCallback callback;
    private CommentAdapter commentAdapter;

    // Data
    public int likeCount;
    public int dislikeCount;
    private boolean like = false;
    private boolean dislike = false;
    private MovieInfo movieInfo;
    private CommentList commentList;
    private int totalCount;     // 한줄평 총 참여자 수
    private SharedPreferences prefer;
    private SharedPreferences.Editor editor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FragmentCallback) {
            callback = (FragmentCallback)context;
        }

        callback.setDetailBool(true);       // 상세보기 프래그먼트가 제일상단에 위치
        Toast.makeText(context, "onAttach() - 상세보기 프래그먼트", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        callback.changeToolbarTitle(0);     // 영화 목록 프래그먼트로 복귀, 툴바 타이틀 변경
        callback.setDetailBool(false);      // 상세보기 프래그먼트 종료 (목록 프래그먼트 위치)

        if(callback != null) {
            callback = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_movie_details, container, false);

        // 액티비티로부터 가져온 데이터(argument)
        if(getArguments() != null) {
            movieInfo = (MovieInfo)getArguments().getSerializable("movieInfo");
            commentList = (CommentList)getArguments().getSerializable("commentList");
            totalCount = getArguments().getInt("totalCount");
        }

        // 각 영화에서의 좋아요 싫어요 기록
        prefer = getContext().getSharedPreferences(movieInfo.title, Activity.MODE_PRIVATE);
        editor = prefer.edit();

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        image = (ImageView)rootView.findViewById(R.id.movieImage);
        task = new ImageLoadTask(movieInfo.thumb, image, progressBar);
        task.execute();     // 이미지로드

        title = (TextView)rootView.findViewById(R.id.movieTitle);
        title.setText(movieInfo.title);

        grade = (ImageView) rootView.findViewById(R.id.movieGrade);
        setGradeImage(movieInfo.grade);

        date = (TextView)rootView.findViewById(R.id.movieDate);
        date.setText(movieInfo.date.replace("-",".") + " 개봉");      // yyyy.MM.dd

        genreAndDuration = (TextView)rootView.findViewById(R.id.movieGenreDuration);
        genreAndDuration.setText(movieInfo.genre + " / " + movieInfo.duration + "분");       // 장르 / ~분

        reserGrade = (TextView)rootView.findViewById(R.id.reservationGrade);
        reserGrade.setText(movieInfo.reservation_grade + "위");

        reserRate = (TextView)rootView.findViewById(R.id.reservationRate);
        reserRate.setText(movieInfo.reservation_rate + "%");

        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        ratingBar.setRating(movieInfo.user_rating);

        rate = (TextView)rootView.findViewById(R.id.audienceRate);
        rate.setText(String.valueOf(movieInfo.audience_rating));

        audienceCount = (TextView)rootView.findViewById(R.id.auduenceCount);
        audienceCount.setText(movieInfo.audience + "명");

        synopsis = (TextView)rootView.findViewById(R.id.synopsis);      // 줄거리
        synopsis.setText(movieInfo.synopsis);

        director = (TextView)rootView.findViewById(R.id.director);
        director.setText(movieInfo.director);

        actor = (TextView)rootView.findViewById(R.id.actor);
        actor.setText(movieInfo.actor);

        // 한줄평 리스트뷰 정의 및 어뎁터 생성
        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        commentAdapter = new CommentAdapter(getContext()) {
            @Override
            public int getCount() {
                if(this.getItems().size() < 2) {
                    return this.getItems().size();
                }

                return 2;       // 리스트뷰는 최대 2개의 항목만 표시
            }
        };
        commentAdapter.setItems(commentList.result);
        listView.setAdapter(commentAdapter);

        likeCountView = (TextView)rootView.findViewById(R.id.likeCountView);
        likeCountView.setText(String.valueOf(movieInfo.like));
        likeCount = Integer.parseInt(likeCountView.getText().toString());

        dislikeCountView = (TextView)rootView.findViewById(R.id.dislikeCountView);
        dislikeCountView.setText(String.valueOf(movieInfo.dislike));
        dislikeCount = Integer.parseInt(dislikeCountView.getText().toString());

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

        // 좋아요 싫어요 기록 적용
        if(prefer != null) {
            if(prefer.getBoolean("like", false)) {
                likeButton.setBackgroundResource(R.drawable.ic_thumb_up_selected);      // 좋아요 버튼이 선택된 이미지
                like = true;
            }
            else if(prefer.getBoolean("dislike", false)) {
                dislikeButton.setBackgroundResource(R.drawable.ic_thumb_down_selected);     // 싫어요 버튼이 선택된 이미지
                dislike = true;
            }
        }

        Button writeButton = (Button)rootView.findViewById(R.id.writeButton);       // 작성하기 버튼
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.returnToWriteCommentActivity(movieInfo);
            }
        });

        Button allSeeButton = (Button)rootView.findViewById(R.id.allSeeButton);     // 모두보기 버튼
        allSeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.returnToAllCommentsActivity(commentList.result, movieInfo, totalCount);
            }
        });

        return rootView;
    }

    // 프래그먼트 변경사항을 업데이트 (액티비티로부터 호출)
    public void update(CommentList commentList, int totalCount) {
        this.commentList = commentList;
        this.totalCount = totalCount;
        commentAdapter.setItems(commentList.result);
        commentAdapter.notifyDataSetChanged();
    }

    private void incrLikeCount() {
        likeCount++;
        likeCountView.setText(String.valueOf(likeCount));
        likeButton.setBackgroundResource(R.drawable.ic_thumb_up_selected);

        increaseLikeDisLike(movieInfo.id, 0, "Y");
        editor.putBoolean("like", true);
        editor.commit();
    }

    private void descLikeCount() {
        likeCount--;
        likeCountView.setText(String.valueOf(likeCount));
        likeButton.setBackgroundResource(R.drawable.thumb_up);

        increaseLikeDisLike(movieInfo.id, 0, "N");
        editor.putBoolean("like", false);
        editor.commit();
    }

    private void incrDislikeCount() {
        dislikeCount++;
        dislikeCountView.setText(String.valueOf(dislikeCount));
        dislikeButton.setBackgroundResource(R.drawable.ic_thumb_down_selected);

        increaseLikeDisLike(movieInfo.id, 1, "Y");
        editor.putBoolean("dislike", true);
        editor.commit();
    }

    private void descDislikeCount() {
        dislikeCount--;
        dislikeCountView.setText(String.valueOf(dislikeCount));
        dislikeButton.setBackgroundResource(R.drawable.thumb_down);

        increaseLikeDisLike(movieInfo.id, 1, "N");
        editor.putBoolean("dislike", false);
        editor.commit();
    }

    // likeCode=0: 좋아요, likCode=1: 싫어요
    private void increaseLikeDisLike(int id, int likeCode, String yesNo) {
        String likeDislike = null;
        if(likeCode == 0) {
            likeDislike = "likeyn=";
        }
        else if(likeCode == 1) {
            likeDislike = "dislikeyn=";
        }

        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/increaseLikeDisLike" + "?";
        url += "id=" + id + "&" + likeDislike + yesNo;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SUCCESS", "서버로 좋아요 싫어요 요청성공!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "에러발생 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    private void setGradeImage(int grade) {
        switch (grade) {
            case 0:
                this.grade.setImageResource(R.drawable.ic_all);
                break;
            case 12:
                this.grade.setImageResource(R.drawable.ic_12);
                break;
            case 15:
                this.grade.setImageResource(R.drawable.ic_15);
                break;
            case 19:
                this.grade.setImageResource(R.drawable.ic_19);
                break;
        }
    }
}
