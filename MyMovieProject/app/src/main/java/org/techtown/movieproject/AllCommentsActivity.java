package org.techtown.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.CommentList;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.api.ResponseInfo;
import org.techtown.movieproject.comment.CommentAdapter;

import java.util.ArrayList;

public class AllCommentsActivity extends AppCompatActivity implements ActivityCallback{
    // 상수
    private static final String LOG = "AllCommentsActivity";

    // UI
    private TextView title;
    private ImageView grade;
    private RatingBar ratingBar;
    private TextView rateAndCommentCount;
    private ListView listView;

    private CommentAdapter adapter;

    // 데이터
    private MovieInfo movieInfo;
    private ArrayList<CommentInfo> items;
    private int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);

        title = (TextView)findViewById(R.id.title);
        grade = (ImageView)findViewById(R.id.grade);
        ratingBar = (RatingBar)findViewById(R.id.allRatingBar);
        rateAndCommentCount = (TextView)findViewById(R.id.allCountsView);
        listView = (ListView)findViewById(R.id.listView);

        // 네트워크 연결상태 점검
        int networkStatus = NetworkStatus.getConnectivity(this);

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            Snackbar.make(title, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
        }

        Button writeButton = (Button)findViewById(R.id.writeButton);    // 작성하기 버튼
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToWriteCommentActivity(movieInfo);
            }
        });

        Intent intent = getIntent();
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        items = (ArrayList<CommentInfo>)intent.getSerializableExtra("listViewData");
        movieInfo = (MovieInfo)intent.getSerializableExtra("movieInfo");
        totalCount = intent.getIntExtra("totalCount", -1);

        title.setText(movieInfo.title);
        setGradeImage(movieInfo.grade);
        ratingBar.setRating(movieInfo.audience_rating / 2);
        rateAndCommentCount.setText(movieInfo.audience_rating + " (" + totalCount + "명 참여)");

        adapter = new CommentAdapter(this);
        adapter.setItems(items);
        listView.setAdapter(adapter);
    }

    // 작성하기 액티비티 전환
    private void returnToWriteCommentActivity(MovieInfo movieInfo) {
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);
        intent.putExtra("movieInfo", movieInfo);

        startActivity(intent);
    }

    @Override
    public void increaseRecommend(int reviewId, int movieId) {
        int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            Snackbar.make(title, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
        } else {
            String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/increaseRecommend" + "?";
            url += "review_id=" + reviewId;

            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            synchronization(movieId, 100);      // 한줄평 동기화
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(LOG, "응답 에러 : " + error.getMessage());
                        }
                    }
            );

            request.setShouldCache(false);
            AppHelper.requestQueue.add(request);
        }
    }

    public void synchronization(int id, int limit) {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readCommentList" + "?";
        url += "id=" + id + "&limit=" + limit;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

                        if(info.code == 200) {      // 정상 응답
                            CommentList commentList = gson.fromJson(response, CommentList.class);
                            items = commentList.result;
                            adapter.setItems(items);
                            adapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG, "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    // 관람등급 이미지설정
    public void setGradeImage(int grade) {
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