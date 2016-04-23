package brainbreaker.popularmovies.Utilities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import brainbreaker.popularmovies.Constants.ApplicationConstants;
import brainbreaker.popularmovies.Listeners.FetchVideoListener;
import brainbreaker.popularmovies.Mappers.JSONMapper;
import brainbreaker.popularmovies.R;

/**
 * Created by root on 14/4/16.
 */
public class FetchVideo extends AsyncTask<String, Void, String> {
    private FetchVideoListener listener;
    private final String LOG_TAG = FetchVideo.class.getSimpleName();

    public FetchVideo(FetchVideoListener fetchVideoListener){
        this.listener = fetchVideoListener;
    }

    @Override
    protected String doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieID = params[0];
        // Will contain the raw JSON response as a string.
        String VideoJsonStr = null;
        StringBuilder buffer = new StringBuilder();
        String api_key = ApplicationConstants.apikey;
        Uri VideoURL = Uri.parse("http://api.themoviedb.org/3/movie/").buildUpon()
                .appendPath(movieID)
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
            String YoutubeURL = JSONMapper.getMovieVideoKeyFromJson(VideoJsonStr);
            Log.e("YoutubeURL", YoutubeURL);
            return YoutubeURL;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String videoKey) {
        listener.fetchVideoListener(videoKey);
    }
}
