package brainbreaker.popularmovies.Models;

/**
 * Created by brainbreaker on 6/2/16.
 */
public class ReviewClass {
    private String author;
    private String content;
    private String url;


    public ReviewClass(String author,
                      String content,
                      String url
                      ){
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor(){
        return author;
    }
    public String getContent(){
        return content;
    }
    public String getUrl(){
        return url;
    }

}
