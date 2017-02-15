package com.diana.mymovietheater;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

/**
 * Movie object class
 */
public class Movie implements Parcelable {
    private long id;
    private String movieTitle, moviePlot, moviePicURL;
    private float movieRating;
    private String imdbID;
    private Boolean checkBox;

    // Parcel constructor
    protected Movie(Parcel in) {
        id = in.readLong();
        movieTitle = in.readString();
        moviePlot = in.readString();
        moviePicURL = in.readString();
        movieRating = in.readFloat();
        imdbID = in.readString();
        checkBox=in.readInt()==1;

    }

   // IMDB id constructor
    public Movie(String imdbID, String movieTitle, String moviePicURL, Boolean checkBox) {
        this.movieTitle = movieTitle;
        this.moviePicURL = moviePicURL;
        this.imdbID = imdbID;
        this.checkBox=checkBox;
    }

    //DB id constructor
    public Movie(long id, String movieTitle, String moviePlot, String moviePicURL, float movieRating, Boolean checkBox) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.moviePlot = moviePlot;
        this.moviePicURL = moviePicURL;
        this.movieRating = movieRating;
        this.checkBox=checkBox;

    }



    @Override
    public String toString() {
        return
                "movieTitle: " + movieTitle + "\n" +
                        "moviePlot: " + moviePlot + "\n" +
                        "movieRating=" + movieRating +"\n" +
                        "url: " + moviePicURL
                ;
    }


// getters and setters

    public void setId(long id) {this.id = id;}

    public Boolean getCheckBox() {return checkBox;}

    public void setCheckBox(Boolean checkBox) {this.checkBox = checkBox;}

    public long getId() {
        return id;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public void setMoviePlot(String moviePlot) {
        this.moviePlot = moviePlot;
    }

    public String getMoviePicURL() {
        return moviePicURL;
    }

    public void setMoviePicURL(String moviePicURL) {
        this.moviePicURL = moviePicURL;
    }

    public float getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(float movieRating) {
        this.movieRating = movieRating;
    }

    //  Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeLong(id);
        dest.writeString(movieTitle);
        dest.writeString(moviePlot);
        dest.writeString(moviePicURL);
        dest.writeFloat(movieRating);
        dest.writeInt(checkBox ? 1 : 0);

    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
