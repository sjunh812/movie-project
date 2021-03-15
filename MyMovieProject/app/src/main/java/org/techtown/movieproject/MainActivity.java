package org.techtown.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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

import org.techtown.movieproject.adapter.CommentAdapter;
import org.techtown.movieproject.moviedetails.*;
import org.techtown.movieproject.movielist.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback{
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FragmentManager manager;

    // 영화 목록 프래그먼트
    MoiveListFragment1 listFragment1;
    MoiveListFragment2 listFragment2;
    MoiveListFragment3 listFragment3;
    MoiveListFragment4 listFragment4;
    MoiveListFragment5 listFragment5;
    MoiveListFragment6 listFragment6;

    // 영화 상세보기 프래그먼트
    MovieDetailsFragment1 detailsFragment1;
    MovieDetailsFragment2 detailsFragment2;
    MovieDetailsFragment3 detailsFragment3;
    MovieDetailsFragment4 detailsFragment4;
    MovieDetailsFragment5 detailsFragment5;
    MovieDetailsFragment6 detailsFragment6;

    // 한줄평 데이터
    ArrayList<CommentItem> items;
    Bundle commentBundle;

    // 초기 데이터 저장
    public void InitData() {
        commentBundle = new Bundle();
        items = new ArrayList<>();

        items.add(new CommentItem("kym71**", "적당히 재밌다. 오랜만에 잠 안오는 영화 봤네요.", 4.1f));
        items.add(new CommentItem("sjunh8**", "그럭저럭 기분전환으로 볼 만 했습니다.", 3.8f));

        commentBundle.putSerializable("commentData", items);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InitData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        manager = getSupportFragmentManager();

        // 영화 상세보기 프래그먼트 정의
        detailsFragment1 = new MovieDetailsFragment1();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("영화 목록");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // 뷰페이저 정의
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(manager);   // 뷰페이저 어뎁터 정의

        // 뷰페이저 내 양쪽 뷰 미리보기
        int dpValue = 54;
        float d = getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d);

        pager.setClipToPadding(false);
        pager.setPadding(margin, 0, margin, 0);
        pager.setPageMargin(margin/2);

        // 뷰페이저를 구성하는 프래그먼트 정의 및 추가
        listFragment1 = new MoiveListFragment1();
        listFragment2 = new MoiveListFragment2();
        listFragment3 = new MoiveListFragment3();
        listFragment4 = new MoiveListFragment4();
        listFragment5 = new MoiveListFragment5();
        listFragment6 = new MoiveListFragment6();

        pagerAdapter.addItem(listFragment1);
        pagerAdapter.addItem(listFragment2);
        pagerAdapter.addItem(listFragment3);
        pagerAdapter.addItem(listFragment4);
        pagerAdapter.addItem(listFragment5);
        pagerAdapter.addItem(listFragment6);

        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_list, menu);

        return true;
    }*/

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
/*            Intent intent = new Intent(getApplicationContext(), AllCommentsActivity.class);
            intent.putExtra("listViewData", items);     // ArrayList<CommentItem> 부가데이터
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);*/
        }
        else if(id == R.id.nav_book) {

        }
        else if(id == R.id.nav_settings) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onMoiveDetailsFragment(int index) {
        FragmentTransaction transaction;

        switch(index) {
            case 1:
                toolbar.setTitle("영화 상세");
                detailsFragment1.setArguments(commentBundle);   // 프레그먼트에 한줄평 초기데이터 전달
                transaction = manager.beginTransaction().replace(R.id.container, detailsFragment1);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            default:
        }
    }

    @Override
    public void returnToWriteCommentActivity() {
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);

        startActivity(intent);
    }

    @Override
    public void returnToAllCommentsActivity(ArrayList<CommentItem> items) {
        Intent intent = new Intent(getApplicationContext(), AllCommentsActivity.class);
        intent.putExtra("listViewData", items);     // ArrayList<CommentItem> 부가데이터

        startActivity(intent);
    }

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            if(intent.getStringExtra("comments") != null) {
                addToListView(intent);
            }
            else{   // intent내 data값이 null인 경우
                Snackbar.make(toolbar, "취소", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void addToListView(Intent intent) {
        float rating = intent.getFloatExtra("rating", 0.0f);
        String comments = intent.getStringExtra("comments");

        detailsFragment1.update(new CommentItem("sjunh8**", comments, rating));
    }

    // 뷰페이저 어텝터 클래스
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
}