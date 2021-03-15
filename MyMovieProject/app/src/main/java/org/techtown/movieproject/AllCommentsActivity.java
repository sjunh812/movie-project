package org.techtown.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.techtown.movieproject.adapter.CommentAdapter;

import java.util.ArrayList;

public class AllCommentsActivity extends AppCompatActivity {
    ArrayList<CommentItem> items;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comments);

        listView = (ListView)findViewById(R.id.listView);

        Button writeButton = (Button)findViewById(R.id.writeButton);    // 작성하기 버튼
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToWriteCommentActivity();
            }
        });

        Intent intent = getIntent();
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        items = (ArrayList<CommentItem>)intent.getSerializableExtra("listViewData");
        CommentAdapter adapter = new CommentAdapter(getApplicationContext());      // 넘어온 ArrayList<CommentItem> 데이터를 이용해 어뎁터 생성
        adapter.setItems(items);
        listView.setAdapter(adapter);
    }

    private void returnToWriteCommentActivity() {
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);

        startActivity(intent);
    }

/*    class CommentAdapter extends BaseAdapter implements Serializable {
        public void addItem(CommentItem item) {
            // 역순 배치
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

            CommentItem item = items.get(position);

            view.setUserId(item.getId());
            view.setComment(item.getComment());
            view.setRatingBar(item.getRating());

            return view;
        }
    }*/
}