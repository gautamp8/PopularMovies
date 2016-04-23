package brainbreaker.popularmovies.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import brainbreaker.popularmovies.Models.ReviewClass;
import brainbreaker.popularmovies.R;

/**
 * Created by brainbreaker on 6/2/16.
 */
public class ReviewListAdapter extends BaseAdapter {
    ArrayList<ReviewClass> reviewList;
    Context context;
    public ReviewListAdapter(Context context, ArrayList<ReviewClass> reviewList){
        this.context = context;
        this.reviewList = reviewList;
    }
    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public ReviewClass getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewItemHolder item;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_list_item_layout, parent,false);
            item = new ViewItemHolder();
            item.ReviewAuthor = (TextView) convertView.findViewById(R.id.ReviewAuthor);
            item.ReviewContent = (TextView) convertView.findViewById(R.id.ReviewContent);

            convertView.setTag(item);
        }
        else {
            item = (ViewItemHolder) convertView.getTag();
        }
        ReviewClass currentReview = reviewList.get(position);
        item.ReviewAuthor.setText(currentReview.getAuthor());
        item.ReviewContent.setText(currentReview.getContent());

        return convertView;
    }
    private class ViewItemHolder {
        TextView ReviewAuthor;
        TextView ReviewContent;
    }
}