package org.techtown.movieproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WriteCommentActivity extends AppCompatActivity {
    private boolean save;

    private RatingBar ratingBar;
    private EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        comments = (EditText)findViewById(R.id.commentView);

        Button saveButton = (Button)findViewById(R.id.saveButton);      // 저장 버튼
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save = true;

                Toast.makeText(getApplicationContext(), "작성하기 화면에서 돌아왔습니다.\n" +
                        "저장여부 : " + save, Toast.LENGTH_SHORT).show();

                passToMainActivity();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelButton);  // 취소 버튼
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save = false;

                Toast.makeText(getApplicationContext(), "작성하기 화면에서 돌아왔습니다.\n" +
                        "저장여부 : " + save, Toast.LENGTH_SHORT).show();

                returnToMainActivity();
            }
        });
    }

    private void passToMainActivity() {     // 메인화면으로 데이터 전달
        float rating = ratingBar.getRating();
        String comment = comments.getText().toString();

        if(comment.equals("")) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            putExtraToIntent(intent, rating, comment);

            startActivity(intent);
        }
    }

    private void putExtraToIntent(Intent intent, float rating, String comment) {
        intent.putExtra("rating",rating);
        intent.putExtra("comments", comment);
    }

    private void returnToMainActivity() {   // 메인화면으로 (데이터 x)
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
    }
}