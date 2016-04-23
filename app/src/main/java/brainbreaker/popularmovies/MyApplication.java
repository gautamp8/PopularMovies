package brainbreaker.popularmovies;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import brainbreaker.popularmovies.Models.FavouriteMovies;
import brainbreaker.popularmovies.Models.MovieClass;

/**
 * Created by root on 14/4/16.
 */
public class MyApplication extends Application{

    public static void saveMovieList(Context context, ArrayList<MovieClass> MovieList) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MovieList);
        prefsEditor.putString("MovieList", json);
        prefsEditor.apply();
    }

    public static ArrayList<MovieClass> retrieveMovieList(Context context) {
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

    public static void saveFavList(Context context, ArrayList<FavouriteMovies> FavList) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(FavList);
        prefsEditor.putString("FavMovieList", json);
        prefsEditor.apply();
    }

    public static ArrayList<FavouriteMovies> retrieveFavList(Context context) {
        ArrayList<FavouriteMovies> favList;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("FavMovieList", "");
        if (json.isEmpty()) {
            favList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<FavouriteMovies>>() {
            }.getType();
            favList = gson.fromJson(json, type);
        }
        return favList;
    }

    public static void setfavMovieName(Context context, ArrayList<String> favMovie) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favMovie);
        prefsEditor.putString("FavMovieName", json);
        prefsEditor.apply();
    }

    public static ArrayList<String> getfavMovieNameList(Context context) {
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
