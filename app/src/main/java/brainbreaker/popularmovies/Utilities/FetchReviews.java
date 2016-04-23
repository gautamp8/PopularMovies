package brainbreaker.popularmovies.Utilities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import brainbreaker.popularmovies.Constants.ApplicationConstants;
import brainbreaker.popularmovies.Listeners.ReviewListLoadedListener;
import brainbreaker.popularmovies.Mappers.JSONMapper;
import brainbreaker.popularmovies.Models.ReviewClass;
import brainbreaker.popularmovies.R;

/**
 * Created by brainbreaker on 6/2/16.
 */
public class FetchReviews extends AsyncTask<String, Void, ArrayList<ReviewClass>> {
    private String api_key = ApplicationConstants.apikey;
    private ReviewListLoadedListener listener;

    public FetchReviews(ReviewListLoadedListener listLoadedListener) {
        this.listener = listLoadedListener;
    }
    @Override
    protected ArrayList<ReviewClass> doInBackground(String... MovieID) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ReviewJsonStr = null;
        StringBuilder buffer = new StringBuilder();
        // CREATE A REQUEST TO TMDB DATABASE AND OPEN THE CONNECTION
        Uri Reviews = Uri.parse("http://api.themoviedb.org/3/movie").buildUpon()
                .appendPath(MovieID[0])
                .appendPath("reviews")
                .appendQueryParameter("api_key",api_key).build();

        Log.e("FETCH REVIEWS URL", Reviews.toString());

        try {
            urlConnection = (HttpURLConnection) new URL(Reviews.toString()).openConnection();
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
            ReviewJsonStr = buffer.toString();
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
                    Log.e("XYZ", "Error closing stream", e);
                }
            }
        }

        try {
            ArrayList<ReviewClass> reviewsList = JSONMapper.getMovieReviewsFromJson(ReviewJsonStr);
            return reviewsList;
        } catch (Exception e) {
            Log.e("XYZ", e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ArrayList<ReviewClass> reviewArrayList) {
        listener.reviewListLoaded(reviewArrayList);
    }
}