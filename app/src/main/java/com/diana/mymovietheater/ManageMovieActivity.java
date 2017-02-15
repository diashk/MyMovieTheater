package com.diana.mymovietheater;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;


import com.diana.mymovietheater.Enums.ACTIONS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ManageMovieActivity extends AppCompatActivity implements View.OnClickListener {
    private static ACTIONS ACTION_CASE;
    private EditText titleText, plotText, urlText;
    private RatingBar ratingBar;
    private String url;
    private MovieDBHelper helper = new MovieDBHelper(this);
    private Movie movie;
    private long movieId;
    private ImageView imageView;
    private Bitmap mBitmap;
    private CheckBox mCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_movie);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox_watched);
        titleText = (EditText) (findViewById(R.id.editText_movie_title));
        plotText = (EditText) (findViewById(R.id.editText_movie_plot));
        urlText = (EditText) (findViewById(R.id.editText_pic_url));
        ratingBar = (RatingBar) (findViewById(R.id.ratingBar_movie_rating));
        urlText.setMaxLines(1);
        urlText.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.imageView_movie_image);
        findViewById(R.id.imageView_movie_image).setOnClickListener(this);
        findViewById(R.id.imageBtn_back).setOnClickListener(this);
        findViewById(R.id.imageBtn_save).setOnClickListener(this);
        ACTION_CASE = (ACTIONS) getIntent().getSerializableExtra("addingChoice");
        movie = getIntent().getParcelableExtra("movie");

    }

    //menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);}

    // menu option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //share the movie info
            case R.id.main_menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,movie.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


// on start : 1) checks if this is an existing movie - if TRUE calls for the loadMovie method. 2) calls for ImageDownLoadTask method.
    @Override
    protected void onStart() {
        super.onStart();
        if (ACTION_CASE == ACTIONS.ADD_FROM_WEB || ACTION_CASE == ACTIONS.EDIT) {
            loadMovie();
        }

        String picURL = urlText.getText().toString();
        new ImageDownloadTask().execute(picURL);
    }
// LaodMovie - Loads an existing movie to layout.
    private void loadMovie() {
        titleText.setText(movie.getMovieTitle());
        plotText.setText(movie.getMoviePlot());
        urlText.setText(movie.getMoviePicURL());
        ratingBar.setRating(movie.getMovieRating());
        Boolean isChecked = movie.getCheckBox();
        mCheckBox.setChecked(isChecked);
        movieId = movie.getId();
    }

// onClick - handles the buttons.
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case (R.id.imageBtn_save):
                saveMovie();
                finish();
                return;
            case (R.id.imageBtn_back):
                finish();
                return;
            case R.id.editText_pic_url:
                urlText.setMaxLines(10);
                break;
            case R.id.imageView_movie_image:

                if (mBitmap==null)return;
                Dialog posterDialog = new Dialog(this);
                posterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                posterDialog.setContentView(R.layout.image_view_layout);
                ImageView imageView = (ImageView)posterDialog.findViewById(R.id.imageView_dialog_view);
                imageView.setImageBitmap(mBitmap);
                posterDialog.show();
                break;
        }
    }

    // a method that adds a new movie to data base
    public void saveMovie() {
        String title = titleText.getText().toString();
        String plot = plotText.getText().toString();
        url = urlText.getText().toString();
        float rating = ratingBar.getRating();
        Boolean checkBox=mCheckBox.isChecked();
        if (ACTION_CASE == ACTIONS.ADD_FROM_WEB || ACTION_CASE == ACTIONS.ADD_MANUALLY) {
            if (title.equals("")){
                Toast.makeText(ManageMovieActivity.this,R.string.movie_content_alert, Toast.LENGTH_LONG).show();
            }else {
                helper.manageMovie(new Movie(0, title, plot, url, rating,checkBox), ACTIONS.ADD);
                return;
            }
        }

        if (ACTION_CASE == ACTIONS.EDIT) {

            helper.manageMovie(new Movie(movieId, title, plot, url, rating, checkBox),ACTIONS.EDIT);
            return;
        }

    }


    // image loading class
    public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {



        @Override
        protected Bitmap doInBackground(String... params) {


            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                // decode the byte stream from the internet into a Bitmap object
                mBitmap = BitmapFactory.decodeStream(connection.getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap==null) return;

            // set the Bitmap in the ImageView
            imageView.setImageBitmap(bitmap);
        }
    }

}
