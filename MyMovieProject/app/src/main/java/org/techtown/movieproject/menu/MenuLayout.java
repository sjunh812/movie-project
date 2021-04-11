package org.techtown.movieproject.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.techtown.movieproject.R;

public class MenuLayout extends FrameLayout {
    private ImageView imageView;

    public MenuLayout(@NonNull Context context) {
        super(context);

        init(context);
    }

    public MenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.menu_image, this, true);

        imageView = (ImageView)findViewById(R.id.imageView);
    }

    public void setImageView(int res) {
        imageView.setImageResource(res);
    }
}
