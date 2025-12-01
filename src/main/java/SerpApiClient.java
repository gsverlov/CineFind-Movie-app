import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SerpApiClient {
    private final String apiKey;
    private static final String BASE_URL = "https://serpapi.com/search";

    public SerpApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Search Google and return the first organic result URL
     * @param query The search query (e.g., movie title)
     * @return The first result URL, or null if no results
     */
    public String searchFirstUrl(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String urlString = BASE_URL + "?q=" + encodedQuery + "&api_key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("SerpAPI error: " + responseCode);
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        // Parse response
        JSONObject jsonResponse = new JSONObject(response.toString());

        // Get organic results
        if (jsonResponse.has("organic_results")) {
            JSONArray organicResults = jsonResponse.getJSONArray("organic_results");
            if (!organicResults.isEmpty()) {
                JSONObject firstResult = organicResults.getJSONObject(0);
                return firstResult.getString("link");
            }
        }

        return null;
    }

    /**
     * Search for a movie and return details
     */
    public SearchResult searchMovie(String movieTitle) throws Exception {
        String firstUrl = searchFirstUrl(movieTitle + " movie");

        if (firstUrl == null) {
            return null;
        }

        return new SearchResult(movieTitle, firstUrl);
    }

    public static class SearchResult {
        public String movieTitle;
        public String url;

        public SearchResult(String movieTitle, String url) {
            this.movieTitle = movieTitle;
            this.url = url;
        }
    }
}