package models;

import exceptions.MovieAlreadyFavoritedException;
import exceptions.PasswordsNotEqualException;
import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class User {
    /* Represents an individual user for the program.

    Attributes:
        UserID
        Password
        Favorites
        Rating

    provides methods that returns user specific things such as ratings given and favorited movies.
     * Saves all users + their passwords + favorites to a JSON file.
     *
     * @param file         where to save, e.g. Paths.get("users.json")
     * @param movieIdFn    function that converts a Movie → String ID to store

    Class invariants:
    - Starting favorite movie list is empty list
    - UserID ad
     */

    private String username;
    private String password;
    private final ArrayList<Movie> favorites;
    private static final Map<String, String> userPasswordMap = new HashMap<>();
    private static final Map<String, User> userMap = new HashMap<>();
    private List<Movie> searchHistory;

    public User(String username, String password) throws UsernameTakenException {
        if (userPasswordMap.containsKey(username)) {
            throw new UsernameTakenException();
        }

        this.username = username;
        this.password = password;
        this.favorites = new ArrayList<>();
        this.searchHistory = new ArrayList<>();
        userMap.put(username, this);
        userPasswordMap.put(username, password);
    }

    public String getUsername(){
        return this.username;
    }

    public static Map<String, String> getUserPasswordMap(){
        return userPasswordMap;
    }

    public static Map<String, User> getUserMap(){ return userMap;}

    public static boolean checkValidUser(String username) throws UserNotFoundException {
        if (userMap.isEmpty()){
            throw new UserNotFoundException();
        }
        return userMap.containsKey(username);
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changePassword(String password1, String password2) throws PasswordsNotEqualException {
        if (password1.equals(password2)){
            userPasswordMap.put(this.username, password1);
        }
        else{
            throw new PasswordsNotEqualException();
        }
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

    public void setSearchHistory(List<Movie> history) {
        this.searchHistory = history;
    }

    public List<Movie> getSearchHistory() {
        return new ArrayList<>(this.searchHistory);
    }



}
