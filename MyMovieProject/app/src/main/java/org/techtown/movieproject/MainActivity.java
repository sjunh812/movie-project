package org.techtown.movieproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import org.techtown.movieproject.callback.ActivityCallback;
import org.techtown.movieproject.callback.FragmentCallback;
import org.techtown.movieproject.fragment.MovieDetailsFragment;
import org.techtown.movieproject.fragment.MovieListFragment;
import org.techtown.movieproject.helper.AppHelper;
import org.techtown.movieproject.helper.MenuAdapter;
import org.techtown.movieproject.helper.NetworkStatus;
import org.techtown.movieproject.helper.PagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback, ActivityCallback {
    // 상수
    private static final String TAG = "MainActivity";

    // UI
    private Toolbar toolbar;
    private DrawerLayout drawer;        // 네비게이션 바 내 레이아웃 중 최상단 레이아웃
    private ViewPager pager;            // 영화 목록을 보여주기위한 뷰페이저(프레그먼트로 구성)
    private ListView menuListView;      // 옵션메뉴 선택시 나타타는 뷰
    private View menuView;              // 옵션메뉴를 구성하는 뷰

    // Helper
    private AppHelper helper;
    private FragmentManager manager;
    private PagerAdapter pagerAdapter;
    private MenuAdapter menuAdapter;

    // 애니메이션
    private Animation translateUp;
    private Animation translateDown;

    // 프래그먼트
    MovieListFragment listFragment;
    MovieDetailsFragment detailsFragment;

    // 데이터
    private int currMovieId = -1;       // -1이 아니면 onResume() 에서 동기화
    private Bundle bundle;              // setArgument()를 호출하여 프래그먼트로 전달할 Bundle 객체
    boolean isDetail = false;           // 화면에 띄워진 프래그먼트가 상세보기 프래그먼트인지 여부
    private int type = 1;               // 1:예매율순, 2:큐레이션, 3:개봉일
    private boolean isMenuOpen = false; // 옵션메뉴가 열렸는지 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // 애니메이션 초기화
        MenuAnimationListener listener = new MenuAnimationListener();
        translateUp = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        translateUp.setAnimationListener(listener);
        translateDown = AnimationUtils.loadAnimation(this, R.anim.translate_down);
        translateDown.setAnimationListener(listener);

        // Menu 초기화
        menuListView = (ListView)findViewById(R.id.menu_listView);
        menuAdapter = new MenuAdapter(this);
        menuAdapter.addItem(R.drawable.order11);
        menuAdapter.addItem(R.drawable.order22);
        menuAdapter.addItem(R.drawable.order33);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());     // 네트워크 상태
                int curType = type;
                ImageView imageView = (ImageView)menuView.findViewById(R.id.imageView);

                switch(position) {
                    case 0:
                        type = 1;       // 예매율순
                        imageView.setImageResource(R.drawable.order11);
                        break;
                    case 1:
                        type = 2;       // 큐레이션
                        imageView.setImageResource(R.drawable.order22);
                        break;
                    case 2:
                        type = 3;       // 상영예정
                        imageView.setImageResource(R.drawable.order33);
                        break;
                }

                if(curType != type) {
                    pagerAdapter.clearItems();      //  현재 띄워진 뷰페이저 어뎁터 아이템들을 모두 지움
                    pagerAdapter.notifyDataSetChanged();    // 동기화
                }

                menuListView.startAnimation(translateUp);

                if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {                             // 인터넷 연결안됨
                    MovieList movieList = helper.selectMovieList("movielist", type);      // DB 에서 영화목록 데이터 가져옴
                    if(movieList != null) {
                        setViewPagerFragment(movieList);
                        Snackbar.make(toolbar, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
                    }
                }else {                         // 인터넷 연결됨
                    readMovieList(type);        // 서버로부터 영화목록 가져오기
                }
            }
        });

        // Volley 에서 쓸 RequestQueue 정의 (최초 1회)
        if(AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        // DB 생성(오픈) 및 movielist, moviedetails 테이블 생성
        helper = new AppHelper();
        helper.openDB(getApplicationContext(), "movie.db");
        helper.createTable("movielist");
        helper.createTable("moviedetails");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("영화 목록");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        manager = getSupportFragmentManager();
        pager = (ViewPager)findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(manager);
        setViewPagerPadding(54);    // 뷰페이저 내 양쪽뷰 보이게 함

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // 네트워크 연결상태 점검
        int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {     // 인터넷 연결안됨
            MovieList movieList = helper.selectMovieList("movielist", type);      // DB 에서 영화목록 데이터 가져옴

            if(movieList != null) {
                setViewPagerFragment(movieList);
            }
        }else {     // 인터넷 연결됨
            readMovieList(type);        // 서버로부터 영화목록 가져오기
        }
    }

    public void setViewPagerPadding(int dpValue) {
        float d = getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d);

        pager.setClipToPadding(false);
        pager.setPadding(margin, 0, margin, 0);
        pager.setPageMargin(margin/2);
    }

    // 영화목록 가져오기
    public void readMovieList(int type) {
        // type(1:예매율순, 2:큐레이션, 3:개봉일)
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readMovieList" + "?";
        url += "type=" + type;

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
                        Log.d(TAG, "응답 에러 : " + error.getMessage());
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

            helper.insertMovieList("movielist", movieList);     // DB에 영화목록 저장
        }
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
    public void passInfoToListFragment(Fragment fragment, MovieInfo movieInfo, int index) {
        bundle = new Bundle();
        bundle.putSerializable("movieInfo", movieInfo);     // 영화목록 데이터
        bundle.putInt("index", index);                       // 순서번호

        fragment.setArguments(bundle);
    }

    // 영화상세보기 가져오기 (영화목록 프래그먼트에서 상세보기 버튼 클릭시)
    @Override
    public void onMoiveDetailsFragment(int id, int networkStatus) {
        detailsFragment = new MovieDetailsFragment();
        toolbar.setTitle("영화 상세");
        currMovieId = id;

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {     // 인터넷 연결안됨
            Snackbar.make(toolbar, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();

            MovieList movieList = helper.selectMovieList("moviedetails", 4);        // 영화상세 DB (type:4 = default)
            ArrayList<MovieInfo> list = movieList.result;
            MovieInfo info = null;
            CommentList commentList = helper.selectCommentList("commentlist" + id);      // 한줄평목록 DB

            // 영화 상세보기 DB 데이터 중 id에 맞는 튜플찾기
            for(int i = 0; i < list.size(); i++) {
                MovieInfo sample = list.get(i);
                if(sample.id == id) {
                    info = sample;
                    break;
                }
            }

            bundle = new Bundle();
            bundle.putSerializable("movieInfo", info);
            bundle.putSerializable("commentList", commentList);
            detailsFragment.setArguments(bundle);

            FragmentTransaction transaction;
            transaction = manager.beginTransaction().replace(R.id.container, detailsFragment);
            transaction.addToBackStack(null);       // 뒤로가기 시, 프래그먼트 종료를 위해 BackStack 사용
            transaction.commit();
        } else {                                                  // 인터넷 연결됨
            readMovie(id);                                        // 서버로부터 영화상세보기 가져오기
            helper.createTable("commentlist" + id);     // 영화 id 에 맞는 한줄평 테이블 생성 (ex) 영화 ID가 1일 경우 = commentlist1)
        }
    }
    public void readMovie(int id) {
        // id(영화 id)
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
                        Log.d(TAG, "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);      // 이전 데이터가 있어도 무시
        AppHelper.requestQueue.add(request);
    }
    public void processDetailsResponse(String response, int id) {
        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        if(info.code == 200) {                                                // 정상 응답
            MovieList movieList = gson.fromJson(response, MovieList.class);
            MovieInfo movieInfo = movieList.result.get(0);
            helper.insertMovieList("moviedetails", movieList);      // DB에 영화리스트 저장

            passInfoToBundle(movieInfo);
            synchronization(id, 100);                                    // 조회할 건수 = 100개
        }
    }
    public void passInfoToBundle(MovieInfo movieInfo) {
        // 영화 상세보기 프래그먼트에 MovieInfo 전달
        bundle = new Bundle();
        bundle.putSerializable("movieInfo", movieInfo);
    }
    // 한줄평목록 가져오기
    public void synchronization(int id, int limit) {
        // id(영화 id), limit(조회할 건수)
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/readCommentList" + "?";
        url += "id=" + id + "&limit=" + limit;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processCommentResponse(response);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }
    public void processCommentResponse(String response) {
        Gson gson = new Gson();
        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        if(info.code == 200) {      // 정상 응답
            CommentList commentList = gson.fromJson(response, CommentList.class);

            helper.updateMovieCommentTotalCount("moviedetails", info.totalCount, currMovieId);      // DB에 한줄평 개수 업데이트 (단, moviedetails 테이블에 갱신)
            helper.insertCommentList("commentlist" + currMovieId, commentList);                     // DB에 한줄평 데이터 저장

            if(isDetail) {      // 영화상세보기 프래그먼트가 화면상단에 위치
                detailsFragment.update(commentList, info.totalCount);       // 동기화
            }
            else {      // 영화상세보기 외 프래그먼트가 화면상단에 위치
                passInfoToDetailsFragment(detailsFragment, commentList, info.totalCount);
                menuView.setVisibility(View.INVISIBLE);                                                       // 영화 상세 프래그먼트이므로 옵션메뉴창을 닫음
                if(isMenuOpen) {
                    menuListView.startAnimation(translateUp);
                }

                FragmentTransaction transaction;
                transaction = manager.beginTransaction().replace(R.id.container, detailsFragment);            // 영화상세보기 프래그먼트를 화면상단으로 대체
                transaction.addToBackStack(null);                                                             // 뒤로가기 시 프래그먼트 종료를 위해 BackStack 사용
                transaction.commit();
            }
        }
    }
    public void passInfoToDetailsFragment(Fragment fragment, CommentList commentList, int totalCount) {
        // passInfoToBundle 에서 선언한 bundle 이용
        bundle.putSerializable("commentList", commentList);
        bundle.putInt("totalCount", totalCount);

        fragment.setArguments(bundle);
    }

    // 증가한 한줄평 추천 수 서버로 전달
    @Override
    public void increaseRecommend(int reviewId, int movieId) {
        int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            Snackbar.make(toolbar, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
        } else {
            // review_id(한줄평 id)
            String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/increaseRecommend" + "?";
            url += "review_id=" + reviewId;

            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            synchronization(movieId, 100);      // 추천 수 변경으로 인한 한줄평 동기화
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
    }

    // 다른 액티비티로 부터
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());     // 네트워크 연결상태 점검

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {             // 인터넷 연결안됨
        }else {                                                             // 인터넷 연결됨
            if(intent != null) {
                if(intent.getStringExtra("comments") != null) {       // 한줄평 작성하기 액티비티로부터
                    int movieId = intent.getIntExtra("movieId", -1);
                    String userId = intent.getStringExtra("userId");
                    float rating = intent.getFloatExtra("rating", 0.0f);
                    String comments = intent.getStringExtra("comments");

                    createComment(movieId, userId, rating, comments);       // 새로 쓴 한줄평 데이터를 서버로 전달
                }
            }
        }
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
                        Log.d(TAG, "응답 에러 : " + error.getMessage());
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    // 작성하기 액티비티 전환
    @Override
    public void returnToWriteCommentActivity(MovieInfo movieInfo) {
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);
        intent.putExtra("movieInfo", movieInfo);

        startActivity(intent);
    }

    // 모두보기 액티비티 전환
    @Override
    public void returnToAllCommentsActivity(ArrayList<CommentInfo> items, MovieInfo movieInfo, int totalCount) {
        Intent intent = new Intent(getApplicationContext(), AllCommentsActivity.class);
        intent.putExtra("listViewData", items);
        intent.putExtra("movieInfo", movieInfo);
        intent.putExtra("totalCount", totalCount);

        startActivity(intent);
    }

    // 툴바이름 변경
    @Override
    public void changeToolbarTitle(int code) {
        switch(code){
            case 0:
                menuView.setVisibility(View.VISIBLE);
                toolbar.setTitle("영화 목록");
                break;
            default:
                break;
        }
    }

    @Override
    public void setisDetail(Boolean bool) {
        isDetail = bool;
    }

    // 내부저장소에 Bitmap 이미지 저장
    @Override
    public void saveBitmapToJpeg(Bitmap bitmap, String name) {
        // 내부저장소의 캐시경로
        File storage = getCacheDir();

        // 저장할 파일이름
        String fileName = name + ".jpg";

        // 스토리지에 파일 인스턴스 생성
        File tempFile = new File(storage, fileName);

        try {
            // 자동으로 빈 파일객체 생성
            tempFile.createNewFile();

            FileOutputStream outStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 내부저장소로 부터 Bitmap 이미지 가져오기
    @Override
    public Bitmap getBitmapFromCacheDir(String name) {
        ArrayList<String> list = new ArrayList<>();
        File file = new File(getCacheDir().toString());
        File[] files = file.listFiles();

        for(File tempFile : files) {
            Log.d(TAG, "내부저장소 내 파일 : " + tempFile.getName());

            if(tempFile.getName().equals(name)) {
                list.add(tempFile.getName());
            }
        }

        if(list.size() > 0) {
            int random = new Random().nextInt(list.size());
            String path = getCacheDir() + "/" + list.get(random);
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            return bitmap;
        }

        return null;
    }

    public boolean getIsMenuOpen() {
        return isMenuOpen;
    }

    public void setIsMenuOpen(boolean isMenuOpen) {
        this.isMenuOpen = isMenuOpen;
    }

    @Override
    protected void onResume() {
        super.onResume();

        int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());     // 네트워크 연결상태 점검

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {                         // 인터넷 연결안됨
            Snackbar.make(toolbar, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
        }else {                                                                         // 인터넷 연결됨
            if(currMovieId != -1) {                                                     // 앱 처음 실행화면이 아닌 상태
                synchronization(currMovieId, 100);                                 // 한줄평 동기화
            }
        }
    }

    // 뒤로가기 버튼클릭 시
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    // 메뉴 바(옵션메뉴)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list, menu);

        MenuItem menuItem = menu.getItem(0);
        menuView = menuItem.getActionView();
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen) {
                    menuListView.startAnimation(translateUp);
                } else {
                    menuListView.setVisibility(View.VISIBLE);
                    menuListView.startAnimation(translateDown);
                }
            }
        });

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

    class MenuAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isMenuOpen) {
                isMenuOpen = false;
                menuListView.setVisibility(View.INVISIBLE);
            } else {
                isMenuOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}