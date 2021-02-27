package org.techtown.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button likeButton;
    Button dislikeButton;
    TextView likeCountView;
    TextView dislikeCountView;

    private boolean like = false;
    private boolean dislike = false;

    private int likeCount = 15;
    private int dislikeCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        likeButton = (Button)findViewById(R.id.likeButton);
        dislikeButton = (Button)findViewById(R.id.dislikeButton);
        likeCountView = (TextView)findViewById(R.id.likeCountView);
        dislikeCountView = (TextView)findViewById(R.id.dislikeCountView);
        Button writeButton = (Button)findViewById(R.id.writeButton);
        Button allSeeButton = (Button)findViewById(R.id.allSeeButton);
        ListView listView = (ListView)findViewById(R.id.listView);
        CommentAdapter adapter = new CommentAdapter();

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
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "작성하기", Toast.LENGTH_SHORT).show();
            }
        });
        allSeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "모두보기", Toast.LENGTH_SHORT).show();
            }
        });
        // listView 설정
        listView.setAdapter(adapter);
        adapter.addItem(new CommentItem("kym71**", "적당히 재밌다. 오랜만에 잠 안오는 영화 봤네요."));
        adapter.addItem(new CommentItem("sjunh8**", "그럭저럭 기분전환으로 볼 만 했습니다."));
    }

    class CommentAdapter extends BaseAdapter {

        ArrayList<CommentItem> items = new ArrayList<CommentItem>();

        public void addItem(CommentItem item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
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
            // 데이터 관리
            CommentItem item = items.get(position);

            view.setUserId(item.getId());
            view.setComment(item.getComment());

            return view;
        }
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