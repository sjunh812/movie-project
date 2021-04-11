package org.techtown.movieproject.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.techtown.movieproject.menu.MenuLayout;

import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {
    private ArrayList<Integer> items = new ArrayList<>();
    private Context context;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    public void addItem(int item) {
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
        MenuLayout view = null;

        if(convertView != null) {
            view = (MenuLayout)convertView;
        } else {
            view = new MenuLayout(context);
        }

        int res = items.get(position);
        view.setImageView(res);

        return view;
    }
}
