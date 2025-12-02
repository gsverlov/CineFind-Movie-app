package controllers;

import apiservices.ApiConfig;
import apiservices.OMDbApiClient;
import apiservices.PineconeClient;
import models.Movie;
import searchlogic.EmbeddingGenerator;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchInteractor {

    private EmbeddingGenerator embeddingGenerator;
    private PineconeClient pineconeClient;
    private OMDbApiClient omdbClient;

    public AdvancedSearchInteractor() {
        // Initialize API clients with keys from config
        this.embeddingGenerator = new EmbeddingGenerator(ApiConfig.NOMIC_API_KEY);
        this.pineconeClient = new PineconeClient(
                ApiConfig.PINECONE_API_KEY,
                ApiConfig.PINECONE_HOST
        );
        this.omdbClient = new OMDbApiClient(ApiConfig.OMDB_API_KEY);
    }

    /**
     * Get the Pinecone client (kept for backward compatibility)
     */
    public PineconeClient getPineconeClient() {
        return pineconeClient;
    }

    /**
     * Search for movies based on user preferences using vector similarity
     * @param userPreferences Combined string of mood + themes from user
     * @return Array of matching movies with full details from OMDB
     */
    public Movie[] search(String userPreferences) {
        try {
            // Step 1: Create embedding for user query
            System.out.println("Creating embedding for user preferences...");
            float[] userEmbedding = embeddingGenerator.embedQuery(userPreferences);
            System.out.println("Embedding created: " + userEmbedding.length + " dimensions");

            // Step 2: Query Pinecone for similar movie embeddings
            System.out.println("Querying Pinecone for similar movies...");
            List<PineconeClient.PineconeMatch> matches = pineconeClient.query(userEmbedding, 10);
            System.out.println("Found " + matches.size() + " matches");

            // Step 3: Fetch full movie details from OMDB for each match
            List<Movie> movies = new ArrayList<>();
            for (PineconeClient.PineconeMatch match : matches) {
                JSONObject metadata = match.metadata;

                if (metadata != null) {
                    String title = metadata.optString("title", "Unknown");

                    try {
                        System.out.println("  → Fetching details for: " + title);

                        // Search OMDB by title to get the movie
                        String searchResponse = omdbClient.searchMovies(title);
                        JSONObject searchJson = new JSONObject(searchResponse);

                        if (searchJson.has("Search") && searchJson.getJSONArray("Search").length() > 0) {
                            // Get the first result's imdbID
                            JSONObject firstResult = searchJson.getJSONArray("Search").getJSONObject(0);
                            String imdbID = firstResult.optString("imdbID");

                            if (imdbID != null && !imdbID.isEmpty()) {
                                // Fetch full details using imdbID
                                JSONObject detailsJson = omdbClient.getMovieDetails(imdbID);

                                if (detailsJson != null && !detailsJson.has("Error")) {
                                    // Create models.Movie with full details
                                    Movie movie = new Movie(
                                            detailsJson.optString("Title"),
                                            detailsJson.optString("Year"),
                                            detailsJson.optString("imdbID"),
                                            detailsJson.optString("Type"),
                                            detailsJson.optString("Poster")
                                    );

                                    // Set additional fields
                                    movie.plot = detailsJson.optString("Plot");
                                    movie.director = detailsJson.optString("Director");
                                    movie.genre = detailsJson.optString("Genre");
                                    movie.runtime = detailsJson.optString("Runtime");
                                    movie.rating = detailsJson.optString("imdbRating");

                                    movies.add(movie);
                                    System.out.println("     ✓ " + movie.title + " (similarity: " +
                                            String.format("%.2f", match.score) + ")");
                                }
                            }
                        } else {
                            System.out.println("     ✗ Could not find on OMDB: " + title);
                        }

                        // Small delay to respect OMDB rate limits
                        Thread.sleep(200);

                    } catch (Exception e) {
                        System.err.println("     ✗ Error fetching details for " + title + ": " + e.getMessage());
                    }
                }
            }

            if (movies.isEmpty()) {
                System.out.println("No movies found with complete details");
            }

            return movies.toArray(new Movie[0]);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in advanced search: " + e.getMessage());

            // Provide helpful debugging info
            if (e.getMessage() != null) {
                if (e.getMessage().contains("401") || e.getMessage().contains("403")) {
                    System.err.println("→ Check your API keys in apiservices.ApiConfig.java");
                } else if (e.getMessage().contains("404")) {
                    System.err.println("→ Check your PINECONE_HOST in apiservices.ApiConfig.java");
                } else if (e.getMessage().contains("UnknownHost")) {
                    System.err.println("→ Verify PINECONE_HOST format: https://your-index.svc.environment.pinecone.io");
                }
            }

            return new Movie[0];
        }
    }
}