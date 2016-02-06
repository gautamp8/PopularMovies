package brainbreaker.popularmovies.Models;

import java.util.ArrayList;

/**
 * Created by brainbreaker on 30/1/16.
 */
final class MovieClass {

    private String[] title= {""};
    private String[] poster= {""};
    private String[] poster2= {""};
    private String[] description = {""};
    private String[] rating = {""};
    private String[] release = {""};
    private String[] id = {""};

    public MovieClass(String[] title,
                      String[] poster,
                      String[] poster2,
                      String[] description,
                      String[] rating,
                      String[] release,
                      String[] id){
        this.title = title;
        this.poster = poster;
        this.poster2 = poster2;
        this.description = description;
        this.rating = rating;
        this.release = release;
        this.id = id;
    }

    public String[] getTitle(){
        return title;
    }
    public String[] getPoster(){
        return poster;
    }
    public String[] getPoster2(){
        return poster2;
    }
    public String[] getDescription(){
        return description;
    }
    public String[] getRating(){
        return rating;
    }
    public String[] getRelease(){
        return release;
    }
    public String[] getid(){
        return id;
    }
}
