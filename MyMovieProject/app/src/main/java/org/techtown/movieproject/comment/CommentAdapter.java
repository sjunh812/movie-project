package org.techtown.movieproject.comment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.movieproject.ActivityCallback;
import org.techtown.movieproject.FragmentCallback;
import org.techtown.movieproject.R;
import org.techtown.movieproject.api.CommentInfo;
import org.techtown.movieproject.api.CommentList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CommentAdapter extends BaseAdapter {
    private ArrayList<CommentInfo> items = new ArrayList<>();

    private ActivityCallback callback;
    Context context;

    public CommentAdapter(Context context) {
        this.context = context;

        if(context instanceof ActivityCallback) {
            callback = (ActivityCallback)context;
        }
    }

    public void addItem(CommentInfo item) {
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

        CommentInfo item = items.get(position);
        String time = calculateTime(item.getTimestamp() * 1000);

        view.setUserId(String.valueOf(item.getWriter()));
        view.setTime(time);
        view.setComment(item.getContents());
        view.setRatingBar(item.getRating());
        view.setRecommend(String.valueOf(item.getRecommend()));

        Button recommendButton = (Button)view.findViewById(R.id.recommendButton);
        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("log",item.getId() + "!");
                callback.increaseRecommend(item.getId(), item.getMovieId());
            }
        });

        return view;
    }

    public String calculateTime(long timeStamp) {
        Calendar oldCal = Calendar.getInstance();       // 한줄평 올린 날짜정보
        Date date = new Date(timeStamp);
        oldCal.setTime(date);
        Calendar cal = Calendar.getInstance();      // 지금 날짜정보

        int oldYear = oldCal.get(Calendar.YEAR);
        int oldDay = oldCal.get(Calendar.DAY_OF_YEAR);
        int oldHour = oldCal.get(Calendar.HOUR_OF_DAY);

        int curYear = cal.get(Calendar.YEAR);
        int curDay = cal.get(Calendar.DAY_OF_YEAR);
        int curHour = cal.get(Calendar.HOUR_OF_DAY);

        if(curYear == oldYear) {
            if((curDay - oldDay) <= 1) {
                if(curDay == oldDay) {
                    if(oldHour == curHour) {
                        return "방금전";
                    }
                    else {
                        return (curHour - oldHour) + "시간전";
                    }
                }
                else {
                    if(oldHour > curHour) {
                        return (curHour - oldHour) + "시간전";
                    }
                    else {
                        return (curDay - oldDay) + "일전";
                    }
                }
            }
            else {
                return (curDay - oldDay) + "일전";
            }
        }
        else {
            return (curYear - oldYear) + "년전";
        }
    }

    public ArrayList<CommentInfo> getItems() {
        return items;
    }

    public void setItems(ArrayList<CommentInfo> items) {
        this.items = items;
    }
}
