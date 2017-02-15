package com.diana.mymovietheater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.diana.mymovietheater.Enums.ACTIONS;

import java.util.ArrayList;

/**
 * This class handles the data base and its methods
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // we create final names for the data base table and columns names.
    private static final String TABLE_NAME = "movies", COL_ID = "movie_id",COL_WATCHED = "movie_watched", COL_TITLE = "movie_title", COL_PLOT = "movie_plot", COL_PIC_URL = "movie_pic_url", COL_RATING = "movie_rating";
    public MovieDBHelper(Context context) {
        super(context, "moviesDB", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
    @Override
    public void onCreate(SQLiteDatabase moviesLiteDatabase) {

        // We create an string and we format it, then put it in the data base.
        String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s TEXT) ", TABLE_NAME, COL_ID, COL_TITLE, COL_PLOT, COL_PIC_URL, COL_RATING,COL_WATCHED);
        moviesLiteDatabase.execSQL(sql);
    }

    // The commands methods.

    public ArrayList<Movie> getAllMovies() {

        ArrayList<Movie> moviesList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_RATING);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(COL_ID));
            String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
            String plot = cursor.getString(cursor.getColumnIndex(COL_PLOT));
            String picURL = cursor.getString(cursor.getColumnIndex(COL_PIC_URL));
            float rating = cursor.getFloat(cursor.getColumnIndex(COL_RATING));
            Boolean watched= Boolean.valueOf(cursor.getString(cursor.getColumnIndex(COL_WATCHED)));
            moviesList.add(new Movie(id, title, plot, picURL, rating,watched));
        }

        db.close();
        return moviesList;
    }

    public void manageMovie(Movie movie, ACTIONS action) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, movie.getMovieTitle());
        values.put(COL_PLOT, movie.getMoviePlot());
        values.put(COL_PIC_URL, movie.getMoviePicURL());
        values.put(COL_RATING, movie.getMovieRating());
        values.put(COL_WATCHED,String.valueOf(movie.getCheckBox()));
        SQLiteDatabase db = getWritableDatabase();

        if (action==ACTIONS.ADD)
        {db.insert(TABLE_NAME, null, values);}

        if (action==ACTIONS.EDIT)
        { db.update(TABLE_NAME, values, COL_ID + "=" + movie.getId(), null);}

        db.close();
    }


    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=" + movie.getId(), null);
        db.close();
    }

    public void deleteAllMovies() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }


}
