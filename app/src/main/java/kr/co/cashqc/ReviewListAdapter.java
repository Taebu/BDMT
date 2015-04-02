
package kr.co.cashqc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 9..
 */
public class ReviewListAdapter extends BaseAdapter {

    public ReviewListAdapter(Context context, ArrayList<ReviewData> datas) {
        mDataList = datas;

        inflater = LayoutInflater.from(context);
    }

    private ArrayList<ReviewData> mDataList;

    private LayoutInflater inflater = null;

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        ViewHolder h;
        if (v == null) {
            h = new ViewHolder();
            v = inflater.inflate(R.layout.row_review, parent, false);

            h.ratingBar = (RatingBar) v.findViewById(R.id.row_review_rating);
            h.nick = (TextView) v.findViewById(R.id.row_review_nick);
            h.phone = (TextView) v.findViewById(R.id.row_review_phone);
            h.content = (TextView) v.findViewById(R.id.row_review_content);

            v.setTag(h);
        } else {
            h = (ViewHolder)v.getTag();
        }

        ReviewData item = (ReviewData) getItem(position);

        int score = item.getRating();

//        h.ratingBar.setNumStars(score);
        h.ratingBar.setRating(score);

        h.nick.setText(item.getNick());
        h.phone.setText(item.getPhone());
        h.content.setText(item.getContent());

        return v;
    }

    private class ViewHolder {

        private RatingBar ratingBar;
        private TextView nick, phone, content;

    }
}
