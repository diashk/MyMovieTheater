package com.diana.mymovietheater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An adapter for the  Movie layout, for the web_search_movie_layout view list.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {


    public MovieAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.web_search_movie_layout, null);
        }


        TextView textTitle = (TextView) convertView.findViewById(R.id.textView_movie_title_adapter);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView_movie_adapter);
        TextView imdbID = (TextView) convertView.findViewById(R.id.textView_imdb_id);


        Movie temp = getItem(position);
        textTitle.setText("Title: " + temp.getMovieTitle());
        imdbID.setText(temp.getImdbID());
        return convertView;
    }
}
