package brainbreaker.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.Models.ReviewClass;
import brainbreaker.popularmovies.R;

/**
 * Created by brainbreaker on 21/11/15.
 */
public class CustomGrid extends BaseAdapter {

    private Context mContext;
    private ArrayList<MovieClass> movielist;
    public CustomGrid(Context c, ArrayList<MovieClass> movielist) {
        mContext = c;
        this.movielist = movielist;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return movielist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=null;
        if (convertView == null) {

            grid = inflater.inflate(R.layout.grid_item_layout, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);

            MovieClass CurrentMovie = movielist.get(position);
            textView.setText(CurrentMovie.getTitle());
            if (imageView != null) {
                Picasso.with(mContext)
                        .load(CurrentMovie.getPoster())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(imageView);
            }
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
