import org.json.JSONObject;

public class ViewMovieDetailsInteractor {

    private final OMDbApiClient apiClient;

    public ViewMovieDetailsInteractor(OMDbApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Movie getMovieDetails(String imdbID) throws Exception {
        JSONObject json = apiClient.getMovieDetails(imdbID);

        if (json == null || json.has("Error")) {
            return null;
        }

        Movie m = new Movie(
                json.optString("Title"),
                json.optString("Year"),
                json.optString("imdbID"),
                json.optString("Type"),
                json.optString("Poster")
        );

        m.plot = json.optString("Plot");
        m.director = json.optString("Director");
        m.genre = json.optString("Genre");
        m.runtime = json.optString("Runtime");
        m.rating = json.optString("imdbRating");

        return m;
    }
}

