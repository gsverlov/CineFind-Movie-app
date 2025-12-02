package DataPersistence;

import exceptions.MovieAlreadyFavoritedException;
import exceptions.UsernameTakenException;
import models.Movie;
import models.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ExtractData {
    private static final Path USER_FILE = Path.of("data/users.json");

    /**
     * Save all user data into JSON.
     */
    public static void saveToJson(Path userFile)
            throws IOException {

        JSONObject root = new JSONObject();
        // Loop over all users in system
        for (Map.Entry<String, User> entry : User.getUserMap().entrySet()) {
            String username = entry.getKey();
            User user = entry.getValue();

            JSONObject jUser = new JSONObject();
            jUser.put("password", User.getUserPasswordMap().get(username));

            // Favorites
            JSONArray favs = new JSONArray();
            for (Movie m : user.getFavorites()) {
                JSONObject jMovie = new JSONObject();
                jMovie.put("title", m.title);
                jMovie.put("year", m.year);
                jMovie.put("imbdID",m.imdbID);
                jMovie.put("type", m.type);
                jMovie.put("poster",m.poster);
                favs.put(jMovie);
            }
            jUser.put("favorites", favs);
            root.put(username, jUser);
        }

        Files.createDirectories(userFile.getParent());
        Files.writeString(userFile, root.toString(2));
    }


    /**
     * Load user data from JSON.
     */

    public static void loadFromJson(Path file)
            throws IOException, UsernameTakenException {

        if (!Files.exists(file)) {
            return;
        }

        String content = Files.readString(file);
        if (content.isBlank()) {
            return;
        }

        JSONObject root = new JSONObject(content);

        // Clear existing in-memory users
        User.getUserPasswordMap().clear();
        User.getUserMap().clear();

        for (String username : root.keySet()) {
            JSONObject jUser = root.getJSONObject(username);
            String password = jUser.getString("password");

            // Recreate user (constructor fills maps)
            User user = new User(username, password);

            if (jUser.has("favorites")) {
                JSONArray favs = jUser.getJSONArray("favorites");
                for (int i = 0; i < favs.length(); i++) {
                    JSONObject jMovie = favs.getJSONObject(i);
                    String title = jMovie.getString("title");
                    String year = jMovie.getString("year");
                    String imbdID = jMovie.getString("imbdID");
                    String type = jMovie.getString("type");
                    String poster = jMovie.getString("poster");
                    Movie movie = new Movie(title,year,imbdID, type, poster);
                        try {
                            user.favoriteMovie(movie);   // see below
                        } catch (MovieAlreadyFavoritedException e) {
                            return;
                        }
                    }
                }
            }
        }
    }
