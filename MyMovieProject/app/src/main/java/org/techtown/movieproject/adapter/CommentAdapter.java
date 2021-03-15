package org.techtown.movieproject.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.techtown.movieproject.CommentItem;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {
    private ArrayList<CommentItem> items = new ArrayList<CommentItem>();

    Context context;

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public void addItem(CommentItem item) {
        // 역순으로 데이터 추가 (최신 아이템이 제일 위로)
        if(items.size() == 0) {
            items.add(item);
        }
        else {
            items.add(items.get(items.size() - 1));
            for(int i=items.size()-2; i>=0; i--) {
                items.set(i+1, items.get(i));
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
            view = new CommentLayout(context);
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

    public ArrayList<CommentItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CommentItem> items) {
        this.items = items;
    }
}
