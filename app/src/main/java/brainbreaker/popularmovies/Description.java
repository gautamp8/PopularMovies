package brainbreaker.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class Description extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent intent = getIntent();
        String movietitle = intent.getStringExtra("MovieTitle");
        String moviedescription = intent.getStringExtra("MovieDescription");
        String movierating = intent.getStringExtra("MovieRating");
        String movierelease = intent.getStringExtra("MovieRelease");
        String posterurl = intent.getStringExtra("PosterURL");

        getSupportActionBar().setTitle(movietitle);
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(moviedescription);
        TextView rating = (TextView) findViewById(R.id.rating);
        rating.setText(movierating);
        TextView release = (TextView) findViewById(R.id.release);
        release.setText(movierelease);

        ImageView poster = (ImageView) findViewById(R.id.poster);
        Picasso.with(this)
                .load(posterurl)
                .placeholder(R.mipmap.ic_launcher)
                .into(poster);

    }
}
