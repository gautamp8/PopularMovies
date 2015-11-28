package brainbreaker.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by brainbreaker on 21/11/15.
 */
public class CustomGrid extends BaseAdapter {

    private Context mContext;
    private final String[] moviename;
    private final String[] movieimageurl;

    public CustomGrid(Context c,String[] moviename,String[] movieimageurl ) {
        mContext = c;
        this.movieimageurl = movieimageurl;
        this.moviename = moviename;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return moviename.length;
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

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_item_layout, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            textView.setText(moviename[position]);
            Picasso.with(mContext)
                    .load(movieimageurl[position])
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imageView);
        } else {
            grid = (View) convertView;
        }

        return grid;
    }

}

