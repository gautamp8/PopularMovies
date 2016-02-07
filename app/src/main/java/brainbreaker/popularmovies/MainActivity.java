package brainbreaker.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import brainbreaker.popularmovies.Adapters.CustomGrid;
import brainbreaker.popularmovies.Models.FavouriteMovies;
import brainbreaker.popularmovies.Models.MovieClass;


public class MainActivity extends ActionBarActivity {
    GridView moviegrid;
    CustomGrid adapter;
    String apikey;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviegrid=(GridView)findViewById(R.id.moviegrid);
        apikey= getResources().getString(R.string.api_key);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // SHOW A PROGRESS DIALOG
        progress = new ProgressDialog(this);
        progress.setTitle("Loading Movies...");
        progress.setCancelable(false);
        progress.show();
        FetchMovieList fetchpopularMovieList = new FetchMovieList();
        fetchpopularMovieList.execute("popularity");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_popular :
                Toast.makeText(MainActivity.this, "You wanted the list to be sort by popularity ", Toast.LENGTH_SHORT).show();
                FetchMovieList fetchpopularMovieList = new FetchMovieList();
                fetchpopularMovieList.execute("popularity");
                return true;
            case R.id.action_rating:
                Toast.makeText(MainActivity.this, "You wanted the list to be sort by rating ", Toast.LENGTH_SHORT).show();
                FetchMovieList fetchratedMovieList = new FetchMovieList();
                fetchratedMovieList.execute("rating");
                return true;
            case R.id.action_fav:
                Toast.makeText(MainActivity.this, "Your favourites", Toast.LENGTH_SHORT).show();

                ArrayList<FavouriteMovies> FavMovieList = DescriptionActivity.RetrieveFavList(this);
                if (FavMovieList!=null) {
                    ArrayList<MovieClass> movieList = new ArrayList<>();
                    for (int i = 0; i < FavMovieList.size(); i++) {
                        movieList.add(FavMovieList.get(i).getMovie());
                    }
                    CustomGrid adapter = new CustomGrid(this, movieList);
                    moviegrid.setAdapter(adapter);
                }
                else {
                    Toast.makeText(MainActivity.this, "You don't have any favourites yet", Toast.LENGTH_SHORT).show();
                    FetchMovieList fetchMovieList = new FetchMovieList();
                    fetchMovieList.execute("popularity");
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public class FetchMovieList extends AsyncTask<String, Void, ArrayList<MovieClass>>{

        private final String LOG_TAG = FetchMovieList.class.getSimpleName();
        @Override
        protected ArrayList<MovieClass> doInBackground(String... sort) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String sort_by = sort[0];
            Uri builtUri;


            try {

                if(sort_by.equals("kids")){
                    sort_by = "vote_average.desc";
                    builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                            .appendQueryParameter("certification_country","US")
                            .appendQueryParameter("certification","R")
                            .appendQueryParameter("sort_by", sort_by)
                            .appendQueryParameter("api_key", apikey).build();
                }
                else if(sort_by.equals("popularity")){
                    sort_by = "popularity.desc";
                    builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                            .appendQueryParameter("sort_by", sort_by)
                            .appendQueryParameter("api_key", apikey).build();
                }

                else{
                     sort_by = "vote_average.desc";
                     builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                            .appendQueryParameter("certification_country","US")
                            .appendQueryParameter("sort_by", sort_by)
                            .appendQueryParameter("api_key", apikey).build();
                }
                // Construct the URL to fetch the movie list
                URL url = new URL(builtUri.toString());
                System.out.println("URL IS- "+ url);
                // CREATE A REQUEST TO TMDB DATABASE AND OPEN THE CONNECTION
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
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
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
                ArrayList<MovieClass> myMovieClassList = getMovieDataFromJson(forecastJsonStr);
                return myMovieClassList;
            } catch (Exception e){
                Log.e(LOG_TAG,e.getMessage(),e);
                e.printStackTrace();
                return null;
            }
        }

        private ArrayList<MovieClass> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_results = "results";
            final String TMDB_title = "original_title";
            final String TMDB_poster = "poster_path";
            final String TMDB_description = "overview";
            final String TMDB_rating = "vote_average";
            final String TMDB_release = "release_date";
            final String TMDB_Movie_id = "id";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultArray = moviesJson.getJSONArray(TMDB_results);

            ArrayList<MovieClass> movieList = new ArrayList<>();
            for (int i = 0; i < resultArray.length(); i++) {

                String moviename;
                String movieposter;
                String description;
                String rating;
                String release;
                String movie_id;

                // Get the JSON object in which movie title is there
                JSONObject movietitle = resultArray.getJSONObject(i);
                moviename = movietitle.get(TMDB_title).toString();
                movieposter = movietitle.get(TMDB_poster).toString();
                description = movietitle.get(TMDB_description).toString();
                rating = movietitle.get(TMDB_rating).toString();
                release = movietitle.get(TMDB_release).toString();
                movie_id = movietitle.get(TMDB_Movie_id).toString();


                //Poster URL Builder
                Uri posterbuiltUri = Uri.parse("http://image.tmdb.org/t/p/w150/").buildUpon()
                        .appendPath(movieposter.replace("/","")).build();

                String PosterUrl = posterbuiltUri.toString();

                MovieClass movieObject = new MovieClass(moviename,PosterUrl,description,rating,release,movie_id,false);
                movieList.add(movieObject);
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(final ArrayList<MovieClass> myresultMovieList) {

            try {
                adapter = new CustomGrid(MainActivity.this,myresultMovieList);
                moviegrid.setAdapter(adapter);
                if(progress.isShowing()){
                    progress.dismiss();
                }
                //Saving the Movie Array List to SharedPrefs so that it is available for offline access also.
                SaveMovieList(MainActivity.this,myresultMovieList);
            }
            catch (NullPointerException e){
                boolean connect = isOnline(MainActivity.this);
                if (!connect){
                    Toast.makeText(MainActivity.this,"Unable to connect to Internet. Please check your connection.",Toast.LENGTH_SHORT).show();
                    if (RetrieveMovieList(MainActivity.this)!=null){
                        adapter = new CustomGrid(MainActivity.this,RetrieveMovieList(MainActivity.this));
                        moviegrid.setAdapter(adapter);
                        if(progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }
            }
                moviegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (myresultMovieList != null) {
                        MovieClass IntentMovie = myresultMovieList.get(position);
                        // Passing all the required data through this activity to Description(Movie Details) Activity
                        Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
                        intent.putExtra("MovieTitle", IntentMovie.getTitle());
                        intent.putExtra("MovieDescription", IntentMovie.getDescription());
                        intent.putExtra("MovieRating", IntentMovie.getRating());
                        intent.putExtra("MovieRelease", IntentMovie.getRelease());
                        intent.putExtra("PosterURL", IntentMovie.getPoster());
                        intent.putExtra("MovieID", IntentMovie.getid());
                        intent.putExtra("favStatus",IntentMovie.getFavstatus());
                        MainActivity.this.startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Some problem was there. Please check your Internet Connection.",Toast.LENGTH_LONG).show();
                    }
                }

            });
            }
        }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static void SaveMovieList(Context context, ArrayList<MovieClass> MovieList) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MovieList);
        prefsEditor.putString("MovieList", json);
        prefsEditor.apply();
    }

    public static ArrayList<MovieClass> RetrieveMovieList(Context context) {
        ArrayList<MovieClass> MovieList;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("MovieList", "");
        if (json.isEmpty()) {
            MovieList = new ArrayList<MovieClass>();
        } else {
            Type type = new TypeToken<ArrayList<MovieClass>>() {
            }.getType();
            MovieList = gson.fromJson(json, type);
        }
        return MovieList;
    }
}


