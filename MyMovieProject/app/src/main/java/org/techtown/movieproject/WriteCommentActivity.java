package org.techtown.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.comment.CommentAdapter;

import java.util.ArrayList;

public class WriteCommentActivity extends AppCompatActivity {
    // UI
    private TextView title;
    private ImageView grade;
    private RatingBar ratingBar;
    private EditText id;
    private EditText comments;

    // 데이터
    private MovieInfo movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        title = (TextView)findViewById(R.id.title);
        grade = (ImageView)findViewById(R.id.grade);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        id = (EditText)findViewById(R.id.idView);
        comments = (EditText)findViewById(R.id.commentView);

        // 네트워크 연결상태 점검
        int networkStatus = NetworkStatus.getConnectivity(this);

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            Snackbar.make(title, "네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.", Snackbar.LENGTH_LONG).show();
        }

        Button saveButton = (Button)findViewById(R.id.saveButton);      // 저장 버튼
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passToMainActivity();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelButton);  // 취소 버튼
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainActivity();
            }
        });

        Intent intent = getIntent();
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        movieInfo = (MovieInfo)intent.getSerializableExtra("movieInfo");

        title.setText(movieInfo.title);
        setGradeImage(movieInfo.grade);
    }

    // 메인액티비티로 Intent 객체전달
    private void passToMainActivity() {
        float rating = ratingBar.getRating();
        String id = this.id.getText().toString();
        String comment = comments.getText().toString();

        if(id.equals("")) {
            Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요!", Toast.LENGTH_LONG).show();
        }
        else {
            if(comment.equals("")) {
                Toast.makeText(getApplicationContext(), "내용을 입력해주세요!", Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("movieId", movieInfo.id);
                intent.putExtra("userId", id);
                intent.putExtra("rating",rating);
                intent.putExtra("comments", comment);

                startActivity(intent);
            }
        }
    }

    private void returnToMainActivity() {   // 메인화면으로 (데이터 x)
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
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