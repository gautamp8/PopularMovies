package brainbreaker.popularmovies.Utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import brainbreaker.popularmovies.Constants.ApplicationConstants;
import brainbreaker.popularmovies.Listeners.MovieListLoadedListener;
import brainbreaker.popularmovies.Mappers.JSONMapper;
import brainbreaker.popularmovies.Models.MovieClass;

/**
 * Created by root on 14/4/16.
 */
public class FetchMovieList extends AsyncTask<String, Void, ArrayList<MovieClass>> {
    private MovieListLoadedListener listener;
    private String apikey = ApplicationConstants.apikey;
    private final String LOG_TAG = FetchMovieList.class.getSimpleName();

    public FetchMovieList(MovieListLoadedListener movieListLoadedListener){
        this.listener = movieListLoadedListener;
    }
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

            if (sort_by.equals("kids")) {
                sort_by = "vote_average.desc";
                builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                        .appendQueryParameter("certification_country", "US")
                        .appendQueryParameter("certification", "R")
                        .appendQueryParameter("sort_by", sort_by)
                        .appendQueryParameter("api_key", apikey).build();
            } else if (sort_by.equals("popularity")) {
                sort_by = "popularity.desc";
                builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                        .appendQueryParameter("sort_by", sort_by)
                        .appendQueryParameter("api_key", apikey).build();
            } else {
                sort_by = "vote_average.desc";
                builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie?").buildUpon()
                        .appendQueryParameter("certification_country", "US")
                        .appendQueryParameter("sort_by", sort_by)
                        .appendQueryParameter("api_key", apikey).build();
            }
            // Construct the URL to fetch the movie list
            URL url = new URL(builtUri.toString());
            System.out.println("URL IS- " + url);
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
            return JSONMapper.getMovieDataFromJson(forecastJsonStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(final ArrayList<MovieClass> myresultMovieList) {
      if (listener!=null){
          listener.movieListLoadedListener(myresultMovieList);
      }
    }
}