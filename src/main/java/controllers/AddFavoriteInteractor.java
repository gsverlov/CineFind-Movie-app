package controllers;

import models.Movie;
import models.User;
import exceptions.MovieAlreadyFavoritedException;

public class AddFavoriteInteractor {

    public boolean execute(User user, Movie movie) {
        if (user == null || movie == null) {
            System.out.println("Error: User or Movie is null");
            return false;
        }

        try {
            user.favoriteMovie(movie);
            System.out.println("Success: Movie added to favorites -> " + movie.title);
            return true;

        } catch (MovieAlreadyFavoritedException e) {
            System.out.println("Fail: Movie is already in favorites.");
            return false;
        }
    }
}