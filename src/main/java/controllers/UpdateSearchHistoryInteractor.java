package controllers;

import models.Movie;
import models.User;
import java.util.ArrayList;
import java.util.List;

public class UpdateSearchHistoryInteractor {

    public List<Movie> execute(User user, Movie movie) {
        if (user == null || movie == null) {
            return new ArrayList<>();
        }

        ArrayList<Movie> history = user.getSearchHistory();

        history.removeIf(m -> m.imdbID.equals(movie.imdbID));

        history.add(0, movie);

        if (history.size() > 5) {
            history.remove(history.size() - 1);
        }

        user.setSearchHistory(history);

        return history;
    }
}