package searchlogic;

import apiservices.*;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MovieEmbeddingPipeline {

    private static final String PROCESSED_FILE = "processed_movies.txt";
    private static final int MAX_EMBEDDING_CHARS = 8000; // limit so I dont run out of tokens
    private static final int RETRY_DELAY_MS = 3000; // 3 seconds between movies

    public static void main(String[] args) {
        // Initialize clients
        SerpApiClient serpClient = new SerpApiClient(ApiConfig.SERPAPI_KEY);
        ZyteApiClient zyteClient = new ZyteApiClient(ApiConfig.ZYTE_API_KEY);
        NomicEmbeddingClient embeddingClient = new NomicEmbeddingClient(ApiConfig.NOMIC_API_KEY);
        PineconeClient pineconeClient = new PineconeClient(
                ApiConfig.PINECONE_API_KEY,
                ApiConfig.PINECONE_HOST
        );

        // Read movie list and already processed movies
        List<String> movieTitles = readMovieList();
        Set<String> processedMovies = readProcessedMovies();

        System.out.println("Total movies in list: " + movieTitles.size());
        System.out.println();

        int successCount = 0;
        int failCount = 0;
        int skippedCount = 0;

        for (int i = 0; i < movieTitles.size(); i++) {
            String movieTitle = movieTitles.get(i).trim();

            if (movieTitle.isEmpty()) {
                continue;
            }

            // Skip already processed movies
            if (processedMovies.contains(movieTitle)) {
                skippedCount++;
                continue;
            }

            System.out.println("[" + (i + 1) + "/" + movieTitles.size() + "] Processing: " + movieTitle);

            try {
                // Step 1: Search for movie URL
                System.out.println("Searching with SerpAPI");
                SerpApiClient.SearchResult searchResult = serpClient.searchMovie(movieTitle);

                if (searchResult == null || searchResult.url == null) {
                    System.out.println(" no results");
                    failCount++;
                    continue;
                }

                System.out.println("found" + searchResult.url);

                // Step 2: Scrape content
                System.out.println("Scraping with Zyte");
                String scrapedContent = zyteClient.scrapeUrl(searchResult.url);

                if (scrapedContent == null || scrapedContent.isEmpty()) {
                    System.out.println("no results");
                    failCount++;
                    continue;
                }

                System.out.println("scraped "+ scrapedContent.length() + " characters");

                // Step 3: Prepare text and create embedding
                System.out.println("Creating embedding");

                // go over ontent to prevent token limit issues
                String truncatedContent = scrapedContent.length() > MAX_EMBEDDING_CHARS
                        ? scrapedContent.substring(0, MAX_EMBEDDING_CHARS)
                        : scrapedContent;

                String textToEmbed = movieTitle + ". " + truncatedContent;
                float[] embedding = embeddingClient.createEmbedding(textToEmbed);

                System.out.println("Embedding created (dimension: " + embedding.length + ")");

                // Step 4: Store in Pinecone
                System.out.println("storing in pinecone");

                JSONObject metadata = new JSONObject();
                metadata.put("title", movieTitle);
                metadata.put("url", searchResult.url);
                metadata.put("content_preview", scrapedContent.substring(0, Math.min(200, scrapedContent.length())));
                metadata.put("content_length", scrapedContent.length());
                metadata.put("type", "movie");

                // Create unique, safe vector ID
                String vectorId = createVectorId(movieTitle);
                pineconeClient.upsertVector(vectorId, embedding, metadata);

                System.out.println("success" + movieTitle);

                // Mark as processed
                markAsProcessed(movieTitle);
                successCount++;

                // Rate limiting delay
                if (i < movieTitles.size() - 1) {
                    System.out.println();
                    Thread.sleep(RETRY_DELAY_MS);
                }

            } catch (Exception e) {
                System.err.println("error" + e.getMessage());
                e.printStackTrace();
                failCount++;

                // Add delay even on failure to avoid hammering APIs
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        System.out.println("\nFinished");
        System.out.println("Successfully processed: " + successCount);
        System.out.println("(already done): " + skippedCount);
        System.out.println("Failed: " + failCount);
        System.out.println("\nProcessed movies saved to: " + PROCESSED_FILE);
    }

    /**
     * Create a unique, safe vector ID from movie title
     */
    private static String createVectorId(String movieTitle) {
        String sanitized = movieTitle.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_")
                .substring(0, Math.min(50, movieTitle.length()));

        int hash = Math.abs(movieTitle.hashCode());
        return "movie_" + sanitized + "_" + hash;
    }

    /**
     * Read movie list from file
     */
    private static List<String> readMovieList() {
        List<String> movies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/movies.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String title = line.trim();
                if (!title.isEmpty() && !title.startsWith("#")) {
                    movies.add(title);
                }
            }
        } catch (Exception e) {
            System.err.println("error reading from movies.txt");

            // Fallback sample
            movies.add("The Shawshank Redemption");
            movies.add("The Godfather");
            movies.add("The Dark Knight");
        }

        return movies;
    }

    /**
     * Read list of already processed movies
     */
    private static Set<String> readProcessedMovies() {
        Set<String> processed = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PROCESSED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processed.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet - that's fine
        } catch (Exception e) {
            System.err.println("error reading from movies " + e.getMessage());
        }

        return processed;
    }

    /**
     * Mark a movie as processed
     */
    private static void markAsProcessed(String movieTitle) {
        try (FileWriter writer = new FileWriter(PROCESSED_FILE, true)) {
            writer.write(movieTitle + "\n");
        } catch (Exception e) {
            System.err.println("could not proccess " + e.getMessage());
        }
    }
}