package org.techtown.project1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button likeButton;
    Button dislikeButton;

    TextView likeCountView;
    TextView dislikeCountView;

    CommentAdapter adapter;

    ArrayList<CommentItem> items = new ArrayList<CommentItem>();

    private boolean like = false;
    private boolean dislike = false;

    // 좋아요 싫어요 데이터.
    private int likeCount = 15;
    private int dislikeCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        likeCountView = (TextView)findViewById(R.id.likeCountView);
        dislikeCountView = (TextView)findViewById(R.id.dislikeCountView);

        likeButton = (Button)findViewById(R.id.likeButton);             // 좋아요 버튼.
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

        dislikeButton = (Button)findViewById(R.id.dislikeButton);       // 싫어요 버튼.
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

        Button writeButton = (Button)findViewById(R.id.writeButton);    // 작성하기 버튼.
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToWriteCommentActivity();
            }
        });

        Button allSeeButton = (Button)findViewById(R.id.allSeeButton);  // 모두보기 버튼.
        allSeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToAllCommentsActivity();
            }
        });

        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new CommentAdapter();
        listView.setAdapter(adapter);

        // 한줄평 데이터 넣기.
        adapter.addItem(new CommentItem("kym71**", "적당히 재밌다. 오랜만에 잠 안오는 영화 봤네요.", 4.1f));
        adapter.addItem(new CommentItem("sjunh8**", "그럭저럭 기분전환으로 볼 만 했습니다.", 3.8f));
    }

    private void returnToWriteCommentActivity() {   // 작성하기 버튼 클릭시.
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);

        startActivity(intent);
    }

    private void returnToAllCommentsActivity() {    // 모두보기 화면 클릭시.
        Intent intent = new Intent(getApplicationContext(), AllCommentsActivity.class);
        intent.putExtra("listViewData", items);     // ArrayList<CommentItem> 부가데이터.

        startActivity(intent);
    }

    class CommentAdapter extends BaseAdapter {  // 한줄평 리스트 뷰의 Adapter 재정의.
        public void addItem(CommentItem item) {
            // 역순으로 데이터 추가. (최신이 제일 위로)
            if(items.size() == 0) {
                items.add(item);
            }
            else {
                items.add(items.get(items.size() - 1));
                for(int i = items.size() - 2; i >= 0; i--) {
                    items.set(i + 1, items.get(i));
                }
                items.set(0, item);
            }
        }

        @Override
        public int getCount() {
            // 사용자 눈에 2개만 보이도록.
            if(items.size() < 2) {
                return items.size();
            }
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentLayout view = null;

            if(convertView == null) {
                view = new CommentLayout(getApplicationContext());
            }
            else {
                view = (CommentLayout)convertView;
            }

            CommentItem item = items.get(position);

            view.setUserId(item.getId());
            view.setComment(item.getComment());
            view.setRatingBar(item.getRating());

            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            if(intent.getStringExtra("comments") != null) {
                addToListView(intent);
            }
            else{   // intent내 data값이 null인 경우.
                Snackbar.make(likeButton, "취소", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void addToListView(Intent intent) {
        float rating = intent.getFloatExtra("rating", 0.0f);
        String comments = intent.getStringExtra("comments");

        adapter.addItem(new CommentItem("sjunh8**", comments, rating));
        adapter.notifyDataSetChanged();     // 리스트 뷰 동기화.
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