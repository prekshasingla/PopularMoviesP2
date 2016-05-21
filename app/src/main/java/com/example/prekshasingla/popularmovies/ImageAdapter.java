package com.example.prekshasingla.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class ImageAdapter extends ArrayAdapter<Movie>
{
    public ArrayList<Movie> movieList;

    public ImageAdapter(Activity context, ArrayList<Movie> movieList) {
        super(context, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view, parent, false);
        }


        ImageView posterView = (ImageView) convertView.findViewById(R.id.imageView);
          Picasso.with(getContext()).load(movie.image).into(posterView);

        return convertView;
    }


}
