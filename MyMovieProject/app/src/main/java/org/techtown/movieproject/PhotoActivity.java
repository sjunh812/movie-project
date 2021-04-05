package org.techtown.movieproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import org.techtown.movieproject.callback.GalleryCallback;
import org.techtown.movieproject.helper.ImageLoadTask;
import org.techtown.movieproject.helper.NetworkStatus;

public class PhotoActivity extends AppCompatActivity implements GalleryCallback {
    // 해당 액티비티에선 핀치줌 구현 (PhotoView 외부 라이브러리로 구현가능)
    private static final String LOG = "PhotoActivity";
    private static final int NONE = 0;
    private static final int SINGLE = 1;    // 한 손가락
    private static final int MULTI = 2;     // 두 손가락

    private int mode = NONE;

    private Matrix matrix;
    private Matrix savedMatrix;

    private PointF startPoint;      // 한 손가락으로 터치시 좌표
    private PointF midPoint;        // 두 손가락으로 줌인,줌아웃시 중간점 좌표
    private float oldDistance;      // 두 손가락으로 줌인,줌아웃시 처음 터치한 두 손가락 간 거리

    private ImageView imageView;
    private ProgressBar progressBar;
    private String urlStr;      // 사진 url
    private int id;     // 해당 사진의 영화 id
    private ImageLoadTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("사진 보기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        matrix = new Matrix();
        savedMatrix = new Matrix();
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnTouchListener(onTouch);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        Intent intent = getIntent();
        processIntent(intent);
    }

    public void processIntent(Intent intent) {
        int networkStatus = NetworkStatus.getConnectivity(this);

        if(intent != null) {
            if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
                progressBar.setVisibility(View.VISIBLE);
            }else {
                urlStr = intent.getStringExtra("url");
                id = intent.getIntExtra("id", -1);
                task = new ImageLoadTask(this, id, "gallery", urlStr, imageView, progressBar);    // 이미지로드
                task.execute();
            }
        }
    }

    private View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.equals(imageView)) {
                int action = event.getAction();

                switch(action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mode = SINGLE;
                        downSingleEvent(event);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if(event.getPointerCount() == 2) {
                            mode = MULTI;
                            downMultiEvent(event);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(mode == SINGLE) {
                            moveSingleEvent(event);
                        } else if(mode == MULTI) {
                            moveMultiEvent(event);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    default:
                        break;
                }
                return true;
            }
            else {
                return false;
            }
        }
    };

    private void downSingleEvent(MotionEvent event) {
        savedMatrix.set(matrix);
        startPoint = new PointF(event.getX(), event.getY());
    }

    private void downMultiEvent(MotionEvent event) {
        oldDistance = getDistance(event);

        if(oldDistance > 5f) {
            savedMatrix.set(matrix);
            midPoint = getMidPoint(event);
        }
    }

    private void moveSingleEvent(MotionEvent event) {
        matrix.set(savedMatrix);
        float dx = event.getX() - startPoint.x;
        float dy = event.getY() - startPoint.y;

        matrix.postTranslate(dx, dy);
        imageView.setImageMatrix(matrix);
    }

    private void moveMultiEvent(MotionEvent event) {
        float newDistance = getDistance(event);

        if(newDistance > 5f) {
            matrix.set(savedMatrix);
            float scale = newDistance / oldDistance;
            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
            imageView.setImageMatrix(matrix);
        }
    }

    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }

    private PointF getMidPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;

        return new PointF(x, y);
    }

    // 이미지 뷰의 높이, 너비등 측정 (onCreate() 메소드에선 이미지 뷰가 그려지기 이전이므로 측정불가)
    public void init() {
        Drawable d = imageView.getDrawable();
        // TODO: check that d isn't null
        if(d != null) {
            RectF imageRectF = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            RectF viewRectF = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
            matrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);
            imageView.setImageMatrix(matrix);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}