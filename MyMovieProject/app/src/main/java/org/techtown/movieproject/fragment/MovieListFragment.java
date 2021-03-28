package org.techtown.movieproject.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.techtown.movieproject.AppHelper;
import org.techtown.movieproject.FragmentCallback;
import org.techtown.movieproject.ImageLoadTask;
import org.techtown.movieproject.NetworkStatus;
import org.techtown.movieproject.R;
import org.techtown.movieproject.api.MovieInfo;
import org.techtown.movieproject.api.MovieList;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class MovieListFragment extends Fragment {
    private FragmentCallback callback;

    // 상수
    private static final String LOG = "movieListFragment";

    // UI
    private ImageView imageView;
    private TextView title;
    private TextView textView;
    private ProgressBar progressBar;

    // 데이터
    private int index;
    private MovieInfo movieInfo;
    private String grade;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FragmentCallback) {
            callback = (FragmentCallback)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(callback != null) {
            callback = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_movie_list, container, false);

        if(getArguments() != null) {
            movieInfo = (MovieInfo)getArguments().getSerializable("movieInfo");
            index = getArguments().getInt("index");
        }

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        imageView = (ImageView)rootView.findViewById(R.id.imageView);

        int networkStatus = NetworkStatus.getConnectivity(getContext());

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {
            Bitmap bitmap = callback.getBitmapFromCacheDir(ImageLoadTask.IMAGE + movieInfo.id + ".jpg");
            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }else {
                progressBar.setVisibility(View.VISIBLE);
                Log.d(LOG, "ERROR : Bitmap is null");
            }
        }else {
            ImageLoadTask task = new ImageLoadTask(getContext(), movieInfo.id, "image", movieInfo.image, imageView, progressBar);
            task.execute();
        }

        title = (TextView)rootView.findViewById(R.id.title);
        title.setText(index + ". " + movieInfo.title);

        textView = (TextView)rootView.findViewById(R.id.textView);
        setGrade(movieInfo.grade);
        textView.setText("예매율 " + movieInfo.reservation_rate + "% | " + grade);

        Button button = (Button)rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = movieInfo.id;
                int networkStatus = NetworkStatus.getConnectivity(getContext());

                callback.onMoiveDetailsFragment(id, networkStatus);
            }
        });

        return rootView;
    }

    private void setGrade(int grade) {
        if(grade == 19) {
            this.grade = "청소년 관람불가";
        }
        else {
            this.grade = grade + "세 관람가";
        }
    }
}
