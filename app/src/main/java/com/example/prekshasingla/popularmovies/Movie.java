package com.example.prekshasingla.popularmovies;

public class Movie {
    String image;
    String original_title;
    String synopsis;
    String user_rating;
    String release_date;
    String id;
    int numOfParameters;

    public Movie() {
        super();
    }

    public Movie(String image, String original_title, String synopsis, String release_date, String user_rating, String id) {
        super();
        this.image = image;
        this.original_title = original_title;
        this.synopsis = synopsis;
        this.release_date = release_date;
        this.user_rating = user_rating;
        this.id = id;
        this.numOfParameters = 6;
    }


}
