package com.diana.mymovietheater;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * An adapter for the Item Movie layout, for the MainActivity view list.
 */
public class ItemMovieAdapter extends ArrayAdapter<Movie> {


    public ItemMovieAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie_layout, null);
        }

        TextView textTitle = (TextView) convertView.findViewById(R.id.textView_item_movie_titele);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView_item_movie_Pic);
        TextView plot = (TextView) convertView.findViewById(R.id.textView_item_movie_Plot);
        Movie temp = getItem(position);
        textTitle.setText("Title: " + temp.getMovieTitle());
        plot.setText("Plot: " + temp.getMoviePlot());
        Boolean checkBox =temp.getCheckBox();

        // insert image resource according to the status of the check box
        if(checkBox==true) {
            image.setImageResource(R.drawable.checked);
        }
        else {
            image.setImageResource(R.drawable.not_checked);
        }
        return convertView;
    }
}