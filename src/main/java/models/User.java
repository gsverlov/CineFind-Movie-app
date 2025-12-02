package models;

import exceptions.MovieAlreadyFavoritedException;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class User {
    /* Represents an individual user for the program.

    Attributes:
        UserID
        Password
        Favorites
        Rating

    provides methods that returns user specific things such as ratings given and favorited movies.

    Class invariants:
    - Starting favorite movie list is empty list
    - UserID ad
     */
    private String username;
    private String password;
    private ArrayList<Movie> favorites;
    private static Map<String, String> userPassowrdMap = new HashMap<>();
    private static Map<String, User> userMap = new HashMap<>();

    public User(String username, String password) throws UsernameTakenException {
        if (userPassowrdMap.containsKey(username)) {
            throw new UsernameTakenException();
        }

        this.username = username;
        this.password = password;
        this.favorites = new ArrayList<>();
        userMap.put(username, this);
        userPassowrdMap.put(username, password);
    }

    public String getUsername(){
        return this.username;
    }

    public static Map<String, String> getUserPasswordMap(){
        return userPassowrdMap;
    }

    public static boolean checkValidUser(String username) throws UserNotFoundException {
        if ( userMap == null || userMap.isEmpty()){
            throw new UserNotFoundException();
        }
        return userMap.containsKey(username);
    }

    public static User getUser(String username){
        return userMap.get(username);
    }

    public void favoriteMovie(Movie movie) throws MovieAlreadyFavoritedException {
        if(this.favorites.contains(movie)){
            throw new MovieAlreadyFavoritedException();
        }
        else{
            this.favorites.add(movie);
        }

    }

    public void unfavoriteMovie(Movie movie){
        this.favorites.remove(movie);
    }

    public List<Movie> getFavorites(){
        return new ArrayList<>(this.favorites);
    }
}
