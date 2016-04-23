package brainbreaker.popularmovies.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import brainbreaker.popularmovies.Listeners.MovieListLoadedListener;
import brainbreaker.popularmovies.Models.FavouriteMovies;
import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.MyApplication;
import brainbreaker.popularmovies.R;
import brainbreaker.popularmovies.Utilities.FetchMovieList;

public class MainActivity extends AppCompatActivity{

    OverflowMenuClick listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_popular :
                Toast.makeText(MainActivity.this, "Sorted By Popularity", Toast.LENGTH_SHORT).show();
                listener.onOverflowItemClicked("popularity");
                return true;

            case R.id.action_rating:
                Toast.makeText(MainActivity.this, "Sorted By Rating", Toast.LENGTH_SHORT).show();
                listener.onOverflowItemClicked("rating");
                return true;

            case R.id.action_fav:
                Toast.makeText(MainActivity.this, "Your favourites", Toast.LENGTH_SHORT).show();
                listener.onOverflowItemClicked("favourite");
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public interface OverflowMenuClick{
        public void onOverflowItemClicked(String item);
    }
}
