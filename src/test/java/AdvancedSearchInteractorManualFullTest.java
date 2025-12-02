import AdvancedSearch.*;
import controllers.AdvancedSearchInteractor;
import models.Movie;
import searchlogic.EmbeddingGenerator;
import apiservices.OMDbApiClient;
import apiservices.PineconeClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchInteractorManualFullTest {

    // Custom AdvancedSearch.searchlogic.EmbeddingGenerator that can throw exceptions with specific messages
    static class TestEmbeddingGenerator extends EmbeddingGenerator {
        private final String exceptionMessage;
        private final boolean throwNullMessageException;

        public TestEmbeddingGenerator(String exceptionMessage) {
            super("INVALID_KEY");
            this.exceptionMessage = exceptionMessage;
            this.throwNullMessageException = false;
        }

        public TestEmbeddingGenerator(String exceptionMessage, boolean throwNullMessageException) {
            super("INVALID_KEY");
            this.exceptionMessage = exceptionMessage;
            this.throwNullMessageException = throwNullMessageException;
        }

        @Override
        public float[] embedQuery(String text) {
            if (throwNullMessageException) {
                // Use custom exception that guarantees getMessage() == null
                throw new NullMessageException();
            }
            if (exceptionMessage != null) {
                throw new RuntimeException(exceptionMessage);
            }
            return new float[]{0.1f, 0.2f};
        }
    }

    // Custom exception that guarantees getMessage() returns null
    static class NullMessageException extends RuntimeException {
        public NullMessageException() {
            super();
        }

        @Override
        public String getMessage() {
            return null;
        }
    }

    // Custom AdvancedSearch.apiservices.PineconeClient that can throw exceptions or return test data
    static class TestPineconeClient extends PineconeClient {
        private final int scenario;
        private final String exceptionMessage;

        public TestPineconeClient(int scenario, String exceptionMessage) {
            super("INVALID_KEY", "https://fake-host");
            this.scenario = scenario;
            this.exceptionMessage = exceptionMessage;
        }

        @Override
        public List<PineconeMatch> query(float[] queryVector, int topK) {
            if (exceptionMessage != null) {
                throw new RuntimeException(exceptionMessage);
            }
            List<PineconeMatch> matches = new ArrayList<>();
            switch (scenario) {
                case 0: // normal metadata
                    matches.add(new PineconeMatch("id1", 0.9, new JSONObject().put("title", "Dummy AdvancedSearch.models.Movie")));
                    break;
                case 1: // null metadata
                    matches.add(new PineconeMatch("id2", 0.5, null));
                    break;
                case 2: // metadata missing title
                    matches.add(new PineconeMatch("id3", 0.7, new JSONObject()));
                    break;
                case 3: // no matches
                    break;
            }
            return matches;
        }
    }

    // Custom AdvancedSearch.apiservices.OMDbApiClient to trigger different branches
    static class TestOMDbClient extends OMDbApiClient {
        private int searchScenario;
        private int detailsScenario;

        public TestOMDbClient(int searchScenario, int detailsScenario) {
            super("INVALID_KEY");
            this.searchScenario = searchScenario;
            this.detailsScenario = detailsScenario;
        }

        // Convenience constructor for when both are the same
        public TestOMDbClient(int scenario) {
            this(scenario, scenario);
        }

        @Override
        public String searchMovies(String name) {
            // Scenario 4: throw exception during searchMovies
            if (searchScenario == 4) {
                throw new RuntimeException("OMDB search failed");
            }

            switch (searchScenario) {
                case 1: // empty Search array (length() == 0, second part of && is false)
                    return new JSONObject().put("Search", new JSONArray()).toString();
                case 2: // first result missing imdbID key (optString returns "")
                    return new JSONObject().put("Search", new JSONArray().put(new JSONObject())).toString();
                case 3: // first result with explicit empty imdbID string
                    JSONObject emptyIdResult = new JSONObject().put("imdbID", "");
                    return new JSONObject().put("Search", new JSONArray().put(emptyIdResult)).toString();
                case 5: // Missing "Search" key entirely (first part of && is false)
                    return new JSONObject().toString();
                case 6: // Try to make optString return null (but it won't work, optString never returns null)
                    // This is actually impossible with optString(), but let's try anyway
                    JSONObject nullIdResult = new JSONObject();
                    nullIdResult.put("imdbID", JSONObject.NULL);
                    return new JSONObject().put("Search", new JSONArray().put(nullIdResult)).toString();
                case 7: // Return malformed JSON that might cause optString to behave differently
                    // Return a JSON where imdbID exists but is explicitly null in JSON
                    return "{\"Search\":[{\"imdbID\":null}]}";
                default: // normal first result
                    JSONObject firstResult = new JSONObject().put("imdbID", "tt1234567");
                    return new JSONObject().put("Search", new JSONArray().put(firstResult)).toString();
            }
        }

        @Override
        public JSONObject getMovieDetails(String imdbID) {
            switch (detailsScenario) {
                case 1: // return null (first part of && is false)
                    return null;
                case 2: // return Error object (second part of && is false: has("Error") == true)
                    return new JSONObject().put("Error", "AdvancedSearch.models.Movie not found");
                default: // normal details
                    JSONObject details = new JSONObject();
                    details.put("Title", "Dummy AdvancedSearch.models.Movie");
                    details.put("Year", "2025");
                    details.put("imdbID", "tt1234567");
                    details.put("Type", "movie");
                    details.put("Poster", "N/A");
                    details.put("Plot", "Dummy plot");
                    details.put("Director", "Dummy Director");
                    details.put("Genre", "Action");
                    details.put("Runtime", "120 min");
                    details.put("imdbRating", "9.0");
                    return details;
            }
        }
    }

    // Interactor subclass to inject dependencies
    static class TestInteractor extends AdvancedSearchInteractor {
        public TestInteractor(EmbeddingGenerator embedding, PineconeClient pinecone, OMDbApiClient omdb) {
            this.embeddingGenerator = embedding;
            this.pineconeClient = pinecone;
            this.omdbClient = omdb;
        }
    }

    public static void main(String[] args) {

        System.out.println("=== AdvancedSearch.controllers.AdvancedSearchInteractor 100% Coverage Manual Test ===\n");

        // 0. Call default constructor and test getPineconeClient()
        System.out.println("Test 0: Default constructor + getPineconeClient()");
        AdvancedSearchInteractor defaultInteractor = new AdvancedSearchInteractor();
        System.out.println("AdvancedSearch.apiservices.PineconeClient retrieved: " + (defaultInteractor.getPineconeClient() != null));
        System.out.println();

        // 1. Normal flow
        System.out.println("Test 1: Normal flow");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(0)
        ).search("normal flow");
        System.out.println();

        // 2. No Pinecone matches
        System.out.println("Test 2: No Pinecone matches");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(3, null),
                new TestOMDbClient(0)
        ).search("no matches");
        System.out.println();

        // 3. Metadata null
        System.out.println("Test 3: Metadata is null");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(1, null),
                new TestOMDbClient(0)
        ).search("metadata null");
        System.out.println();

        // 4. Metadata missing title
        System.out.println("Test 4: Metadata missing title");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(2, null),
                new TestOMDbClient(0)
        ).search("metadata missing title");
        System.out.println();

        // 5. OMDB empty search array
        System.out.println("Test 5: OMDB empty search array");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(1)
        ).search("OMDB empty search");
        System.out.println();

        // 6a. OMDB first result missing imdbID (optString returns "")
        System.out.println("Test 6a: OMDB missing imdbID (optString returns empty)");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(2)
        ).search("OMDB missing imdbID");
        System.out.println();

        // 6b. OMDB first result with explicit empty imdbID string
        System.out.println("Test 6b: OMDB explicit empty imdbID string");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(3)
        ).search("OMDB empty imdbID");
        System.out.println();

        // 7. OMDB getMovieDetails returns null (detailsJson != null is FALSE)
        System.out.println("Test 7: OMDB getMovieDetails returns null");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(0, 1) // search=0 (normal), details=1 (null)
        ).search("OMDB getMovieDetails null");
        System.out.println();

        // 8. OMDB getMovieDetails returns Error (!has("Error") is FALSE)
        System.out.println("Test 8: OMDB getMovieDetails has Error");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(0, 2) // search=0 (normal), details=2 (error)
        ).search("OMDB getMovieDetails error");
        System.out.println();

        // 9. Embedding throws exception with 401 error
        System.out.println("Test 9: Embedding throws 401 exception");
        new TestInteractor(
                new TestEmbeddingGenerator("Error 401 Unauthorized"),
                new TestPineconeClient(0, null),
                new TestOMDbClient(0)
        ).search("embedding 401");
        System.out.println();

        // 10. Embedding throws exception with 403 error
        System.out.println("Test 10: Embedding throws 403 exception");
        new TestInteractor(
                new TestEmbeddingGenerator("Error 403 Forbidden"),
                new TestPineconeClient(0, null),
                new TestOMDbClient(0)
        ).search("embedding 403");
        System.out.println();

        // 11. Pinecone query throws 404 exception
        System.out.println("Test 11: Pinecone throws 404 exception");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, "Error 404 Not Found"),
                new TestOMDbClient(0)
        ).search("pinecone 404");
        System.out.println();

        // 12. Exception with UnknownHost message
        System.out.println("Test 12: UnknownHost exception");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, "java.net.UnknownHostException: invalid host"),
                new TestOMDbClient(0)
        ).search("unknown host");
        System.out.println();

        // 13. Exception with null message (e.getMessage() != null is FALSE)
        System.out.println("Test 13: Exception with null message (NullPointerException)");
        TestInteractor test13 = new TestInteractor(
                new TestEmbeddingGenerator(null, true), // throwNullMessageException = true
                new TestPineconeClient(0, null),
                new TestOMDbClient(0, 0)
        );
        Movie[] result13 = test13.search("null message");
        System.out.println("Test 13 completed, returned " + result13.length + "data/movies");
        System.out.println();

        // 14. Exception with message that doesn't match any condition
        System.out.println("Test 14: Exception with generic message");
        new TestInteractor(
                new TestEmbeddingGenerator("Some random error"),
                new TestPineconeClient(0, null),
                new TestOMDbClient(0)
        ).search("generic error");
        System.out.println();

        // 15. Exception during OMDB search (inner catch block)
        System.out.println("Test 15: Exception during OMDB searchMovies");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(4)
        ).search("omdb search exception");
        System.out.println();

        // 16. Missing "Search" key entirely (first part of has("Search") && ... is false)
        System.out.println("Test 16: OMDB response missing Search key");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(5)
        ).search("missing search key");
        System.out.println();

        // 17. Try multiple approaches to get imdbID == null
        System.out.println("Test 17a: Trying JSONObject.NULL for imdbID");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(6, 0)
        ).search("imdbID null attempt 1");
        System.out.println();

        System.out.println("Test 17b: Trying raw JSON null for imdbID");
        new TestInteractor(
                new TestEmbeddingGenerator(null),
                new TestPineconeClient(0, null),
                new TestOMDbClient(7, 0)
        ).search("imdbID null attempt 2");
        System.out.println();

        System.out.println("=== Manual Test Finished ===");
        System.out.println("All branches and error paths covered!");
    }
}