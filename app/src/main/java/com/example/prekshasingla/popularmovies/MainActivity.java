package com.example.prekshasingla.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {

            if (findViewById(R.id.container) != null) {
                mTwoPane = true;
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new MovieDetailActivityFragment(),
                                    MovieDetailActivityFragment.TAG)
                            .commit();
                }
            } else {
                mTwoPane = false;
            }
        }
        else
        {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected (Movie currentMovie){
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString("image",currentMovie.image);
            arguments.putString("original_title", currentMovie.original_title);
            arguments.putString("synopsis", currentMovie.synopsis);
            arguments.putString("user_rating",currentMovie.user_rating);
            arguments.putString("release_date",currentMovie.release_date);
            arguments.putString("id",currentMovie.id);

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, MovieDetailActivityFragment.TAG)
                    .commit();
        } else {
            Intent i = new Intent(this, MovieDetailActivity.class);
            i.putExtra("image",currentMovie.image);
            i.putExtra("original_title",currentMovie.original_title);
            i.putExtra("synopsis",currentMovie.synopsis);
            i.putExtra("user_rating",currentMovie.user_rating);
            i.putExtra("release_date",currentMovie.release_date);
            i.putExtra("id",currentMovie.id);

            startActivity(i);

        }

    }

}

