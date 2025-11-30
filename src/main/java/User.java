import java.util.ArrayList;
import java.util.Map;

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
    private static Map<String, String> userPassowrdMap;

    public User(String username, String password){
        if (userPassowrdMap.containsKey(username)) {

        }
        userPassowrdMap.put(username, password);
        this.username = username;
        this.password = password;
        this.favorites = new ArrayList<>();
    }

    public boolean check_login(String password){
        return userPassowrdMap.get(this.username) == password;
    }

    public void favoriteMovie(Movie movie){
        this.favorites.add(movie);
    }
}
