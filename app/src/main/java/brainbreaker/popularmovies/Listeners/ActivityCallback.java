package brainbreaker.popularmovies.Listeners;

import brainbreaker.popularmovies.Models.MovieClass;

/**
 * Created by root on 23/4/16.
 */
public interface ActivityCallback {
    public void onMovieItemClicked(MovieClass movieClass);
}
