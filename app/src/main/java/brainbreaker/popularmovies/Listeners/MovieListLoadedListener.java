package brainbreaker.popularmovies.Listeners;

import java.util.ArrayList;

import brainbreaker.popularmovies.Models.MovieClass;

/**
 * Created by root on 14/4/16.
 */
public interface MovieListLoadedListener {
    public void movieListLoadedListener(ArrayList<MovieClass> movieList);
}
