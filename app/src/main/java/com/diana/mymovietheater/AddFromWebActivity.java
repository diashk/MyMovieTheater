package com.diana.mymovietheater;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.diana.mymovietheater.Enums.ACTIONS;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AddFromWebActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * This Activity searches for the movie title typed by the user from http://www.omdbapi.com, brings up to 10 movies found containing the typed title.
     * When the user chooses an item from the list the chosen item is sent to the "ManageMovieActivity" by intent.
     */

    public static final String BY_NAME = "s", BY_ID = "i"; // Flags for what kind of search we want to preform (this is a local flag that why I didn't use ENUMs)
    private String movieName;
    private EditText movie_search_editText;
    private MovieAdapter adapter;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_from_web);

        movie_search_editText = (EditText) findViewById(R.id.editText_web_search);
        findViewById(R.id.image_button_bring_from_web).setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View view) {

// This if checks if the user inserted data to edit text before preforming a search. If not it will not continue.
        if (movie_search_editText.length() == 0) {
            Toast.makeText(this, "Please enter movie name", Toast.LENGTH_SHORT).show();
            return;
        }
//We set an adapter to the list view and set an click listener.
        adapter = new MovieAdapter(this, R.layout.web_search_movie_layout);
        ListView list = (ListView) findViewById(R.id.list_view_bring_from_web);
        list.setAdapter(adapter);
        movie_search_editText = (EditText) findViewById(R.id.editText_web_search);
        list.setOnItemClickListener(this);

//We get the movie name and replace the blank spaces whit "%20".
        movieName = movie_search_editText.getText().toString();
        movieName = movieName.replace(" ", "%20");

// After the user clicks search button , we call the FindMovie method whit the find movie by TITLE flag to find the movies containing the DATA the user entered.
        new FindMovie().execute(BY_NAME, movieName);


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

// After the user chooses an item, we get the specific imdb id of the selected item and call the FindMovie method whit the find movie by ID flag to find an specific movie.
        Movie m = adapter.getItem(i);
        String imdbID = m.getImdbID();
        new FindMovie().execute(BY_ID, imdbID);


    }


    public class FindMovie extends AsyncTask<String, Void, ArrayList<Movie>> {

        // This class preforms the search. It runs in the background.

        private static final String BASE_URL = "http://www.omdbapi.com/?%s=%s";
        private static final String SEARCH = "Search";
        private  String   SEARCH_OPT;
        private Movie selectedMovie;



        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
// This method searches the movies at the selected url and returns the array list whit the found movies. The api provides a max of 10 movies.
            SEARCH_OPT = params[0];
            client.connect();
            Action viewAction = Action.newAction(Action.TYPE_VIEW,"AddFromWeb Page",Uri.parse("android-app://com.diana.mymovietheater/http/host/path"));
            AppIndex.AppIndexApi.start(client, viewAction);


            HttpURLConnection connection;
            BufferedReader reader;
            StringBuilder builder = new StringBuilder();
            ArrayList<Movie> data = new ArrayList<>();

            //Formats the url whit the inserted DATA and search choice.
            String finalUrl = String.format(BASE_URL, params[0], params[1]);


            try {
                URL url = new URL(finalUrl);

                connection = (HttpURLConnection) url.openConnection();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }

                JSONObject root = new JSONObject(builder.toString());

                if (params[0] == BY_NAME) {
                    JSONArray list = root.getJSONArray(SEARCH);
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject main = list.getJSONObject(i);


                        String title = main.getString("Title");
                        String picUrl = main.getString("Poster");
                        String imdbID = main.getString("imdbID");


                        data.add(new Movie(imdbID, title, picUrl,false));
                    }
                }
                if (params[0] == BY_ID) {

                    String title = root.getString("Title");
                    Log.e("title =", title);
                    String plot = root.getString("Plot");
                    String urlPic = root.getString("Poster");

                    float rating;

                  //Catches "N/A" rating. In that case inserts 0 to rating.
                    try {rating = Float.parseFloat(root.getString("imdbRating"));}

                    catch (RuntimeException e){
                        rating =0;
                    }

                    data.add(new Movie(0, title, plot, urlPic, rating / 2,false));

                    return data;

                }

                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> temps) {

            super.onPostExecute(temps);
            if (temps == null) {

                Toast.makeText(AddFromWebActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                Toast.makeText(AddFromWebActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                return;
            }

            // Checks if we made the search by name. If TRUE creates the list view.
            if (SEARCH_OPT==BY_NAME) {
                adapter.clear();
                adapter.addAll(temps);
                return;
            }

            // Checks if we made the search by ID. If TRUE  calls the ManageMovieActivity by INTENT.
            if (SEARCH_OPT==BY_ID) {

                selectedMovie = temps.get(0);
                Intent in = new Intent(AddFromWebActivity.this, ManageMovieActivity.class);
                in.putExtra("addingChoice", ACTIONS.ADD_FROM_WEB);
                in.putExtra("movie", selectedMovie);
                startActivity(in);

                Action viewAction = Action.newAction(
                        Action.TYPE_VIEW,
                        "AddFromWeb Page",
                        Uri.parse("http://host/path"),
                        Uri.parse("android-app://com.diana.mymovietheater/http/host/path")
                );
                AppIndex.AppIndexApi.end(client, viewAction);
                client.disconnect();
                finish();
                return;
            }


        }
    }


}
