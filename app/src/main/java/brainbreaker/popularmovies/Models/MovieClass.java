package brainbreaker.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by brainbreaker on 30/1/16.
 */
public class MovieClass{

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

    // Parcelling part
    public MovieClass(Parcel in){
        String[] data = new String[6];
        Boolean[] bool = new Boolean[1];

        in.readStringArray(data);
        this.title = data[0];
        this.poster = data[1];
        this.description = data[2];
        this.rating = data[3];
        this.release = data[4];
        this.id = data[5];
        this.favStatus = bool[0];
    }
}
