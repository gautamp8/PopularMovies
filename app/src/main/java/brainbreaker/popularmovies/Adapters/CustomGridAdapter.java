package brainbreaker.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import brainbreaker.popularmovies.Activities.DescriptionActivity;
import brainbreaker.popularmovies.Listeners.ActivityCallback;
import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.Models.ReviewClass;
import brainbreaker.popularmovies.R;

/**
 * Created by brainbreaker on 21/11/15.
 */
public class CustomGridAdapter extends RecyclerView.Adapter<CustomGridAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MovieClass> movielist;
    private ActivityCallback onClickListener;
    public CustomGridAdapter (Context c, ArrayList<MovieClass> movielist) {
        mContext = c;
        this.onClickListener = (ActivityCallback) c;
        this.movielist = movielist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MovieClass CurrentMovie = movielist.get(position);
        holder.titleTextView.setText(CurrentMovie.getTitle());
        if (holder.gridImageView!= null) {
            Picasso.with(mContext)
                    .load(CurrentMovie.getPoster())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.gridImageView);
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movielist != null) {
                    MovieClass IntentMovie = movielist.get(position);
                    onClickListener.onMovieItemClicked(IntentMovie);
                }
                else{
                    Toast.makeText(mContext,"Some problem was there. Please check your Internet Connection.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (movielist!=null){
            count = movielist.size();
        }
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView gridImageView;
        public TextView titleTextView;
        public View rootView;
        public ViewHolder(View itemView) {
            super(itemView);
            gridImageView = (ImageView)itemView.findViewById(R.id.grid_image);
            titleTextView = (TextView)itemView.findViewById(R.id.grid_text);
            rootView = itemView;
        }
    }
}
