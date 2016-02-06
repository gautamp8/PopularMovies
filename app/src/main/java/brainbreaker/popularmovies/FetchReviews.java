package brainbreaker.popularmovies;

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

import brainbreaker.popularmovies.Adapters.CustomGrid;
import brainbreaker.popularmovies.Models.ReviewClass;

/**
 * Created by brainbreaker on 6/2/16.
 */
public class FetchReviews extends AsyncTask<String, Void, ReviewClass> {
    private Context mContext;
    private ListView Reviewlist;

    public FetchReviews(Context context, ListView Reviewlist) {
        mContext = context;
        this.Reviewlist = Reviewlist;
    }
    @Override
    protected ReviewClass doInBackground(String... MovieID) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ReviewJsonStr = null;
        StringBuilder buffer = new StringBuilder();
        // CREATE A REQUEST TO TMDB DATABASE AND OPEN THE CONNECTION
        String api_key = mContext.getResources().getString(R.string.api_key);
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
            ReviewClass ReviewsObject = getMovieReviewsFromJson(ReviewJsonStr);
            Log.e("YoutubeURL", ReviewsObject.getContent().toString());
            return ReviewsObject;
        } catch (Exception e) {
            Log.e("XYZ", e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ReviewClass reviewClass) {
        try {
             CustomGrid.ReviewListAdapter adapter = new CustomGrid.ReviewListAdapter(mContext,reviewClass.getAuthor(),reviewClass.getContent(),reviewClass.getUrl());
             Reviewlist.setAdapter(adapter);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private ReviewClass getMovieReviewsFromJson(String movieReviewsJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_results = "results";
        final String TMDB_author = "author";
        final String TMDB_content = "content";
        final String TMDB_url = "url";


        JSONObject moviesJson = new JSONObject(movieReviewsJsonStr);
        JSONArray resultArray = moviesJson.getJSONArray(TMDB_results);

        String[] resultauthorStrs = new String[resultArray.length()];
        String[] resultcontentStrs = new String[resultArray.length()];
        String[] resulturlStrs = new String[resultArray.length()];

        for (int i = 0; i < resultArray.length(); i++) {

            String author;
            String content;
            String url;

            // Get the JSON object in which movie title is there
            JSONObject moviereview = resultArray.getJSONObject(i);
            author = moviereview.get(TMDB_author).toString();
            content = moviereview.get(TMDB_content).toString();
            url = moviereview.get(TMDB_url).toString();

            resultauthorStrs[i] = author;
            resultcontentStrs[i] = content;
            resulturlStrs[i] = url;
        }

        return new ReviewClass(resultauthorStrs, resultcontentStrs, resulturlStrs);
    }

}