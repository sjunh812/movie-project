package org.techtown.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.CommentList;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.api.MovieList;
import org.techtown.movieproject.api.ResponseInfo;
import org.techtown.movieproject.fragment.MovieDetailsFragment;
import org.techtown.movieproject.fragment.MovieListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback, ActivityCallback{
    // UI
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ViewPager pager;

    //
    private FragmentManager manager;
    private PagerAdapter pagerAdapter;

    // Fragment
    MovieListFragment listFragment;
    MovieDetailsFragment detailsFragment;

    // Data
    private int currMovieId = -1;
    private ResponseInfo info;
    private CommentList commentList;
    Bundle bundle;
    boolean isDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // RequestQueue 정의 (최초 1회 in Volley)
        if(AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        readMovieList();    // 서버로부터 영화리스트 가져오기

        manager = getSupportFragmentManager();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("영화 목록");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        pager = (ViewPager)findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(manager);
        setViewPagerPadding(54);    // 뷰페이저 내 양쪽뷰 보이게하기
    }

    public void setViewPagerPadding(int dpValue) {
        float d = getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d);

        pager.setClipToPadding(false);
        pager.setPadding(margin, 0, margin, 0);
        pager.setPageMargin(margin/2);
    }

    // 영화 리스트
    public void readMovieList() {
        // type(1:예매율순, 2:큐레이션, 3:개봉일)
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readMovieList" + "?";
        url += "type=1";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processListResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);      // 이전 데이터가 있어도 무시
        AppHelper.requestQueue.add(request);
    }
    public void processListResponse(String response) {
        // JSON 포멧을 Gson 을 이용해 자바객체로 파싱
        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        if(info.code == 200) {      // 정상 응답
            MovieList movieList = gson.fromJson(response, MovieList.class);
            setViewPagerFragment(movieList);
        }
    }
    public void passInfoToListFragment(Fragment fragment, MovieInfo movieInfo, int index) {
        bundle = new Bundle();
        bundle.putSerializable("movieInfo", movieInfo);
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
    }
    public void setViewPagerFragment(MovieList movieList) {
        for(int i = 0; i < movieList.result.size(); i++) {
            MovieInfo movieInfo = movieList.result.get(i);
            listFragment = new MovieListFragment();

            // 영화목록 프래그먼트에 MovieInfo 및 index(순서번호) 전달
            passInfoToListFragment(listFragment, movieInfo, i+1);
            pagerAdapter.addItem(listFragment);
        }
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onMoiveDetailsFragment(int id) {
        detailsFragment = new MovieDetailsFragment();
        toolbar.setTitle("영화 상세");
        readMovie(id);      // 영화 id를 매개변수로
        currMovieId = id;
    }

    // 영화 상세보기
    public void readMovie(int id) {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readMovie" + "?";
        url += "id=" + id;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processDetailsResponse(response, id);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);      // 이전 데이터가 있어도 무시
        AppHelper.requestQueue.add(request);
    }
    public void passInfoToBundle(MovieInfo movieInfo) {
        // 영화 상세보기 프래그먼트에 MovieInfo 전달
        bundle = new Bundle();
        bundle.putSerializable("movieInfo", movieInfo);
    }
    public void processDetailsResponse(String response, int id) {
        int limit = 100;    // 조회할 건수 (한줄평)

        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        if(info.code == 200) {      // 정상 응답
            MovieList movieList = gson.fromJson(response, MovieList.class);
            MovieInfo movieInfo = movieList.result.get(0);
            passInfoToBundle(movieInfo);
            synchronization(id, limit);
        }
    }
    public void passInfoToDetailsFragment(Fragment fragment, CommentList commentList, int totalCount) {
        // passInfoToBundle 에서 선언한 bundle 이용
        bundle.putSerializable("commentList", commentList);
        bundle.putInt("totalCount", totalCount);
        fragment.setArguments(bundle);
    }
    public void processCommentResponse(String response) {
        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        if(info.code == 200) {
            CommentList commentList = gson.fromJson(response, CommentList.class);
            if(isDetail) {      // 영화 상세보기 프래그먼트가 띄워진 상태
                detailsFragment.update(commentList, info.totalCount);
            }
            else {      // 다른 프래그먼트가 띄워진 상태
                passInfoToDetailsFragment(detailsFragment, commentList, info.totalCount);

                FragmentTransaction transaction;
                transaction = manager.beginTransaction().replace(R.id.container, detailsFragment);
                transaction.addToBackStack(null);   // 뒤로가기 시 프래그먼트 종료를 위해 backstack 사용
                transaction.commit();
            }
        }
    }

    // 다른 액티비티로 Intent 객체 받고 동기화
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            if(intent.getStringExtra("comments") != null) {
                addToListView(intent);
            }
        }
    }
    private void addToListView(Intent intent) {
        int movieId = intent.getIntExtra("movieId", -1);
        String userId = intent.getStringExtra("userId");
        float rating = intent.getFloatExtra("rating", 0.0f);
        String comments = intent.getStringExtra("comments");

        createComment(movieId, userId, rating, comments);   // 한줄평 작성 저장
    }
    public void createComment(int movieId, String userId, float rating, String comments) {
        // id(영화 ID), writer(작성자 ID), rating(평점), contents(의견)
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/createComment" + "?";
        url += "id=" + movieId + "&writer=" + userId + "&rating=" + rating + "&contents=" + comments;

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
                        Log.d("ERROR", "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
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
                        processCommentResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    // 작성하기 액티비티전환
    @Override
    public void returnToWriteCommentActivity(MovieInfo movieInfo) {
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);
        intent.putExtra("movieInfo", movieInfo);

        startActivity(intent);
    }

    // 모두보기 액티비티전환
    @Override
    public void returnToAllCommentsActivity(ArrayList<CommentInfo> items, MovieInfo movieInfo, int totalCount) {
        Intent intent = new Intent(getApplicationContext(), AllCommentsActivity.class);
        intent.putExtra("listViewData", items);     // ArrayList<CommentInfo> 부가데이터
        intent.putExtra("movieInfo", movieInfo);
        intent.putExtra("totalCount", totalCount);

        startActivity(intent);
    }

    // 뷰페이저 어뎁터 클래스
    class PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<>();

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    // 한줄평 추천수 up
    @Override
    public void increaseRecommend(int reviewId, int movieId) {
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
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    // 툴바이름 변경
    @Override
    public void changeToolbarTitle(int code) {
        switch(code){
            case 0:
                toolbar.setTitle("영화 목록");
                break;
            default:
                break;
        }
    }

    @Override
    public void setDetailBool(Boolean bool) {
        isDetail = bool;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "OnResume()-MainActivity", Toast.LENGTH_SHORT).show();

        if(currMovieId != -1) {
            synchronization(currMovieId, 100);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "OnDestroy()-MainActivity", Toast.LENGTH_SHORT).show();
    }

    // 뒤로가기
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    // 메뉴 바
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.movie_list, menu);
        return true;
    }

    // 바로가기 메뉴
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_list) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    // 액티비티 초기화 (onCreate()호출)
            startActivity(intent);
        }
        else if(id == R.id.nav_review) {
        }
        else if(id == R.id.nav_book) {
        }
        else if(id == R.id.nav_settings) {
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}