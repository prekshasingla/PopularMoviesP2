package com.example.prekshasingla.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    private ImageAdapter moviesAdapter;
    private String sortType = "sort_by=popularity.desc";
    private ArrayList<Movie> currentMovieList = new ArrayList<>();

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Context context = getActivity();

        if (id == R.id.action_sort_popularity) {
            sortType = "sort_by=popularity.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }

        if (id == R.id.action_sort_rated) {
            sortType = "sort_by=vote_average.desc";
            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = getActivity();

            FetchMovies moviesTask = new FetchMovies();
            moviesTask.execute();



        //Initializes our custom adapter for the Gridview with the current Movie ArrayList data and fetches id's to identify Gridview
        moviesAdapter = new ImageAdapter(getActivity(), currentMovieList);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movieGridView);

        gridView.setAdapter(moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie currentMovie = moviesAdapter.getItem(position);
                Intent i = new Intent(getActivity(),MovieDetailActivity.class);

                i.putExtra("image",currentMovie.image);
                i.putExtra("original_title",currentMovie.original_title);
                i.putExtra("synopsis",currentMovie.synopsis);
                i.putExtra("user_rating",currentMovie.user_rating);
                i.putExtra("release_date",currentMovie.release_date);

                startActivity(i);

            }
        });
        return rootView;
    }

    public class FetchMovies extends AsyncTask<Void, Void, Movie[]> {
        private final String LOG_TAG = FetchMovies.class.getName();

        private Movie[] getMoviesDatafromJson(String moviesJsonString)
                throws JSONException {
            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_RATING = "vote_average";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            Movie[] resultObjects = new Movie[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                resultObjects[i] = new Movie("http://image.tmdb.org/t/p/w300/" + movieObject.getString(OWM_POSTER),movieObject.getString(OWM_ORIGINAL_TITLE),movieObject.getString(OWM_SYNOPSIS),movieObject.getString(OWM_RELEASE_DATE),movieObject.getString(OWM_RATING),movieObject.getString(OWM_ID));
            }
            return resultObjects;
        }

        @Override
        protected Movie[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonString = null;

            try {

                URL url = new URL("http://api.themoviedb.org/3/discover/movie?" +sortType+  "&api_key=PutYourAPIKeyHere");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonString = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDatafromJson(moviesJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                Context context = getActivity();
                moviesAdapter.clear();
                moviesAdapter.addAll(result);
                moviesAdapter.notifyDataSetChanged();
            }
        }
    }
}
