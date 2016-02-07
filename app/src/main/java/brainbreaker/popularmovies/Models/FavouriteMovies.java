package brainbreaker.popularmovies.Models;

import java.util.ArrayList;

/**
 * Created by brainbreaker on 7/2/16.
 */
public class FavouriteMovies {
    MovieClass movie;
    private ArrayList<ReviewClass> reviews;

    public FavouriteMovies(MovieClass movie,
                      ArrayList<ReviewClass> reviews){
        this.movie = movie;
        this.reviews = reviews;
    }

    public MovieClass getMovie(){
        return movie;
    }
    public ArrayList<ReviewClass> getReviews(){
        return reviews;
    }
}
