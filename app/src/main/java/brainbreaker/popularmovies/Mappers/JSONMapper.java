package brainbreaker.popularmovies.Mappers;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.Models.ReviewClass;

/**
 * Created by root on 14/4/16.
 */
public class JSONMapper {

    public static ArrayList<MovieClass> getMovieDataFromJson(String moviesJsonStr)
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
                    .appendPath(movieposter.replace("/", "")).build();

            String PosterUrl = posterbuiltUri.toString();

            MovieClass movieObject = new MovieClass(moviename, PosterUrl, description, rating, release, movie_id, false);
            movieList.add(movieObject);
        }
        return movieList;

    }

    public static ArrayList<ReviewClass> getMovieReviewsFromJson(String movieReviewsJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_results = "results";
        final String TMDB_author = "author";
        final String TMDB_content = "content";
        final String TMDB_url = "url";


        JSONObject moviesJson = new JSONObject(movieReviewsJsonStr);
        JSONArray resultArray = moviesJson.getJSONArray(TMDB_results);

        ArrayList<ReviewClass> reviewList = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {

            String author;
            String content;
            String url;

            // Get the JSON object in which movie title is there
            JSONObject moviereview = resultArray.getJSONObject(i);
            author = moviereview.get(TMDB_author).toString();
            content = moviereview.get(TMDB_content).toString();
            url = moviereview.get(TMDB_url).toString();

            ReviewClass review = new ReviewClass(author,content,url);
            reviewList.add(review);
        }

        return reviewList;
    }

    public static String getMovieVideoKeyFromJson(String moviesJsonStr)
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
}
