package brainbreaker.popularmovies;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DescriptionActivity extends ActionBarActivity {
    String MovieID;
    ImageView VideoImageView;
    ImageView PlayButton;
    ListView ReviewList;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent intent = getIntent();
        String movietitle = intent.getStringExtra("MovieTitle");
        String moviedescription = intent.getStringExtra("MovieDescription");
        String movierating = intent.getStringExtra("MovieRating");
        String movierelease = intent.getStringExtra("MovieRelease");
        MovieID = intent.getStringExtra("MovieID");

        /** CHANGING THE TITLE OF ACTION BAR AND ENABLING THE BACK BUTTON**/
        getSupportActionBar().setTitle(movietitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(moviedescription);

        TextView rating = (TextView) findViewById(R.id.rating);
        rating.setText("Rating: "+movierating + "/10.0");

        TextView release = (TextView) findViewById(R.id.release);
        release.setText("Release Date: "+movierelease);

        PlayButton = (ImageView) findViewById(R.id.VideoPreviewPlayButton);
        PlayButton.setVisibility(View.GONE);

        final ImageButton fav = (ImageButton) findViewById(R.id.Favourite);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav.setBackgroundResource(android.R.drawable.star_big_on);
                Toast.makeText(DescriptionActivity.this,"Movie Added as favourite",Toast.LENGTH_LONG).show();
            }
        });

        ReviewList = (ListView) findViewById(R.id.ReviewlistView);
        VideoImageView = (ImageView) findViewById(R.id.poster);

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
}
