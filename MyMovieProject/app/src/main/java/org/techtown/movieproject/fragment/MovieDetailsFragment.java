package org.techtown.movieproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.techtown.movieproject.helper.AppHelper;
import org.techtown.movieproject.helper.ImageLoadTask;
import org.techtown.movieproject.helper.NetworkStatus;
import org.techtown.movieproject.PhotoActivity;
import org.techtown.movieproject.api.CommentList;
import org.techtown.movieproject.gallery.GalleryInfo;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.comment.CommentAdapter;
import org.techtown.movieproject.callback.FragmentCallback;
import org.techtown.movieproject.R;
import org.techtown.movieproject.gallery.GalleryAdapter;

import java.util.ArrayList;

public class MovieDetailsFragment extends Fragment{
    // 상수
    private static final String LOG = "movieDetailsFragment";

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
    private RecyclerView recyclerView;

    private ImageLoadTask task;     // 이미지로딩에 필요한 AsyncTask 를 상속한 커스텀클래스
    private FragmentCallback callback;
    private CommentAdapter commentAdapter;
    private GalleryAdapter galleryAdapter;

    // Data
    public int likeCount;
    public int dislikeCount;
    private boolean like = false;
    private boolean dislike = false;
    private MovieInfo movieInfo;
    private CommentList commentList;
    private int totalCount;     // 한줄평 총 참여자 수
    private SharedPreferences prefer;       // 앱 사용자가 누른 좋아요 or 싫어요 기록
    private SharedPreferences.Editor editor;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FragmentCallback) {
            callback = (FragmentCallback)context;
        }

        callback.setisDetail(true);       // 상세보기 프래그먼트가 제일상단에 위치
        //Toast.makeText(context, "onAttach() - 상세보기 프래그먼트", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        callback.changeToolbarTitle(0);     // 영화 목록 프래그먼트로 복귀, 툴바 타이틀 변경
        callback.setisDetail(false);      // 상세보기 프래그먼트 종료 (목록 프래그먼트 위치)

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

        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.linearLayout);
        ProgressBar mainProgressBar = (ProgressBar)rootView.findViewById(R.id.mainProgressBar);
        mainProgressBar.setVisibility(View.INVISIBLE);

        if(movieInfo == null) {
            mainProgressBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
        } else {
            // 각 영화에서의 좋아요 싫어요 누른 기록
            prefer = getContext().getSharedPreferences(movieInfo.title, Activity.MODE_PRIVATE);
            editor = prefer.edit();

            progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            image = (ImageView)rootView.findViewById(R.id.movieImage);

            int networkStatus = NetworkStatus.getConnectivity(getContext());

            if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
                Bitmap bitmap = callback.getBitmapFromCacheDir(ImageLoadTask.THUMB + movieInfo.id + ".jpg");
                if(bitmap != null) {
                    image.setImageBitmap(bitmap);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(LOG, "ERROR : Bitmap is null");
                }
            }else {
                task = new ImageLoadTask(getContext(), movieInfo.id, "thumb", movieInfo.thumb, image, progressBar);
                task.execute();     // 이미지로드
            }

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

            // 갤러리 리싸이클러뷰 정의 및 어뎁터 생성
            recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(0,0,20,0);
                }
            });
            galleryAdapter = new GalleryAdapter(getContext());
            setGalleryAdapter(movieInfo);
            galleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(GalleryAdapter.ViewHolder holder, View view, int position) {
                    recyclerViewItemClick(position);
                    Toast.makeText(getContext(), "아이템 " + position, Toast.LENGTH_SHORT).show();
                }
            });

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
            if(commentList != null) {
                commentAdapter.setItems(commentList.result);
                listView.setAdapter(commentAdapter);
            }

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
                    if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {

                    }else {
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
                }
            });

            dislikeButton = (Button)rootView.findViewById(R.id.dislikeButton);
            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {

                    }else {
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
                    if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
                        callback.returnToAllCommentsActivity(commentList.result, movieInfo, movieInfo.totalCount);      // DB로 부터 저장된 movieInfo 의 totalCount 를 이용
                    }else {
                        callback.returnToAllCommentsActivity(commentList.result, movieInfo, totalCount);
                    }
                }
            });
        }

        return rootView;
    }

    public void recyclerViewItemClick(int position) {
        ArrayList<GalleryInfo> items = galleryAdapter.getItems();
        GalleryInfo item = items.get(position);

        if(item.isYoutube()) {      // 영상 클릭시
            String urlStr = item.getUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr));

            startActivity(intent);
        } else {        // 사진 클릭시
            Intent intent = new Intent(getContext(), PhotoActivity.class);
            intent.putExtra("url", item.getUrl());
            intent.putExtra("id", item.getId());

            startActivity(intent);
        }
    }

    public void setGalleryAdapter(MovieInfo movieInfo) {
        int id = movieInfo.id;
        String photosUrl = movieInfo.photos;        // 여러 사진 url (콤마로 구분)   ex) ~~.com,~~.com
        String videosUrl = movieInfo.videos;        // 여러 영상 url (콤마로 구분)

        if(photosUrl != null) {
            String[] photoUrls = photosUrl.split(",");

            for(String url : photoUrls) {
                GalleryInfo item = new GalleryInfo(id, url, url,false);
                galleryAdapter.addItem(item);
            }
        }

        if(videosUrl != null) {
            String[] videoUrls = videosUrl.split(",");

            for(String url : videoUrls) {
                String newUrl = "https://img.youtube.com/vi/" + url.replace("https://youtu.be/", "") + "/0.jpg";        // 썸네일 url
                Log.d(LOG, newUrl);
                GalleryInfo item = new GalleryInfo(id, url, newUrl, true);
                galleryAdapter.addItem(item);
            }
        }

        recyclerView.setAdapter(galleryAdapter);
    }

    // 프래그먼트 변경사항을 업데이트 (액티비티로부터 호출)
    public void update(CommentList commentList, int totalCount) {
        this.commentList = commentList;
        this.totalCount = totalCount;

        if(commentList != null) {
            commentAdapter.setItems(commentList.result);
            commentAdapter.notifyDataSetChanged();
        }
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
                        Log.d(LOG, "서버로 좋아요 싫어요 요청성공!");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG, "에러발생 : " + error.getMessage());
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
