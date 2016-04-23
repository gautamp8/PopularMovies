package brainbreaker.popularmovies.Listeners;

import java.util.ArrayList;

import brainbreaker.popularmovies.Models.ReviewClass;

/**
 * Created by root on 14/4/16.
 */
public interface ReviewListLoadedListener {
    public void reviewListLoaded(ArrayList<ReviewClass> reviewList);
}
