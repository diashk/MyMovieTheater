package com.diana.mymovietheater;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.diana.mymovietheater.Enums.ACTIONS;
import com.diana.mymovietheater.Enums.DIALOGS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AlertDialog.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {


    private MovieDBHelper helper;
    private ItemMovieAdapter adapter;
    private static Boolean longChecklistner = false;
    private Movie movie;
    private static DIALOGS DIALOG_CASE; //listens to the Type of dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new MovieDBHelper(this);
        adapter = new ItemMovieAdapter(this, R.layout.item_movie_layout);
        ListView listView = (ListView) findViewById(R.id.listView_movies);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        findViewById(R.id.imageButton_add).setOnClickListener(this);
    }
// Creates an options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.main_menu_delete:
//calls to delete all dialog
                DIALOG_CASE = DIALOGS.DELETE_ALL;
                DialogBuilder();
                break;
//calls to exit app dialog
            case R.id.main_menu_exit:
                DIALOG_CASE = DIALOGS.EXIT_APP;
                DialogBuilder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
// We create the view list on start
    @Override
    protected void onStart() {
        super.onStart();
        adapter.clear();
        adapter.addAll(helper.getAllMovies());
    }
// A method for the add new button
    @Override
    public void onClick(View view) {

        DIALOG_CASE = DIALOGS.ADD_NEW;
        DialogBuilder();
    }
// All dialogs are built in this method
    private AlertDialog DialogBuilder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (DIALOG_CASE) {
//add new item dialog
            case ADD_NEW:
                builder.setPositiveButton(R.string.manually, this);
                builder.setNegativeButton(R.string.from_web, (this));
                builder.setMessage(R.string.adding_option_query);
                builder.setTitle(R.string.add_new_dialog_title);

                break;
// delete one dialog
            case DELETE_ONE:
                builder.setPositiveButton(R.string.delete, this);
                builder.setNegativeButton(R.string.edit, (this));
                builder.setMessage(R.string.delete_movie_dialog_query);
                builder.setTitle(R.string.delete_movie_dialog_title);

                break;
//delete all dialog
            case DELETE_ALL:
                builder.setPositiveButton(R.string.delete, this);
                builder.setMessage(R.string.delete_all_alert);
                builder.setTitle(R.string.delete_all_dialog_title);
                break;

 //exit app dialog
            case EXIT_APP:
                builder.setPositiveButton(R.string.string_exit_app, this);
                builder.setMessage(R.string.exit_option_query);
                builder.setTitle(R.string.extit_app_dialog_title);
                break;
        }

//cancel option for all dialogs
        builder.setNeutralButton(R.string.string_cancel, this);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;

    }

// A method that handles varies clicks on the dialogs buttons
    @Override
    public void onClick(DialogInterface dialogInterface, int button_choice) {
// cancels the action on all dialogs end leaves the method
        if (button_choice==DialogInterface.BUTTON_NEUTRAL){
            Toast.makeText(MainActivity.this, R.string.string_action_canceled, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (DIALOG_CASE) {
            case ADD_NEW:
                switch (button_choice) {
                    case (DialogInterface.BUTTON_POSITIVE):// manually
                        Intent intent_manual_add = new Intent(this, ManageMovieActivity.class);
                        intent_manual_add.putExtra("addingChoice", ACTIONS.ADD_MANUALLY);
                        startActivity(intent_manual_add);
                        break;
                    case (DialogInterface.BUTTON_NEGATIVE): // from web
                        Intent intent_web_add = new Intent(this, AddFromWebActivity.class);
                        startActivity(intent_web_add);
                        break;
                }
                break;
            case DELETE_ONE:
                switch (button_choice) {
                    case (DialogInterface.BUTTON_POSITIVE)://delete
                        helper.deleteMovie(movie);
                        longChecklistner = true;
                        onStart();
                        break;
                    case (DialogInterface.BUTTON_NEGATIVE)://edit
                        editMovie(movie);
                        longChecklistner = true;
                        break;}
                break;
            case DELETE_ALL:
                helper.deleteAllMovies();
                Toast.makeText(MainActivity.this, R.string.all_movies_deleted, Toast.LENGTH_SHORT).show();
                onStart();
                break;
            case EXIT_APP:
                Toast.makeText(MainActivity.this, R.string.see_you_later, Toast.LENGTH_SHORT).show();
                finish();

                break;
        }
    }
// A method for the click on  an item.Calls for the edit movie method and sends it a selected movie.
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

// checks if there were a long click before the click. If not, calls the edit movie method.
        if (longChecklistner == false) {
            movie = adapter.getItem(i);
            editMovie(movie);
        } else longChecklistner = false;
    }
// A method that calls for Manage movie activity by intents and sends a chosen movie
    public void editMovie(Movie movie) {
        Intent edit_movie = new Intent(this, ManageMovieActivity.class);
        edit_movie.putExtra("addingChoice", ACTIONS.EDIT);
        edit_movie.putExtra("movie", movie);
        startActivity(edit_movie);
    }
// A method for the long click on item. Calls for the delete all dialog.
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        movie = adapter.getItem(i);
        DIALOG_CASE = DIALOGS.DELETE_ONE;
        DialogBuilder();
        return true;
    }

}
