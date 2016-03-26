package com.example.prekshasingla.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent i=getActivity().getIntent();
        String image=i.getStringExtra("image");
        String original_title=i.getStringExtra("original_title");
        String synopsis=i.getStringExtra("synopsis");
        String user_rating=i.getStringExtra("user_rating");
        String release_date=i.getStringExtra("release_date");
        
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);


        TextView original_title_1 = (TextView) rootView.findViewById(R.id.movieTitle);
        ImageView image_1 = (ImageView) rootView.findViewById(R.id.moviePoster);
        TextView release_date_1 = (TextView) rootView.findViewById(R.id.release_date);
        TextView user_rating_1 = (TextView) rootView.findViewById(R.id.user_rating);
        TextView synopsis_1 = (TextView) rootView.findViewById(R.id.synopsis);


        if (image.isEmpty()) {
          }
        else {
            Picasso.with(getActivity()).load(image).into(image_1);
        }

        if (original_title.isEmpty()) {
            original_title_1.setText("No Title Available");
        } else {
            original_title_1.setText(original_title);
        }

        if (release_date.isEmpty()) {
            release_date_1.setText("No Release Date Available");
        } else {
            release_date_1.setText("Release Date: "  + release_date);
        }

        if (user_rating.isEmpty()) {
            user_rating_1.setText("No User Rating Available");
        } else {
            user_rating_1.setText("User Rating: " + user_rating);
        }

        if (synopsis == null) {
            synopsis_1.setText("No Synopsis Available");
        } else {
            synopsis_1.setText(synopsis);
        }

        return rootView;
    }
}
