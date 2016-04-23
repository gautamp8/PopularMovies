package brainbreaker.popularmovies.Models;

import java.util.ArrayList;

/**
 * Created by brainbreaker on 30/1/16.
 */
public class MovieClass {

    private String title ;
    private String poster;
    private String description;
    private String rating;
    private String release;
    private String id;
    private boolean favStatus;    // A status so as to check whether the movie object is in favourite list or not
    public MovieClass(String title,
                      String poster,
                      String description,
                      String rating,
                      String release,
                      String id,
                      boolean favStatus){
        this.title = title;
        this.poster = poster;
        this.description = description;
        this.rating = rating;
        this.release = release;
        this.id = id;
        this.favStatus = favStatus;
    }

    public String getTitle(){
        return title;
    }
    public String getPoster(){
        return poster;
    }
    public String getDescription(){
        return description;
    }
    public String getRating(){
        return rating;
    }
    public String getRelease(){
        return release;
    }
    public String getid(){
        return id;
    }
    public boolean getFavstatus(){
        return favStatus;
    }
}
