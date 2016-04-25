package brainbreaker.popularmovies.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import brainbreaker.popularmovies.Extras.Connectivity;
import brainbreaker.popularmovies.Listeners.ActivityCallback;
import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.R;

public class MainActivity extends AppCompatActivity implements ActivityCallback{
    public OverflowMenuClick listener;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container,new DescriptionActivityFragment())
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }

        // Check for the Internet Connection
        boolean connect = Connectivity.isOnline(this);
        if (!connect){
            showConnectionErrorDialog(this,"ERROR","You are not connected to Internet. Please check your connection and try again. Don't worry you can still browse offline saved movies and your favourites from Options Menu on top right.",null);
        }
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
                getSupportActionBar().setTitle("Top Rated Movies");
                return true;

            case R.id.action_fav:
                Toast.makeText(MainActivity.this, "Your favourites", Toast.LENGTH_SHORT).show();
                listener.onOverflowItemClicked("favourite");
                getSupportActionBar().setTitle("Your Favourites");
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

    @Override
    public void onMovieItemClicked(MovieClass movieClass) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putString("MovieTitle", movieClass.getTitle());
            args.putString("MovieDescription", movieClass.getDescription());
            args.putString("MovieRating", movieClass.getRating());
            args.putString("MovieRelease", movieClass.getRelease());
            args.putString("PosterURL", movieClass.getPoster());
            args.putString("MovieID", movieClass.getid());
            args.putBoolean("favStatus",movieClass.getFavstatus());

            DescriptionActivityFragment fragment = new DescriptionActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        }
        else {
            startActivity(movieClass);
        }
    }

    public interface OverflowMenuClick{
        public void onOverflowItemClicked(String item);
    }

    public void startActivity(MovieClass IntentMovie){
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("MovieTitle", IntentMovie.getTitle());
        intent.putExtra("MovieDescription", IntentMovie.getDescription());
        intent.putExtra("MovieRating", IntentMovie.getRating());
        intent.putExtra("MovieRelease", IntentMovie.getRelease());
        intent.putExtra("PosterURL", IntentMovie.getPoster());
        intent.putExtra("MovieID", IntentMovie.getid());
        intent.putExtra("favStatus",IntentMovie.getFavstatus());
        this.startActivity(intent);
    }

    public void showConnectionErrorDialog(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        listener.onOverflowItemClicked("favourite");
                        getSupportActionBar().setTitle("Your Favourites");
                    }
                });
        builder1.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
