package com.example.prekshasingla.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public static final String TAG = MovieDetailActivityFragment.class.getSimpleName();
    //static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private ListView mListView;
    private DBAdapter dba;
    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dba= new DBAdapter(getActivity());

        final String id;
        final String image;
        final String original_title;
        final String synopsis;
        final String user_rating;
        final String release_date;

        Bundle arguments=getArguments();
        if(arguments!=null) {
            image = arguments.getString("image");
            original_title = arguments.getString("original_title");
            synopsis = arguments.getString("synopsis");
            user_rating = arguments.getString("user_rating");
            release_date = arguments.getString("release_date");
            id = arguments.getString("id");
        }

        else {
            Intent i = getActivity().getIntent();
            image = i.getStringExtra("image");
            original_title = i.getStringExtra("original_title");
            synopsis = i.getStringExtra("synopsis");
            user_rating = i.getStringExtra("user_rating");
            release_date = i.getStringExtra("release_date");
            id = i.getStringExtra("id");

        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView original_title_1 = (TextView) rootView.findViewById(R.id.movieTitle);
        ImageView image_1 = (ImageView) rootView.findViewById(R.id.moviePoster);
        TextView release_date_1 = (TextView) rootView.findViewById(R.id.release_date);
        TextView user_rating_1 = (TextView) rootView.findViewById(R.id.user_rating);
        TextView synopsis_1 = (TextView) rootView.findViewById(R.id.synopsis);
        mListView = (ListView) rootView.findViewById(R.id.listview_trailer_review);

        mListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


       mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
       mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
      /*
       if(mTrailerAdapter!=null) {
            mListView.setAdapter(mTrailerAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trailer trailer = mTrailerAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(intent);
                }
            });
        }

        if(mReviewAdapter!=null)
        {
            mListView.setAdapter(mReviewAdapter);
        }
*/

        Button trailerbtn= (Button)rootView.findViewById(R.id.Trailers_btn);
        trailerbtn.performClick();
        trailerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setAdapter(mTrailerAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Trailer trailer = mTrailerAdapter.getItem(position);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                        startActivity(intent);
                    }
                });
                new FetchTrailersTask().execute(id);
                mReviewAdapter.clear();
            }
        });
        Button reviewbtn= (Button)rootView.findViewById(R.id.Reviews_btn);
        reviewbtn.performClick();
        reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setAdapter(mReviewAdapter);
                new FetchReviewsTask().execute(id);
                mTrailerAdapter.clear();
            }
        });


        CheckBox star=(CheckBox)rootView.findViewById(R.id.star);
        try{
            dba.open();
        }catch (SQLException e){
            Log.e("SqlException",e.toString());
        }
        int isFavourite=dba.isFavourite(id);
        dba.close();
        if(isFavourite==1)
            star.setChecked(true);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    try {
                        dba.open();
                    } catch (SQLException e) {
                        Log.e("SqlException", e.toString());
                    }
                    dba.updateFavourite(id, original_title, image, synopsis, user_rating, release_date);
                    dba.close();
                    Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        dba.open();
                    } catch (SQLException e) {
                        Log.e("SqlException", e.toString());
                    }
                    dba.removeFavourite(id);
                    dba.close();
                    Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Picasso.with(getContext()).load(image).into(image_1);
        original_title_1.setText(original_title);
        release_date_1.setText("Release Date: " + release_date);
        user_rating_1.setText("User Rating: " + user_rating);
        synopsis_1.setText(synopsis);

    return rootView;
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private List<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> results = new ArrayList<>();

            for(int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                // Only show Trailers which are on Youtube
                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailer trailerModel = new Trailer(trailer);
                    results.add(trailerModel);
                }
            }

            return results;
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
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
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null) {
                if (trailers.size() > 0) {

                    if (mTrailerAdapter != null) {
                        mTrailerAdapter.clear();
                        for (Trailer trailer : trailers) {
                            mTrailerAdapter.add(trailer);
                        }
                    }

                   // mTrailer = trailers.get(0);
                    //if (mShareActionProvider != null) {
                     //   mShareActionProvider.setShareIntent(createShareMovieIntent());
                    //}
                }
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private List<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Review> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Review(review));
            }

            return results;
        }

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
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
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null) {
                if (reviews.size() > 0) {

                    if (mReviewAdapter != null) {
                        mReviewAdapter.clear();
                        for (Review review : reviews) {
                            mReviewAdapter.add(review);
                        }
                    }
                }
            }
        }
    }
}
