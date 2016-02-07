package brainbreaker.popularmovies;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;

import brainbreaker.popularmovies.Adapters.ReviewListAdapter;
import brainbreaker.popularmovies.Models.FavouriteMovies;
import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.Models.ReviewClass;


public class DescriptionActivity extends ActionBarActivity {
    String MovieID;
    ImageView VideoImageView;
    ImageView PlayButton;
    ListView ReviewList;
    public static ArrayList<String> FavouriteMovieNames = new ArrayList<>();
    ProgressDialog progress;
    public static ArrayList<FavouriteMovies> FavouriteList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent intent = getIntent();
        final String movietitle = intent.getStringExtra("MovieTitle");
        final String PosterURL = intent.getStringExtra("PosterURL");
        final String moviedescription = intent.getStringExtra("MovieDescription");
        final String movierating = intent.getStringExtra("MovieRating");
        final String movierelease = intent.getStringExtra("MovieRelease");

        MovieID = intent.getStringExtra("MovieID");

        /** CHANGING THE TITLE OF ACTION BAR AND ENABLING THE BACK BUTTON**/
        getSupportActionBar().setTitle(movietitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView description = (TextView) findViewById(R.id.description);
        description.setText(moviedescription);

        final TextView rating = (TextView) findViewById(R.id.rating);
        rating.setText("Rating: "+movierating + "/10.0");

        final TextView release = (TextView) findViewById(R.id.release);
        release.setText("Release Date: "+movierelease);

        PlayButton = (ImageView) findViewById(R.id.VideoPreviewPlayButton);
        PlayButton.setVisibility(View.GONE);

        ReviewList = (ListView) findViewById(R.id.ReviewlistView);
        VideoImageView = (ImageView) findViewById(R.id.poster);

        final ImageButton fav = (ImageButton) findViewById(R.id.Favourite);
        //If a movie name is added to favourites show the highlighted star(Get The fav movie list from Shared Preferences).
        for (int i = 0; i<GetfavMovieNameList(this).size(); i++)
        {
            if (GetfavMovieNameList(this).get(i).equals(movietitle)){
                fav.setBackgroundResource(android.R.drawable.star_big_on);
                FavouriteMovies favouriteMovie = new FavouriteMovies(RetrieveFavList(this).get(i).getMovie(),RetrieveFavList(this).get(i).getReviews());
                ReviewListAdapter adapter = new ReviewListAdapter(this,favouriteMovie.getReviews());
                ReviewList.setAdapter(adapter);
            }
        }
        // SHOW A PROGRESS DIALOG
        progress = new ProgressDialog(this);
        progress.setTitle("Loading Video...");
        progress.setCancelable(false);
        progress.show();

        // Now we are fetching video key
        FetchVideo fetchVideo = new FetchVideo();
        fetchVideo.execute();

        FetchReviews fetchReviews = new FetchReviews(this,ReviewList);
        fetchReviews.execute(MovieID);

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav.setBackgroundResource(android.R.drawable.star_big_on);
                Toast.makeText(DescriptionActivity.this,"Movie Added as favourite",Toast.LENGTH_LONG).show();
                ArrayList<ReviewClass> reviewlist = new ArrayList<ReviewClass>();
                int len = ReviewList.getCount();
                for (int i = 0; i < len; i++) {
                    ReviewClass currentReview = (ReviewClass) ReviewList.getAdapter().getItem(i);
                    reviewlist.add(currentReview);
                }
                MovieClass movieObject = new MovieClass(movietitle, PosterURL, moviedescription, movierating, movierelease,MovieID,true);
                FavouriteList.add(new FavouriteMovies(movieObject, reviewlist));
                FavouriteMovieNames.add(movietitle);
                SaveFavList(DescriptionActivity.this,FavouriteList);
                SetfavMovieName(DescriptionActivity.this,FavouriteMovieNames);
            }
        });

    }

    public class FetchVideo extends AsyncTask<Void, Void, String> {

        private final String LOG_TAG = FetchVideo.class.getSimpleName();
        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String VideoJsonStr = null;
            StringBuilder buffer = new StringBuilder();
            String api_key = getResources().getString(R.string.api_key);
            Uri VideoURL = Uri.parse("http://api.themoviedb.org/3/movie/").buildUpon()
                    .appendPath(MovieID)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", api_key).build();
            // CREATE A REQUEST TO TMDB DATABASE AND OPEN THE CONNECTION
            try {
                urlConnection = (HttpURLConnection) new URL(VideoURL.toString()).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                VideoJsonStr = buffer.toString();
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                String YoutubeURL = getMovieVideoKeyFromJson(VideoJsonStr);
                Log.e("YoutubeURL", YoutubeURL);
                return YoutubeURL;
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String videokey) {
            try {
                // Sample Thumbnail URL: http://img.youtube.com/vi/VIDEO_ID/default.jpg

                Uri VideoThumbnailURL = Uri.parse("http://img.youtube.com/vi").buildUpon()
                        .appendPath(videokey)
                        .appendPath("default.jpg").build();

                Log.e("VideoThumbnailURL",VideoThumbnailURL.toString());

                Picasso.with(DescriptionActivity.this)
                        .load(VideoThumbnailURL)
                        .resize(500,200)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(VideoImageView);


                if(progress.isShowing()){
                    progress.dismiss();
                }
                PlayButton.setVisibility(View.VISIBLE);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            // Playing the video in Youtube App.
            PlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri VideoURL = Uri.parse("https://www.youtube.com/watch").buildUpon()
                            .appendQueryParameter("v",videokey).build();
                    Log.e("BUILT URL", VideoURL.toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, VideoURL));
                }
            });
        }
    }

    private String getMovieVideoKeyFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_results = "results";
        final String TMDB_video_key = "key";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultArray = moviesJson.getJSONArray(TMDB_results);
        String videoKey;
        JSONObject movieResults = resultArray.getJSONObject(0);
        videoKey = movieResults.get(TMDB_video_key).toString();
        return videoKey;
    }

    /** WHEN CLICKED ON BACK BUTTON **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // BACK CLICKED. GO TO HOME.
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //FINISH THE CURRENT ACTIVITY
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void SaveFavList(Context context, ArrayList<FavouriteMovies> FavList) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(FavList);
        prefsEditor.putString("FavMovieList", json);
        prefsEditor.apply();
    }

    public static ArrayList<FavouriteMovies> RetrieveFavList(Context context) {
        ArrayList<FavouriteMovies> FavList;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("FavMovieList", "");
        if (json.isEmpty()) {
            FavList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<FavouriteMovies>>() {
            }.getType();
            FavList = gson.fromJson(json, type);
        }
        return FavList;
    }

    public static void SetfavMovieName(Context context, ArrayList<String> favMovie) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favMovie);
        prefsEditor.putString("FavMovieName", json);
        prefsEditor.apply();
    }

    public static ArrayList<String> GetfavMovieNameList(Context context) {
        ArrayList<String> favMovieList;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("FavMovieName", "");
        if (json.isEmpty()) {
            favMovieList = new ArrayList<String>();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();

            favMovieList = gson.fromJson(json, type);
        }
        return favMovieList;
    }
}
